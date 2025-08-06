package hong.postService.domain;

import hong.postService.exception.file.FileNotFoundException;
import hong.postService.exception.file.InvalidFileFieldException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class FileTest {

    @Test
    void validateOriginalFileName() {
        //given
        String fileName_ok = "example.txt";
        String fileName_onlyDot = "example.";
        String fileName_formatX = "example";
        String fileName_nameX = ".txt";

        //when
        File.validateOriginalFileName(fileName_ok);

        //then
        assertThatThrownBy(() -> File.validateOriginalFileName(fileName_onlyDot))
                .isInstanceOf(InvalidFileFieldException.class);
        assertThatThrownBy(() -> File.validateOriginalFileName(fileName_formatX))
                .isInstanceOf(InvalidFileFieldException.class);
        assertThatThrownBy(() -> File.validateOriginalFileName(fileName_nameX))
                .isInstanceOf(InvalidFileFieldException.class);
    }

    @Test
    void extractExtension() {
        //given
        String fileName = "example.txt";

        //when
        String result = File.extractExtension(fileName);

        //then
        assertThat(result).isEqualTo(".txt");
    }

    @Test
    void extractStoredFileName() {
        //given
        String s3Key = "post/1/a12345.txt";
        String wrong1 = "a12345.txt";
        String wrong2 = "post/a12345.txt";

        //when
        String result = File.extractStoredFileName(s3Key);

        //then
        assertThat(result).isEqualTo("a12345.txt");

        assertThatThrownBy(() -> File.extractStoredFileName(wrong1))
                .isInstanceOf(InvalidFileFieldException.class);
        assertThatThrownBy(() -> File.extractStoredFileName(wrong2))
                .isInstanceOf(InvalidFileFieldException.class);

    }

    @Test
    void generateStoredFileName() {
        //given
        String originalFileName = "example.txt";

        //when
        String storedFileName = File.generateStoredFileName(originalFileName);

        //then
        assertThat(storedFileName).isNotNull();

        assertThatThrownBy(() -> File.generateStoredFileName(null))
                .isInstanceOf(InvalidFileFieldException.class);

    }

    @Test
    void remove() {
        //given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("old", "content");
        File file = post.addNewFile("example.txt", "post/1/example-stored.txt");

        //when
        file.remove();

        //then
        assertThat(file.isRemoved()).isTrue();

        assertThatThrownBy(() -> file.remove())
                .isInstanceOf(FileNotFoundException.class);
    }
}