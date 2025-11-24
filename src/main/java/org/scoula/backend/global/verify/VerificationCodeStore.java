package org.scoula.backend.global.verify;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class VerificationCodeStore {

	private static class CodeInfo {
		String code;
		LocalDateTime expiresAt;
		boolean verified;

		CodeInfo(String code, LocalDateTime expiresAt) {
			this.code = code;
			this.expiresAt = expiresAt;
			this.verified = false;
		}
	}

	private final Map<String, CodeInfo> codeMap = new HashMap<>();
	private final Random random = new Random();

	/** 6자리 인증번호 생성 */
	public String generateCode(String email) {
		String code = String.format("%06d", random.nextInt(999999));
		codeMap.put(email, new CodeInfo(code, LocalDateTime.now().plusMinutes(5)));
		return code;
	}

	/** 인증 번호 검증 */
	public boolean verifyCode(String email, String code) {
		CodeInfo info = codeMap.get(email);
		if (info == null) return false;

		if (LocalDateTime.now().isAfter(info.expiresAt)) return false;

		if (!info.code.equals(code)) return false;

		// 검증 성공
		info.verified = true;
		return true;
	}

	/** 이메일이 인증된 상태인지 확인 */
	public boolean isVerified(String email) {
		CodeInfo info = codeMap.get(email);
		return info != null && info.verified;
	}

	/** 인증 기록 삭제 */
	public void clear(String email) {
		codeMap.remove(email);
	}
}
