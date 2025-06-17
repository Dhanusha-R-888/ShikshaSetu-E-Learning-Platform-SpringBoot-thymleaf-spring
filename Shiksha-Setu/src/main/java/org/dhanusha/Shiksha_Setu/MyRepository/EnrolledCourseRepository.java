package org.dhanusha.Shiksha_Setu.MyRepository;

import java.util.List;

import org.dhanusha.Shiksha_Setu.Model.Course;
import org.dhanusha.Shiksha_Setu.Model.EnrolledCourse;
import org.dhanusha.Shiksha_Setu.Model.EnrolledSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrolledCourseRepository  extends JpaRepository<EnrolledCourse, Long>{
	
	EnrolledCourse findByEnrolledSections(EnrolledSection section);
	
	List<EnrolledCourse> findByCourseIn(List<Course> courses);

}
