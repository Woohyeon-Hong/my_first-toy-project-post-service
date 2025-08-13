package hong.postService.service.postService.dto;

import hong.postService.service.fileService.dto.FileCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostUpdateRequest {

    private String title;
    private String content;

    private List<FileCreateRequest> addFiles;
    private List<Long> removeFileIds;
}
