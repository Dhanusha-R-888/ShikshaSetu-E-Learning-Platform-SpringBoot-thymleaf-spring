package org.dhanusha.Shiksha_Setu.MyService;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import org.dhanusha.Shiksha_Setu.Model.Course;
import org.dhanusha.Shiksha_Setu.Model.EnrolledCourse;
import org.dhanusha.Shiksha_Setu.Model.EnrolledSection;
import org.dhanusha.Shiksha_Setu.Model.Learner;
import org.dhanusha.Shiksha_Setu.Model.QuizQuestion;
import org.dhanusha.Shiksha_Setu.Model.Section;
import org.dhanusha.Shiksha_Setu.MyRepository.CourseRepository;
import org.dhanusha.Shiksha_Setu.MyRepository.EnrolledCourseRepository;
import org.dhanusha.Shiksha_Setu.MyRepository.EnrolledSectionRepository;
import org.dhanusha.Shiksha_Setu.MyRepository.LearnerRepository;
import org.dhanusha.Shiksha_Setu.MyRepository.QuizQuestionRepository;
import org.dhanusha.Shiksha_Setu.MyRepository.SectionRepository;
import org.json.JSONObject;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.cloudinary.Cloudinary;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;

@Service
public class LearnerService {
	
	private final PasswordEncoder encoder;

	private final Cloudinary cloudinary;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	LearnerRepository learnerRepository;

	@Autowired
	ChatClient chatClient;

	@Autowired
	QuizQuestionRepository questionRepository;

	@Autowired
	EnrolledSectionRepository enrolledSectionRepository;

	@Autowired
	SectionRepository sectionRepository;

	@Autowired
	EnrolledCourseRepository enrolledCourseRepository;

	@Value("${razor-pay.api.key}")
	String key;
	@Value("${razor-pay.api.secret}")
	String secret;

	LearnerService(Cloudinary cloudinary, PasswordEncoder encoder) {
		this.cloudinary = cloudinary;
		this.encoder = encoder;
	}

