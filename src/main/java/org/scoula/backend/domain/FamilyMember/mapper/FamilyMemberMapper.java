package org.scoula.backend.domain.FamilyMember.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.springframework.data.repository.query.Param;

@Mapper
public interface FamilyMemberMapper {
	void insertMember(FamilyMember member);
	void updateLeaderStatus(@Param("memberId") long memberId, @Param("isLeader") boolean isLeader);
}
