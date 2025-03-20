//package hong.postService.service.postService;
//
//import hong.postService.domain.Member;
//import hong.postService.domain.Post;
//import hong.postService.repository.memberRepository.UsersRepository;
//import hong.postService.repository.postRepository.BoardRepository;
//import hong.postService.repository.postRepository.PostUpdateDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//import java.util.NoSuchElementException;
//
//@Service
//@RequiredArgsConstructor
//public class PostServiceImpl implements PostService{
//
//    private final BoardRepository boardRepository;
//    private final UsersRepository usersRepository;
//
//    @Override
//    public Post upload(Post post) {
//        post.setModifiedDate(LocalDateTime.now());
//        return boardRepository.save(post);
//    }
//
//    @Override
//    public void deletePost(Long id) {
//        if (isIdExisted(id)) {
//            boardRepository.delete(id);
//        } else {
//            throw new NoSuchElementException();
//        }
//    }
//
//    private boolean isIdExisted(Long id) {
//        List<Post> posts = boardRepository.findAll();
//        return posts.stream().anyMatch((post) -> post.getId() == id);
//    }
//
//    @Override
//    public List<Post> showAllPosts() {
//        return boardRepository.findAll();
//    }
//
//    @Override
//    public List<Post> showMemberPosts(Long memberId) {
//        if (isMemberIdExisted(memberId)) {
//            return boardRepository.findMemberPosts(memberId);
//        } else {
//            throw new NoSuchElementException();
//        }
//    }
//
//    private boolean isMemberIdExisted(Long id) {
//        List<Member> members = usersRepository.findAll();
//        return members.stream().anyMatch(member -> member.getId() == id);
//    }
//
//    @Override
//    public List<Post> searchTitle(String title) {
//        return boardRepository.searchPosts(title);
//    }
//
//    @Override
//    public Post updatePost(Long id, PostUpdateDto updateParam) {
//        if (isIdExisted(id)) {
//            updateParam.setModifiedDate(LocalDateTime.now());
//            boardRepository.update(id, updateParam);
//            return boardRepository.findById(id).get();
//        } else {
//            throw new NoSuchElementException();
//        }
//    }
//
//    @Override
//    public  List<Post> showMemberPostsWithPaging(Long memberId, int page, int size) {
//        int offset = (page - 1) * size;
//        return boardRepository.findMemberPostsWithPaging(memberId, size, offset);
//    }
//
//    @Override
//    public List<Post> showPostsWithPaging(int page, int size) {
//        int offset = (page - 1) * size;
//        return boardRepository.findPostsWithPaging(size, offset);
//    }
//
//
//}
