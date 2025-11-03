package org.scoula.backend.domain.FamilyMember.repository;

import java.util.Optional;

import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
	Optional<FamilyMember> findByEmail(String email);

}
