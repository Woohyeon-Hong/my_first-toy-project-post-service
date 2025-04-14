package hong.postService.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "에러 응답 객체")
@Getter
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "HTTP 상태 코드", example = "400")
    private int status;

    @Schema(description = "에러 코드", example = "INVALID_MEMBER_FIELD")
    private String errorCode;

    @Schema(description = "메시지", example = "username은 필수입니다.")
    private String message;

    @Schema(description = "에러 발생 시간", example = "2025-04-14T20:33:13.693305")
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String errorCode, String message) {
        this(status, errorCode, message, LocalDateTime.now());
    }
}
