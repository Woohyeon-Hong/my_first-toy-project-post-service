package hong.postService.exception.post;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(Long postId) {
        super("해당하는 ID의 Post가 없습니다. (id = " + postId + ")");
    }
}
