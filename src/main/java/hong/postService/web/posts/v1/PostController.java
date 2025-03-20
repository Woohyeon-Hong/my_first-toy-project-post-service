//package hong.postService.web.posts;
//
//import hong.postService.domain.Member;
//import hong.postService.domain.Post;
//import hong.postService.repository.postRepository.BoardRepository;
//import hong.postService.repository.postRepository.PostUpdateDto;
//import hong.postService.service.postService.PostService;
//import hong.postService.web.posts.dto.Form;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//
//@Slf4j
//@Controller
//@RequestMapping("/posts")
//@RequiredArgsConstructor
//public class PostController {
//
//    private final PostService postService;
//    private final BoardRepository boardRepository;
//
//    @GetMapping("/create")
//    public String createForm(@ModelAttribute("createForm")Form createForm) {
//        return "/posts/createForm";
//    }
//
//    @PostMapping("/create")
//    public String create(@Validated @ModelAttribute("createForm")Form createForm,
//                         BindingResult bindingResult,
//                         @SessionAttribute("loginMember")Member loginMember) {
//
//        if (bindingResult.hasErrors()) {
//            return "/posts/createForm";
//        }
//
//        Post post = new Post();
//        post.setTitle(createForm.getTitle());
//        post.setContent(createForm.getContent());
//
//        post.setMemberId(loginMember.getId());
//        post.setModifiedDate(LocalDateTime.now());
//
//        postService.upload(post);
//        return "redirect:/";
//    }
//
//    @GetMapping("/{postId}/read")
//    public String read(@PathVariable("postId")Long postId, Model model) {
//
//        Post post = boardRepository.findById(postId).orElseThrow();
//
//        model.addAttribute("post", post);
//
//        return "/posts/read";
//    }
//
//    @GetMapping("/readAll")
//    public String readAll(@RequestParam(defaultValue = "1") int page,
//                          @RequestParam(defaultValue = "10") int size,
//                          Model model) {
//
//        List<Post> posts = postService.showPostsWithPaging(page, size);
//        model.addAttribute("posts", posts);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("pageSize", size);
//
//        String serachCond="";
//        model.addAttribute("searchCond", serachCond);
//        return "posts/readAll";
//    }
//
//    @GetMapping("/search")
//    public String search(@RequestParam("query") String query, Model model) {
//
//        List<Post> searched = postService.searchTitle(query);
//
//        model.addAttribute("posts", searched);
//
//        return "posts/readSearched";
//    }
//
//    @GetMapping("/{postId}/update")
//    public String updateForm(@PathVariable("postId")Long postId, Model model) {
//
//        Post post = boardRepository.findById(postId).orElseThrow();
//
//        Form updateForm = new Form();
//        updateForm.setTitle(post.getTitle());
//        updateForm.setContent(post.getContent());
//
//        model.addAttribute("updateForm", updateForm);
//
//        return "posts/updateForm";
//    }
//
//    @PostMapping("{postId}/update")
//    public String update(@PathVariable("postId")Long postId,
//                         @Validated @ModelAttribute("updateForm")Form updateForm, BindingResult bindingResult) {
//
//        if (bindingResult.hasErrors()) {
//            return "posts/updateForm";
//        }
//
//        PostUpdateDto postUpdateDto = new PostUpdateDto();
//        postUpdateDto.setTitle(updateForm.getTitle());
//        postUpdateDto.setContent(updateForm.getContent());
//        postUpdateDto.setModifiedDate(LocalDateTime.now());
//
//        postService.updatePost(postId, postUpdateDto);
//
//        return "redirect:/";
//    }
//
//    @PostMapping("/{postId}/delete")
//    public String delete(@PathVariable("postId")Long postId) {
//
//        postService.deletePost(postId);
//
//        return "redirect:/";
//    }
//}
