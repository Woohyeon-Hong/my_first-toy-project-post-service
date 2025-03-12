//package hong.postService.web;
//
//import hong.postService.domain.Member;
//import hong.postService.domain.Post;
//import hong.postService.service.postService.PostService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.SessionAttribute;
//
//import java.util.List;
//
//@Controller
//@RequiredArgsConstructor
//public class HomeController {
//
//    private final PostService postService;
//
//    /**
//     *회원 가입 버튼 클릭 시 'members/add'로
//     *로그인 버튼 클릭 시 'members/login'로
//     */
//    @GetMapping("/")
//    public String home(@SessionAttribute(name = "loginMember", required = false)Member loginMember,
//                       @RequestParam(defaultValue = "1") int page,
//                       @RequestParam(defaultValue = "10") int size,
//                       Model model) {
//
//        if (loginMember == null) {
//            return "home";
//        }
//
//        model.addAttribute("member", loginMember);
//
//        List<Post> posts = postService.showMemberPostsWithPaging(loginMember.getId(), page, size);
//        model.addAttribute("posts", posts);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("pageSize", size);
//
//        return "/members/loginHome";
//    }
//}
