package org.scoula.backend.domain.FamilyMember.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "family_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "family_id")
	private Integer familyId;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Gender gender;

	@Column(nullable = false)
	private LocalDate birth;

	@Column(nullable = false, unique = true)
	private String phone;

	@Column(name = "profile_image")
	private String profileImage;

	@Column(name = "is_leader")
	private Boolean isLeader = false;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	public enum Gender {
		M, F
	}
	@Column(nullable = false)
	private String password; // 🔹 비밀번호 (회원가입 시 해시 처리)

}
