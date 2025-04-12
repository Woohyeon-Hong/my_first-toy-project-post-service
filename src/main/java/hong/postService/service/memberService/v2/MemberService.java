package hong.postService.service.memberService.v2;

import hong.postService.domain.Member;
import hong.postService.exception.member.DuplicateMemberFieldException;
import hong.postService.exception.member.InvalidMemberFieldException;
import hong.postService.exception.member.MemberNotFoundException;
import hong.postService.exception.member.PasswordMismatchException;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.service.memberService.dto.MemberUpdateInfoRequest;
import hong.postService.service.memberService.dto.PasswordUpdateRequest;
import hong.postService.service.memberService.dto.UserCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

//business 로직-------------------------------------------------------------

    /**
     * 회원 가입 시 각 필드에 대해, DB 중복 검증 후에 도메인 생성 메서드를 호출한다.
     * 이를 통해, DB 중복 검증 메서드에서는 null 체크 후 DB를 조회함으로써 안정성을 확보한다.
     * 비록, Data JPA에서 null 값을 허용한다고, 해도 안정성 측면에서 null 체크는 필요하다.
     * 또한, 도메인에서 null 체크를 한다고 해도 방어적 null 체크를 수행하는게 좋다.
     */
    @Transactional
    public Long signUp(UserCreateRequest request) {

        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();
        String nickname = request.getNickname();

        usernameDuplicateCheck(username);
        passwordDuplicateCheck(password);
        emailDuplicateCheck(email);
        nicknameDuplicateCheck(nickname);

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

        usernameDuplicateCheck(username);
        passwordDuplicateCheck(password);
        emailDuplicateCheck(email);
        nicknameDuplicateCheck(nickname);

        Member member = Member.createNewAdmin(username, password, email, nickname);

        Member saved = memberRepository.save(member);

        return saved.getId();
    }

    @Transactional
    public void unregister(Long id) {
        Member findMember = findMember(id);
        memberRepository.delete(findMember);
    }

    @Transactional
    public void updateInfo(Long id, MemberUpdateInfoRequest updateParam) {
        Member findMember = findMember(id);

        if (updateParam.getUsername() != null) {
            changeUsername(findMember, updateParam.getUsername());
        }

        if (updateParam.getEmail() != null) {
            changeEmail(findMember, updateParam.getEmail());
        }

        if (updateParam.getNickname() != null) {
            changeNickname(findMember, updateParam.getNickname());
        }
    }

    /**
     * 비밀번호 변경 로직은 다음 순서로 수행된다:
     *
     * 1. null 여부 검증: newPassword, currentPassword는 컨트롤러에서 Bean Validation을 통해 null체크를 한다.
     * 2. 현재 비밀번호 확인: 클라이언트가 입력한 현재 비밀번호가 실제 저장된 비밀번호와 일치하는지 검증한다.
     *    - 이는 사용자의 인증을 다시 한 번 확인하는 보안 절차다.
     * 3. 새로운 비밀번호 검증:
     *    1) 현재 비밀번호와 동일한지 확인 → 동일할 경우 예외 발생 (보안 및 UX 측면에서 부적절)
     *    2) DB 내에 동일한 비밀번호가 이미 존재하는지 중복 검증 → 있을 경우 예외 발생
     *       (효율성을 위해 현재 비밀번호와의 일치 여부를 먼저 확인한 후 DB를 조회한다)
     * 4. 모든 검증을 통과하면 실제 비밀번호를 변경한다.
     */
    @Transactional
    public void updatePassword(Long id, PasswordUpdateRequest updateParam) {
        Member findMember = findMember(id);

        String current = updateParam.getCurrentPassword();
        String next = updateParam.getNewPassword();

        if (!findMember.getPassword().equals(current)) {
            throw new PasswordMismatchException();
        }

        if (current.equals(next)) {
            throw new InvalidMemberFieldException("updatePassword: 기존 비밀번호와 동일한 비밀번호로는 변경할 수 없습니다.");
        }

        passwordDuplicateCheck(next);

        changePassword(findMember, next);
    }

    public Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));
    }


    //검증 로직-------------------------------------------------------------------------
    public void usernameDuplicateCheck(String newUsername) {

        if (newUsername == null) throw new InvalidMemberFieldException("usernameDuplicateCheck: newUsername == null");

        List<Member> members = memberRepository.findAllByUsernameAndIsRemovedFalse(newUsername);
        if (!members.isEmpty()) {
            throw new DuplicateMemberFieldException("해당 username이 이미 존재함.");
        }
    }

    public void passwordDuplicateCheck(String newPassword) {

        if (newPassword == null) throw new InvalidMemberFieldException("passwordDuplicateCheck: newPassword == null");

        List<Member> members = memberRepository.findAllByPasswordAndIsRemovedFalse(newPassword);

        if (!members.isEmpty()) {
            throw new DuplicateMemberFieldException("해당 password가 이미 존재함.");
        }
    }

    public void emailDuplicateCheck(String newEmail) {

        if (newEmail == null) throw new InvalidMemberFieldException("emailDuplicateCheck: newEmail == null");

        List<Member> members = memberRepository.findAllByEmailAndIsRemovedFalse(newEmail);

        if (!members.isEmpty()) {
            throw new DuplicateMemberFieldException("해당 email이 이미 존재함.");

        }
    }

    public void nicknameDuplicateCheck(String newNickname) {

        if (newNickname == null) throw new InvalidMemberFieldException("nicknameDuplicateCheck: newNickname == null");

        List<Member> members = memberRepository.findAllByNicknameAndIsRemovedFalse(newNickname);

        if (!members.isEmpty()) {
            throw new DuplicateMemberFieldException("해당 nickname이 이미 존재함.");
        }
    }

//내부 로직-------------------------------------------------------------------------
    /**
     * 새로운 username에 대해 다음 순서로 검증 및 업데이트를 수행한다:
     *
     * 1. null 여부 확인: null인 경우 처리를 중단한다. (null.equals(...)는 NPE 발생 가능성 있음)
     * 2. 기존 username과 동일한 경우: 변경할 필요가 없으므로 조용히 무시한다.
     * 3. 중복 여부 확인: 동일 username이 이미 다른 사용자에 의해 사용 중이라면 예외를 발생시킨다.
     * 4. 검증을 통과하면 username을 실제로 변경한다.
     *
     * 이 과정을 통해 불필요한 업데이트를 방지하고, 안정적인 필드 변경을 보장한다.
     */
    private void changeUsername(Member member, String newUsername) {
        if (!newUsername.equals(member.getUsername())) {
            usernameDuplicateCheck(newUsername);
            member.changeUsername(newUsername);
        }
    }

    private void changeEmail(Member member, String newEmail) {
        if (!newEmail.equals(member.getEmail())) {
            emailDuplicateCheck(newEmail);
            member.changeEmail(newEmail);
        }
    }

    private void changeNickname(Member member, String newNickname) {
        if (!newNickname.equals(member.getNickname())) {
            nicknameDuplicateCheck(newNickname);
            member.changeNickname(newNickname);
        }
    }

    private void changePassword(Member member, String newPassword) {
        member.changePassword(newPassword);
    }

}