	public String loadHome(HttpSession session) {
		if (session.getAttribute("learner") != null) {
			return "learner-home.html";
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}

	public String viewCourses(HttpSession session, Model model) {
		if (session.getAttribute("learner") != null) {
			List<Course> courses = courseRepository.findByPublishedTrue();
			if (courses.isEmpty()) {
				session.setAttribute("fail", "No Courses Available");
				return "redirect:/learner/home";
			} else {
				model.addAttribute("courses", courses);
				return "available-courses.html";
			}
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}

	public String enrollCourse(HttpSession session, Long id, Model model) {
		if (session.getAttribute("learner") != null) {
			Learner learner = (Learner) session.getAttribute("learner");
			Course course = courseRepository.findById(id).get();

			if (course.isPaid()) {
				double amount = 199;
				try {
					RazorpayClient client = new RazorpayClient(key, secret);

					JSONObject object = new JSONObject();
					object.put("amount", amount * 100);
					object.put("currency", "INR");

					Order order = client.orders.create(object);
					String orderId = order.get("id");

					model.addAttribute("orderId", orderId);
					model.addAttribute("amount", amount * 100);
					model.addAttribute("currency", "INR");
					model.addAttribute("leaner", learner);
					model.addAttribute("key", key);
					model.addAttribute("path", "/learner/enroll-paidcourse/" + course.getId());

					return "payment.html";

				} catch (RazorpayException e) {
					e.printStackTrace();
					session.setAttribute("fail", "Something Went Wrong");
					return "redirect:/learner/home";
				}

			} else {

				List<Section> sections = sectionRepository.findByCourse(course);
				List<EnrolledSection> enrolledSections = new ArrayList<EnrolledSection>();
				for (Section section : sections) {
					EnrolledSection enrolledSection = new EnrolledSection();
					enrolledSection.setSection(section);
					enrolledSections.add(enrolledSection);
				}

				EnrolledCourse enrolledCourse = new EnrolledCourse();
				enrolledCourse.setCourse(course);
				enrolledCourse.setEnrolledSections(enrolledSections);

				learner.getEnrolledCourses().add(enrolledCourse);

				learnerRepository.save(learner);

				session.setAttribute("pass", "Courses Enrolled Success, Thanks " + learner.getName());
				session.setAttribute("learner", learnerRepository.findById(learner.getId()).get());
				return "redirect:/learner/home";
			}

		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}

	public String viewEnrolledCourses(HttpSession session, Model model) {
		if (session.getAttribute("learner") != null) {
			Learner learner = (Learner) session.getAttribute("learner");

			List<EnrolledCourse> enrolledCourses = learner.getEnrolledCourses();
			if (enrolledCourses.isEmpty()) {
				session.setAttribute("fail", "Not Enrolled for Any of the Courses");
				return "redirect:/learner/home";
			} else {
				model.addAttribute("enrolledCourses", enrolledCourses);
				return "view-enrolled-courses.html";
			}
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}

	public String viewEnrolledSections(HttpSession session, Long id, Model model) {
		if (session.getAttribute("learner") != null) {
			EnrolledCourse enrolledCourse = enrolledCourseRepository.findById(id).get();
			List<EnrolledSection> enrolledSections = enrolledCourse.getEnrolledSections();

			model.addAttribute("enrolledSections", enrolledSections);
			return "view-enrolled-sections.html";
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}

	public String viewVideo(HttpSession session, Long id, Model model) {
		if (session.getAttribute("learner") != null) {

			EnrolledSection section = enrolledSectionRepository.findById(id).get();
			section.setSectionCompleted(true);

			enrolledSectionRepository.save(section);

			String videoUrl = section.getSection().getVideoUrl();
			model.addAttribute("link", videoUrl);
			EnrolledCourse course = enrolledCourseRepository.findByEnrolledSections(section);
			model.addAttribute("id", course.getId());
			return "play-video.html";
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}

	public String loadSectionQuiz(Long id, HttpSession session, Model model) {
		if (session.getAttribute("learner") != null) {

			EnrolledSection section = enrolledSectionRepository.findById(id).get();

			if (!section.isSectionCompleted()) {
				EnrolledCourse course = enrolledCourseRepository.findByEnrolledSections(section);
				session.setAttribute("fail", "First Complete the Section to Take Quiz");
				return "redirect:/learner/view-enrolled-sections/" + course.getId();
			}
			List<QuizQuestion> questions = section.getSection().getQuizQuestions();
			model.addAttribute("questions", questions);
			model.addAttribute("id", id);

			return "section-quiz.html";
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}

	public String submitQuiz(Long id, HttpSession session, Map<String, String> quiz) {
		if (session.getAttribute("learner") != null) {
			EnrolledSection section = enrolledSectionRepository.findById(id).get();
			String prompt = "";
			for (String questionId : quiz.keySet()) {
				String question = questionRepository.findById(Long.parseLong(questionId)).get().getQuestion();
				String answer = quiz.get(questionId);
				prompt += ". question: " + question + ". answer: " + answer;
			}
			prompt += "Evaluate the following quiz. For each question, consider the given answer. Return ONLY the total score out of 100 (just a number).\n\n";
			String answer = chatClient.prompt(prompt).call().content();
			int score = Integer.parseInt(answer);
			if (score >= 75) {
				section.setSectionQuizCompleted(true);
				enrolledSectionRepository.save(section);
				session.setAttribute("pass", "Quiz Cleared Success");
			} else {
				session.setAttribute("fail", "Quiz did not Clear try again");
			}

			return "redirect:/learner/view-enrolled-sections/" + id;
		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}

	public String enrollPaidCourse(HttpSession session, Long id, Model model) {
		if (session.getAttribute("learner") != null) {
			Learner learner = (Learner) session.getAttribute("learner");
			Course course = courseRepository.findById(id).get();
			List<Section> sections = sectionRepository.findByCourse(course);
			List<EnrolledSection> enrolledSections = new ArrayList<EnrolledSection>();
			for (Section section : sections) {
				EnrolledSection enrolledSection = new EnrolledSection();
				enrolledSection.setSection(section);
				enrolledSections.add(enrolledSection);
			}

			EnrolledCourse enrolledCourse = new EnrolledCourse();
			enrolledCourse.setCourse(course);
			enrolledCourse.setEnrolledSections(enrolledSections);

			learner.getEnrolledCourses().add(enrolledCourse);

			learnerRepository.save(learner);

			session.setAttribute("pass", "Courses Enrolled Success, Thanks " + learner.getName());
			session.setAttribute("learner", learnerRepository.findById(learner.getId()).get());
			return "redirect:/learner/home";

		} else {
			session.setAttribute("fail", "Invalid Session, Login First");
			return "redirect:/login";
		}
	}

	
		
}
