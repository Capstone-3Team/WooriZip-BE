package org.scoula.backend.domain.Family.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.backend.domain.Family.domain.Family;
import org.springframework.data.repository.query.Param;


@Mapper
public interface FamilyMapper {
	void insertFamily(Family family);
	Family findByInviteCode(String inviteCode);
	void updateLeaderMemberId(@Param("familyId") int familyId, @Param("leaderId") long leaderId);
}
