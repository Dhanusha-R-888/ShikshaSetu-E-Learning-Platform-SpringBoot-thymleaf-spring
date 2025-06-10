package org.dhanusha.Shiksha_Setu.MyController;

import org.dhanusha.Shiksha_Setu.MyService.LearnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/learner")
public class LearnerController {
	
	@Autowired
	LearnerService learnerService;

	@GetMapping("/home")
	public String loadHome(HttpSession session) {
		return learnerService.loadHome(session);
	}
	
	@GetMapping("/view-courses")
	public String loadCourses(HttpSession session, Model model) {
		return learnerService.viewCourses(session, model);
	}

	@GetMapping("/enroll/{id}")
	public String enrollCourse(HttpSession session, @PathVariable Long id,Model model) {
		return learnerService.enrollCourse(session, id,model);
}
}
