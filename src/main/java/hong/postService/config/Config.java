package hong.postService.config;

import hong.postService.repository.memberRepository.UsersRepository;
import hong.postService.repository.postRepository.BoardRepository;
import hong.postService.service.memberService.MemberService;
import hong.postService.service.postService.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Config {

//    private final MemberMapper memberMapper;
//    private final PostMapper postMapper;
//
//    @Bean
//    public UsersRepository usersRepository() {
//        return new UsersRepositoryImpl(memberMapper);
//    }
//
//    @Bean
//    public BoardRepository boardRepository() {
//        return new BoardRepositoryImpl(postMapper);
//    }

//    @Bean
//    public MemberService memberService() {
//        return new MemberServiceImpl(usersRepository());
//    }
//
//    @Bean
//    public PostService postService() {
//        return new PostServiceImpl(boardRepository(), usersRepository());
//    }
}
