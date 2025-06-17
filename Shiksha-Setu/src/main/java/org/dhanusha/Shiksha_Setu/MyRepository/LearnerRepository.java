package org.dhanusha.Shiksha_Setu.MyRepository;

import java.util.List;

import org.dhanusha.Shiksha_Setu.Model.EnrolledCourse;
import org.dhanusha.Shiksha_Setu.Model.Learner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerRepository extends JpaRepository<Learner, Long> {
	boolean existsByMobile(long mobile);

	boolean existsByEmail(String email);

	Learner findByEmail(String email);
	
	List<Learner> findByEnrolledCoursesIn(List<EnrolledCourse> enrolledCourses);

}
