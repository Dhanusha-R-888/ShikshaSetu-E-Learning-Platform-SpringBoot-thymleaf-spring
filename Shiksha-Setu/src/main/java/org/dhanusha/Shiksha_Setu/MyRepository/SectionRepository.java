package org.dhanusha.Shiksha_Setu.MyRepository;

import java.util.List;

import org.dhanusha.Shiksha_Setu.Model.Course;
import org.dhanusha.Shiksha_Setu.Model.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long>{
	
	List<Section> findByCourse(Course course);

}
