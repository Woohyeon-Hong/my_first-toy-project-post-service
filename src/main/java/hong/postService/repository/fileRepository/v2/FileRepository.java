package hong.postService.repository.fileRepository.v2;

import hong.postService.domain.File;
import hong.postService.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


/**
 *  파일 Repository API
 *
 * Create
 *     File save(file) - 글 작성 시
 *
 * Read
 *     Optional<File> findByIdAndIsRemovedFalse(id)
 *     Optional<File> findByS3KeyAndIsRemovedFalse(String s3Key)
 *     Optional<File> findByStoredFileNameAndIsRemovedFalse(String storedFileName)
 *
 *     List<File> findAllByPostIdAndIsRemovedFalse(Post post)
 *
 * Delete
 *     void delete(file)
 */
public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByIdAndIsRemovedFalse(Long id);
    Optional<File> findByS3KeyAndIsRemovedFalse(String s3Key);
    Optional<File> findByStoredFileNameAndIsRemovedFalse(String storedFileName);

    @Query("select f from File f left join fetch f.post where f.post = :post and f.isRemoved = false")
    List<File> findAllByPostIdAndIsRemovedFalse(Post post);
}
