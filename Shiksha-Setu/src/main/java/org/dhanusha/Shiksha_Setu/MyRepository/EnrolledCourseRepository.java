package org.dhanusha.Shiksha_Setu.MyRepository;

import org.dhanusha.Shiksha_Setu.Model.EnrolledCourse;
import org.dhanusha.Shiksha_Setu.Model.EnrolledSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrolledCourseRepository  extends JpaRepository<EnrolledCourse, Long>{
	
	EnrolledCourse findByEnrolledSections(EnrolledSection section);

}
