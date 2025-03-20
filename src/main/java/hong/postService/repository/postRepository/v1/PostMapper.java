//package hong.postService.repository.postRepository;
//
//import hong.postService.domain.Post;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@Mapper
//public interface PostMapper {
//
//    //create
//    void save(Post post);
//
//    //read
//    Optional<Post> findById(Long id);
//    List<Post> findMemberPosts(Long memberId);
//    List<Post> findAll();
//
//    //update
//    void update(@Param("id") Long id, @Param("updateParam") PostUpdateDto updateParam);
//
//    //delete
//    void delete(Long id);
//
//    //pagination
//    List<Post> findMemberPostsWithPaging(@Param("memberId") Long id, @Param("limit") int limit, @Param("offset") int offset);
//    List<Post> findPostsWithPaging(@Param("limit") int limit, @Param("offset") int offset);
//
//    //search
//    List<Post> searchPosts(String title);
//}
