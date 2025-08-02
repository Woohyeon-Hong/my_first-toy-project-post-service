package hong.postService.exception.file;

public class FileNotFoundException extends RuntimeException{

    public FileNotFoundException(Long id) {
        super("해당 Id의 파일이 존재하지 않습니다. (id = " + id + ")");
    }
}
