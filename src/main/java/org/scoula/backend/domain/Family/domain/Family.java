package org.scoula.backend.domain.Family.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "family")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Family {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String name; // 가족명

	@Column(name = "invite_code", nullable = false, unique = true)
	private String inviteCode; // 초대 코드

	@Column(name = "leader_member_id")
	private Long leaderMemberId; // 리더 ID

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();
}
