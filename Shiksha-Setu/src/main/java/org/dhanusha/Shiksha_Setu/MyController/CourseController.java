package org.dhanusha.Shiksha_Setu.MyController;

import org.dhanusha.Shiksha_Setu.Model.Course;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/courses")
public class CourseController {
	
	@GetMapping("/add")
	public String showAddCourseForm(Model model) {
	    model.addAttribute("course", new Course());
	    return "add-course"; // This should match your HTML filename (add-course.html or add-course.jsp)
	}

	@GetMapping("/manage")
	public String manageCourses() {
	    return "manage-courses"; // This should match manage-courses.html or JSP
	}


}
