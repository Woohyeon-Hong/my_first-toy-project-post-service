package hong.postService.exception;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(Long id) {
        super("해당하는 Comment는 없습니다. (id = " + id + ")");
    }
}
