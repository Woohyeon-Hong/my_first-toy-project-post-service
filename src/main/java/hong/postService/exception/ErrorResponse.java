package hong.postService.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String errorCode, String message) {
        this(status, errorCode, message, LocalDateTime.now());
    }
}
