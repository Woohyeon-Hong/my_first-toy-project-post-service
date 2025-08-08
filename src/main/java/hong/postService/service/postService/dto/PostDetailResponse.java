package hong.postService.service.postService.dto;

import hong.postService.domain.File;
import hong.postService.domain.Post;
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

    private List<String> fileNames;

    public static PostDetailResponse from(Post post) {

        ArrayList<String> fileNames = new ArrayList<>();

        List<File> files = post.getFiles();
        for (File file : files) {
            fileNames.add(file.getOriginalFileName());
        }

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getWriter().getNickname(),
                post.getCreatedDate(),
                post.getLastModifiedDate(),
                fileNames
        );
    }
}
