package hong.postService.web.file.v2;

import hong.postService.exception.ErrorResponse;
import hong.postService.service.fileService.dto.DownloadUrlResponse;
import hong.postService.service.fileService.v2.FileService;
import hong.postService.service.fileService.dto.UploadUrlRequest;
import hong.postService.service.fileService.dto.UploadUrlResponse;
import hong.postService.service.userDetailsService.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "file", description = "파일 관련 API")
@RestController
@RequestMapping("/v2/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "upload url 발급",
    description = "업로드 요청이 온 파일들에 대해 upload-url들을 발급해준다.")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "200", description = "발급 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 파일 업로드 요청",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/upload-urls")
    public ResponseEntity<UploadUrlResponse> issueUploadUrls(@Valid @RequestBody UploadUrlRequest request) {
        UploadUrlResponse uploadUrlResponse = fileService.getUploadUrls(request);
        return ResponseEntity.ok(uploadUrlResponse);
    }


    @Operation(summary = "download url 발급",
            description = "다운로드 요청이 온 파일들에 대해 download-url들을 발급해준다.")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "200", description = "발급 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 파일 다운로드 요청",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/{fileId}/download-url")
    public ResponseEntity<DownloadUrlResponse> issueDownloadUrl(@PathVariable("fileId") Long fileId,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        DownloadUrlResponse downloadUrl = fileService.getDownloadUrl(fileId, userDetails.getUserId());
        return ResponseEntity.ok(downloadUrl);
    }
}
