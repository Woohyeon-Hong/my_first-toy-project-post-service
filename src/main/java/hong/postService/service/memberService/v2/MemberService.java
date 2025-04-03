package hong.postService.service.memberService.v2;

import hong.postService.domain.Member;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.service.memberService.dto.MemberUpdateInfoRequest;
import hong.postService.service.memberService.dto.PasswordUpdateRequest;
import hong.postService.service.postService.v2.PostService;
import hong.postService.web.members.dto.MemberResponse;
import hong.postService.service.memberService.dto.UserCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

//business 로직-------------------------------------------------------------
    @Transactional
    public Long signUp(UserCreateRequest request) {

        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();
        String nickname = request.getNickname();

        usernameValidate(username);
        passwordValidate(password);
        emailValidate(email);
        nicknameValidate(nickname);

        Member member = Member.createNewMember(username, password, email, nickname);

        Member saved = memberRepository.save(member);

        return saved.getId();
    }

    @Transactional
    public Long signUpAdmin(UserCreateRequest request) {

        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();
        String nickname = request.getNickname();

        usernameValidate(username);
        passwordValidate(password);
        emailValidate(email);
        nicknameValidate(nickname);

        Member member = Member.createNewAdmin(username, password, email, nickname);

        Member saved = memberRepository.save(member);

        return saved.getId();
    }

    public Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("unregister: 해당 id가 없습니다."));
    }


    @Transactional
    public void unregister(Long id) {
        Member findMember = findMember(id);
        memberRepository.delete(findMember);
    }

    @Transactional
    public void updateInfo(Long id, MemberUpdateInfoRequest updateParam) {
        Member findMember = findMember(id);

        changeUsername(findMember, updateParam.getUsername());
        changeEmail(findMember, updateParam.getEmail());
        changeNickname(findMember, updateParam.getNickname());
    }

    @Transactional
    public void updatePassword(Long id, PasswordUpdateRequest updateParam) {
        Member findMember = findMember(id);

        String current = updateParam.getCurrentPassword();
        String next = updateParam.getNewPassword();

        if (!findMember.getPassword().equals(current)) {
            throw new IllegalArgumentException("updatePassword: 현재 비밀번호가 일치하지 않습니다.");
        }

        passwordValidate(next);

        changePassword(findMember, next);
    }

//검증 로직-------------------------------------------------------------------------
    public void usernameValidate(String username) {
        List<Member> members = memberRepository.findAllByUsername(username);

        if (!members.isEmpty()) {
            throw new IllegalArgumentException("해당 username이 이미 존재함.");
        }
    }

    public void passwordValidate(String password) {
        List<Member> members = memberRepository.findAllByPassword(password);

        if (!members.isEmpty()) {
            throw new IllegalArgumentException("해당 password가 이미 존재함.");
        }
    }

    public void emailValidate(String email) {
        List<Member> members = memberRepository.findAllByEmail(email);

        if (!members.isEmpty()) {
            throw new IllegalArgumentException("해당 email이 이미 존재함.");

        }
    }

    public void nicknameValidate(String nickname) {
        List<Member> members = memberRepository.findAllByNickname(nickname);

        if (!members.isEmpty()) {
            throw new IllegalArgumentException("해당 nickname이 이미 존재함.");
        }
    }

//내부 로직-------------------------------------------------------------------------

    private void changeUsername(Member member, String username) {
        if (username != null && !username.equals(member.getUsername())) {
            usernameValidate(username);
            member.changeUsername(username);
        }
    }

    private void changeEmail(Member member, String email) {
        if(email != null && !email.equals(member.getEmail())){
            emailValidate(email);
            member.changeEmail(email);
        }
    }

    private void changeNickname(Member member, String nickname) {
        if(nickname != null && !nickname.equals(member.getNickname())) {
            nicknameValidate(nickname);
            member.changeNickname(nickname);
        }
    }

    private void changePassword(Member member, String password) {
        if (password != null && !password.equals(member.getPassword())) {
            passwordValidate(password);
            member.changePassword(password);
        }
    }

}
