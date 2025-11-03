package org.scoula.backend.domain.Family.repository;

import org.scoula.backend.domain.Family.domain.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FamilyRepository extends JpaRepository<Family, Integer> {
	Optional<Family> findByInviteCode(String inviteCode);
}
