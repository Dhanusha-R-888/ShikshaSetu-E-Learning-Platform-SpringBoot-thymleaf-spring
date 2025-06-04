package org.dhanusha.Shiksha_Setu.MyRepository;

import java.util.List;

import org.dhanusha.Shiksha_Setu.Model.Course;
import org.dhanusha.Shiksha_Setu.Model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
	List<Course> findByTutor(Tutor attribute);

}
