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


//Static Utility 로직---------------------------------------------------------------------------------------------------

    public static String extractExtension(String fileName) {
        validateOriginalFileName(fileName);

        int idx = fileName.lastIndexOf(".");
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
        validateOriginalFileName(parts[2]);

        return parts[2]; // storedFileName
    }

    public static String generateStoredFileName(String originalFileName) {
        validateOriginalFileName(originalFileName);
        String extension = extractExtension(originalFileName);
        return UUID.randomUUID().toString() + extension;
    }

    public static void validateOriginalFileName(String fileName) {
        if (fileName == null) throw new InvalidFileFieldException("validateOriginalFileName: filename == null");
        int idx = fileName.lastIndexOf(".");
        if (idx == -1 || idx == fileName.length() - 1 || fileName.substring(0, idx).isEmpty()) throw new InvalidFileFieldException("validateOriginalFileName: 형식자가 잘못됐습니다.");
    }

    public static void validateTmpS3KeyFormat(String tmpS3Key) {
        if (!tmpS3Key.startsWith("post/tmp/")) {
            throw new InvalidFileFieldException("addFilesWith: 임시 s3Key 포맷이 아닙니다.");
        }
    }


//비즈니스 로직---------------------------------------------------------------------------------------------------

    public void updateS3Key(String newS3Key) {
        checkNotRemoved();
        if (newS3Key == null) throw new InvalidFileFieldException("getRegularS3Key: newS3Key == null");

        this.s3Key = newS3Key;
    }

    public void remove() {
        checkNotRemoved();

        storedFileName = "";
        this.isRemoved = true;
    }

//내부 로직---------------------------------------------------------------------------------------------------

    private void checkNotRemoved() {
        if (isRemoved()) throw new FileNotFoundException(this.getId());
    }
}
