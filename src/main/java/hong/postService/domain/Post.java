package hong.postService.domain;

import lombok.Data;

import java.time.LocalDateTime;


/**
 * ## Post (게시물)
 *
 * - id (Long) - DB에서 자동 생성
 * - title (String)
 * - content (String)
 * - ModifiedDate (Date)
 * - memberId (Long)
 */

@Data
public class Post {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime modifiedDate;
    private Long memberId;

    public Post() {
    }

    public Post(String title, String content, Long memberId) {
        this.title = title;
        this.content = content;
        this.memberId = memberId;
    }

    public Post(String title, String content, LocalDateTime modifiedDate, Long memberId) {
        this.title = title;
        this.content = content;
        this.modifiedDate = modifiedDate;
        this.memberId = memberId;
    }
}
