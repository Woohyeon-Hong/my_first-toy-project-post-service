//package hong.postService.repository.postRepository;
//
//import hong.postService.domain.Post;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@Repository
//@RequiredArgsConstructor
//public class BoardRepositoryImpl implements BoardRepository{
//
//    private final PostMapper postMapper;
//
//    @Override
//    public Post save(Post post) {
//        postMapper.save(post);
//        return post;
//    }
//
//    @Override
//    public Optional<Post> findById(Long id) {
//        return postMapper.findById(id);
//    }
//
//    @Override
//    public List<Post> findMemberPosts(Long memberId) {
//        return postMapper.findMemberPosts(memberId);
//    }
//
//    @Override
//    public List<Post> findAll() {
//        return postMapper.findAll();
//    }
//
//    @Override
//    public void update(Long id, PostUpdateDto updateParam) {
//        postMapper.update(id, updateParam);
//    }
//
//    @Override
//    public void delete(Long id) {
//        postMapper.delete(id);
//    }
//
//    @Override
//    public List<Post> findMemberPostsWithPaging(Long memberId, int limit, int offset) {
//        return postMapper.findMemberPostsWithPaging(memberId, limit, offset);
//    }
//
//    @Override
//    public List<Post> findPostsWithPaging(int limit, int offset) {
//        return postMapper.findPostsWithPaging(limit, offset);
//    }
//
//    @Override
//    public List<Post> searchPosts(String title) {
//        return postMapper.searchPosts(title);
//    }
//}
