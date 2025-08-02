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
    void extractExtension() {
        //given
        String fileName_ok = "example.txt";
        String fileName_onlyDot = "example.";
        String fileName_formatX = "example";
        String fileName_nameX = ".txt";

        //when
        String result1 = File.extractExtension(fileName_ok);
        String result2 = File.extractExtension(fileName_nameX);

        //then
        assertThat(result1).isEqualTo(".txt");
        assertThat(result2).isEqualTo(".txt");

        assertThatThrownBy(() ->  File.extractExtension(fileName_formatX))
                .isInstanceOf(InvalidFileFieldException.class);

        assertThatThrownBy(() ->  File.extractExtension(fileName_onlyDot))
                .isInstanceOf(InvalidFileFieldException.class);
    }

    @Test
    void generateStoredFileName() {
        //given
        String extension = ".txt";
        String storedFileName = null;

        //when
        storedFileName = File.generateStoredFileName(extension);

        //then
        assertThat(storedFileName).isNotNull();

        assertThatThrownBy(() -> File.generateStoredFileName(null))
                .isInstanceOf(InvalidFileFieldException.class);

    }

    @Test
    void generateS3Key() {
        //given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("old", "content");

        File file1 = post.addNewFile("example1.txt");
        File file2 = post.addNewFile("example2.txt");

        //when
        file1.generateS3Key();

        //then
        assertThat(file1.getS3Key()).isNotNull();

        assertThatThrownBy(() -> file1.generateS3Key())
                .isInstanceOf(InvalidFileFieldException.class);
    }

    @Test
    void remove() {
        //given
        Member member = Member.createNewMember("user", "pw", null, "nick");
        Post post = member.writeNewPost("old", "content");
        File file = post.addNewFile("example.txt");

        //when
        file.remove();

        //then
        assertThat(file.isRemoved()).isTrue();

        assertThatThrownBy(() -> file.remove())
                .isInstanceOf(FileNotFoundException.class);
    }
}