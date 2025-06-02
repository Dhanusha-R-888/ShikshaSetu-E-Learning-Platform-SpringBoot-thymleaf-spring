package org.dhanusha.Shiksha_Setu.MyRepository;

import org.dhanusha.Shiksha_Setu.Model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
	boolean existsByMobile(long mobile);

	boolean existsByEmail(String email);

	Tutor findByEmail(String email);
}
