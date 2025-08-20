package hong.postService.scheduler;

import hong.postService.service.fileService.v2.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilePurgeJob {

    private final FileService fileCleanupService;

    // 매 시간 정각 실행 예시
    @Scheduled(cron = "0 0 * * * *")
    public void run() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(2); // 유예 2일
        int total = 0;
        int batch;

        do {
            batch = fileCleanupService.purgeSoftDeletedFilesBefore(threshold, 200);
            total += batch;
        } while (batch > 0); // 더 이상 없을 때까지 페이징 루프
        log.info("purged {} soft-deleted files", total);
    }
}