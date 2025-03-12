//package hong.postService.web.members;
//
//import hong.postService.domain.Member;
//import hong.postService.domain.Post;
//import hong.postService.repository.memberRepository.MemberUpdateDto;
//import hong.postService.repository.memberRepository.UsersRepository;
//import hong.postService.service.memberService.MemberService;
//import hong.postService.service.postService.PostService;
//import hong.postService.web.members.dto.AddForm;
//import hong.postService.web.members.dto.LoginForm;
//import hong.postService.web.members.dto.MemberUpdateInfo;
//import hong.postService.web.members.dto.MemberUpdatePassword;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Slf4j
//@Controller
//@RequestMapping("/members")
//@RequiredArgsConstructor
//public class MemberController {
//
//    private final MemberService memberService;
//    private final UsersRepository usersRepository;
//
//    @GetMapping("/add")
//    public String addForm(@ModelAttribute("addForm") AddForm addform) {
//        return "/members/addMemberForm";
//    }
//
//    @PostMapping("/add")
//    public String add(@Validated @ModelAttribute("addForm") AddForm addForm,
//                      BindingResult bindingResult) {
//
//        if (usersRepository.findByLoginId(addForm.getLoginId()).isPresent()) {
//            bindingResult.rejectValue("loginId", "Duplicate");
//        }
//
//        if (bindingResult.hasErrors()) {
//            return "/members/addMemberForm";
//        }
//
//        Member newMember = new Member();
//
//        newMember.setName(addForm.getName());
//        newMember.setLoginId(addForm.getLoginId());
//        newMember.setPassword(addForm.getPassword());
//
//        Member savedMember = memberService.signUp(newMember);
//
//        return "redirect:/";
//    }
//
//    @GetMapping("/login")
//    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
//        return "/members/loginForm";
//    }
//
//    @PostMapping("/login")
//    public String login(@Validated @ModelAttribute("loginForm") LoginForm member,
//                        BindingResult bindingResult,
//                        HttpServletRequest request) {
//
//        if (bindingResult.hasErrors()) {
//            return "/members/loginForm";
//        }
//
//        try {
//            Member user = memberService.logIn(member.getLoginId(), member.getPassword());
//            HttpSession session = request.getSession(true);
//            session.setAttribute("loginMember", user);
//
//            return "redirect:/";
//        } catch (Exception e) {
//            bindingResult.reject("loginFail");
//            return "/members/loginForm";
//        }
//    }
//
//    @PostMapping("/logout")
//    public String logout(HttpServletRequest request) {
//
//        HttpSession session = request.getSession(false);
//
//        if (session != null) {
//            session.invalidate();
//        }
//
//        return "redirect:/";
//    }
//
//    @GetMapping("/updateInfo")
//    public String updateInfoForm(@SessionAttribute(name = "loginMember")Member loginMember,
//                             Model model) {
//
//        MemberUpdateInfo updateParam = new MemberUpdateInfo();
//        updateParam.setName(loginMember.getName());
//        updateParam.setLoginId(loginMember.getLoginId());
//
//        model.addAttribute("updateParam", updateParam);
//        return "members/updateInfoForm";
//    }
//
//    @PostMapping("/updateInfo")
//    public String updateInfo(@Validated @ModelAttribute("updateParam")MemberUpdateInfo updateParam,
//                         BindingResult bindingResult,
//                         @SessionAttribute(name = "loginMember")Member loginMember) {
//
//        if (usersRepository.findByLoginId(updateParam.getLoginId()).isPresent()) {
//            bindingResult.rejectValue("loginId", "Duplicate");
//        }
//
//
//        if (bindingResult.hasErrors()) {
//            return "/members/updateInfoForm";
//        }
//
//        MemberUpdateDto memberUpdateDto = new MemberUpdateDto();
//        memberUpdateDto.setLoginId(updateParam.getLoginId());
//        memberUpdateDto.setName(updateParam.getName());
//        memberUpdateDto.setPassword(loginMember.getPassword());
//
//        Member updateMember = memberService.updateInfo(loginMember.getId(), memberUpdateDto);
//
//        return "redirect:/";
//    }
//
//    @GetMapping("/updatePassword")
//    public String updatePasswordForm(@SessionAttribute(name = "loginMember")Member loginMember,
//                             Model model) {
//
//        MemberUpdatePassword updateParam = new MemberUpdatePassword();
//        updateParam.setPassword(loginMember.getPassword());
//
//        model.addAttribute("updateParam", updateParam);
//        return "/members/updatePasswordForm";
//    }
//
//    @PostMapping("/updatePassword")
//    public String updatePassword(@Validated @ModelAttribute("updateParam") MemberUpdatePassword updateParam,
//                                 BindingResult bindingResult,
//                                 @SessionAttribute(name = "loginMember")Member loginMember) {
//
//        if (bindingResult.hasErrors()) {
//            return "/members/updatePasswordForm";
//        }
//
//        Member updateMember = memberService.updatePassword(loginMember.getId(), updateParam.getPassword());
//
//        return "redirect:/";
//    }
//
//    @PostMapping("/delete")
//    public String delete(@SessionAttribute(name = "loginMember") Member loginMember, HttpServletRequest request) {
//        memberService.unregister(loginMember.getId());
//
//        HttpSession session = request.getSession(false);
//
//        if (session != null) {
//            session.invalidate();
//        }
//
//        return "redirect:/";
//    }
//}
