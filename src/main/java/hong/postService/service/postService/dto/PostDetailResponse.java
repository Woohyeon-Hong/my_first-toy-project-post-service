package hong.postService.service.postService.dto;

import hong.postService.domain.File;
import hong.postService.domain.Post;
import hong.postService.service.fileService.dto.FileResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostDetailResponse {

    private Long id;
    private String title;
    private String content;
    private String writerNickname;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    private List<FileResponse> files;

    public static PostDetailResponse from(Post post) {

        ArrayList<FileResponse> fileResponses = new ArrayList<>();

        List<File> files = post.getFiles();
        for (File file : files) {
            if (!file.isRemoved()) {
                fileResponses.add(new FileResponse(file.getId(), file.getOriginalFileName()));
            }
        }

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getWriter().getNickname(),
                post.getCreatedDate(),
                post.getLastModifiedDate(),
                fileResponses
        );
    }
}


