package hong.postService.domain;

import hong.postService.domain.baseEntity.BaseTimeEntity;
import hong.postService.exception.file.FileNotFoundException;
import hong.postService.exception.file.InvalidFileFieldException;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class File extends BaseTimeEntity{

    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    @Column(nullable = false)
    private String originalFileName;
    @Column(nullable = false)
    private String storedFileName;
    @Column(nullable = false, unique = true)
    private String s3Key;

    @Column(name = "is_removed", nullable = false)
    private boolean isRemoved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public static String extractExtension(String fileName) {
        if (fileName == null) throw new InvalidFileFieldException("extractExtension: filename == null");
        int idx = fileName.lastIndexOf(".");
        if (idx == -1 || idx == fileName.length() - 1) throw new InvalidFileFieldException("extractExtension: 형식자가 잘못됐습니다.");
        return fileName.substring(idx);
    }

    public static String extractStoredFileName(String s3Key) {
        if (s3Key == null) {
            throw new InvalidFileFieldException("extractStoredFileName: s3Key == null");
        }

        String[] parts = s3Key.split("/");

        if (parts.length != 3 || !"post".equals(parts[0]) || parts[2].isBlank()) {
            throw new InvalidFileFieldException("extractStoredFileName: s3Key 형식이 잘못됐습니다. 형식: post/{postId}/{storedFileName}");
        }

        return parts[2]; // storedFileName
    }


    public static String generateStoredFileName(String originalFileName) {
        if (originalFileName == null) throw new InvalidFileFieldException("generateStoredFileName: originalFileName == null");
        return UUID.randomUUID().toString() + extractExtension(originalFileName);
    }

    public void generateS3Key() {
        checkNotRemoved();
        if (this.s3Key != null) throw new InvalidFileFieldException("registerS3Key: 이미 s3Key가 등록됐습니다.");

        this.s3Key = "post/" + post.getId() + "/" + this.storedFileName;
    }

    public void remove() {
        checkNotRemoved();

        storedFileName = "";
        this.isRemoved = true;
    }

    private void checkNotRemoved() {
        if (isRemoved()) throw new FileNotFoundException(this.getId());
    }
}
