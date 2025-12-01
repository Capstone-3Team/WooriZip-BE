package org.scoula.backend.domain.VideoAnswer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncShortsExecutor {

	private final PetShortsAsyncService petShortsAsyncService;

	@Async
	public void run(Long answerId) {
		log.info("🚀 비동기 숏츠 실행 시작! answerId={}", answerId);
		petShortsAsyncService.processPetShorts(answerId);
	}
}
