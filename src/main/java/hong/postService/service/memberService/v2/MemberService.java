package hong.postService.service.memberService.v2;

import hong.postService.domain.Member;
import hong.postService.domain.UserRole;
import hong.postService.exception.member.DuplicateMemberFieldException;
import hong.postService.exception.member.InvalidMemberFieldException;
import hong.postService.exception.member.MemberNotFoundException;
import hong.postService.exception.member.PasswordMismatchException;
import hong.postService.repository.memberRepository.v2.MemberRepository;
import hong.postService.service.memberService.dto.MemberUpdateInfoRequest;
import hong.postService.service.memberService.dto.OAuthCreateRequest;
import hong.postService.service.memberService.dto.PasswordUpdateRequest;
import hong.postService.service.memberService.dto.UserCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


/**
 * MemberService는 회원에 대한 비즈니스 로직을 담당하는 서비스 계층입니다.
 *
 * 주요 기능:
 *     회원 가입 (일반/관리자)
 *     회원 조회 (id로)
 *     회원 정보 수정 (username, email, nickname)
 *     회원 비밀번호 변경
 *     회원 탈퇴 (soft delete)
 *     회원 중복 필드 검증
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

//business 로직-------------------------------------------------------------

    /**
     * 회원 가입 시 다음과 같은 절차로 검증 및 생성을 수행한다:
     *
     * 1. 각 필드에 대해 DB 중복 검증을 수행한다.
     *    - 이때, null 값이 들어가는 것을 방지하기 위해 중복 검증 메서드 내에서 null 체크도 함께 수행한다.
     *
     * 2. 중복 검증을 모두 통과한 후, 도메인 객체의 생성 메서드를 호출하여 Member를 생성한다.
     *    - 도메인 계층에서도 null에 대한 유효성 검사를 다시 수행한다.
     *
     * ⚠️ 비록 Spring Data JPA에서는 null 값을 포함한 쿼리도 허용되지만,
     *     안정성과 명확한 예외 처리를 위해 서비스 계층에서도 null 방어 로직을 적용한다.
     *
     * 이 과정을 통해 데이터 정합성과 예외 처리를 이중으로 보호할 수 있다.
     */


    /**
     * 일반 회원 가입을 수행합니다.
     *
     * @param request 회원 가입 요청 DTO
     * @return 생성된 회원의 ID
     *
     * @throws InvalidMemberFieldException null이거나 유효하지 않은 필드가 있을 경우
     * @throws DuplicateMemberFieldException 중복된 username, email, nickname 등이 존재할 경우
     */
    @Transactional
    public Long signUp(UserCreateRequest request) {

        checkIsUser(request);

        String username = request.getUsername();
        String password = createPassword(request);
        String email = request.getEmail();
        String nickname = request.getNickname();

        validateFields(username, email, nickname);

        Member member = Member.createNewMember(username, password, email, nickname);

        return  memberRepository.save(member).getId();
    }

    /**
     * 관리자 회원 가입을 수행합니다.
     *
     * @param request 관리자 회원 가입 요청 DTO
     * @return 생성된 관리자 회원의 ID
     *
     * @throws InvalidMemberFieldException null이거나 유효하지 않은 필드가 있을 경우
     * @throws DuplicateMemberFieldException 중복된 username, email, nickname 등이 존재할 경우
     */
    @Transactional
    public Long signUpAdmin(UserCreateRequest request) {

        checkIsAdmin(request);

        String username = request.getUsername();
        String password = createPassword(request);
        String email = request.getEmail();
        String nickname = request.getNickname();

        validateFields(username, email, nickname);

        Member member = Member.createNewAdmin(username, password, email, nickname);

        return  memberRepository.save(member).getId();
    }

    @Transactional
    public Long signUpWithOAuth(OAuthCreateRequest request) {
        String username = request.getUsername();
        String email = request.getEmail();

        //임의 할당
        String password = encoder.encode(UUID.randomUUID().toString());
        String defaultNickname = "user_" + UUID.randomUUID().toString().substring(0, 8);

        validateFields(username, email, defaultNickname);

        Member member = Member.createNewOAuthMember(username, password, email, defaultNickname);

        return memberRepository.save(member).getId();
    }

    /**
     * 회원 ID로 회원을 조회합니다.
     *
     * @param memberId 조회할 회원의 ID
     * @return 조회된 Member 엔티티
     *
     * @throws MemberNotFoundException 존재하지 않거나 삭제된 회원일 경우
     */
    public Member findMember(Long memberId) {
        return memberRepository.findByIdAndIsRemovedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    /**
     * 회원 정보를 수정합니다.
     *
     * @param memberId 수정 대상 회원 ID
     * @param updateParam username, email, nickname을 포함한 수정 DTO
     *
     * @throws MemberNotFoundException 존재하지 않거나 삭제된 회원일 경우
     * @throws InvalidMemberFieldException null 값이거나, 형식이 잘못되거나, OAuth 회원인 경우
     * @throws DuplicateMemberFieldException 중복된 필드가 존재할 경우
     */
    @Transactional
    public void updateInfoOfNotOAuthMember(Long memberId, MemberUpdateInfoRequest updateParam) {
        Member findMember = findMember(memberId);
        checkIsOAUthMember(findMember);
        updateFields(updateParam, findMember);
    }

    /**
     * 회원 nickname을 수정합니다.
     *
     * @param memberId 수정 대상 회원 ID
     * @param newNickname 바꿀 nickname
     *
     * @throws MemberNotFoundException 존재하지 않거나 삭제된 회원일 경우
     */
    @Transactional
    public void updateNickname(Long memberId, String newNickname) {
        Member findMember = findMember(memberId);
        if (newNickname != null) changeNickname(findMember, newNickname);
    }

    /**
     * 비밀번호를 변경합니다.
     *
     * @param memberId 대상 회원 ID
     * @param updateParam 비밀번호 변경 요청 DTO (현재 비밀번호 + 새 비밀번호)
     *
     * @throws PasswordMismatchException 현재 비밀번호가 일치하지 않는 경우
     * @throws DuplicateMemberFieldException 새 비밀번호가 중복된 경우
     * @throws InvalidMemberFieldException 새 비밀번호가 기존과 같은 경우
     */
    @Transactional
    public void updatePasswordOfNotOAuthMember(Long memberId, PasswordUpdateRequest updateParam) {
        Member findMember = findMember(memberId);
        checkIsOAUthMember(findMember);
        String next = validatePasswordUpdateRequest(updateParam, findMember);
        changePassword(findMember, encoder.encode(next));
    }

    /**
     * 회원을 탈퇴 처리합니다. (Soft delete 방식)
     *
     * @param memberId 탈퇴할 회원의 ID
     *
     * @throws MemberNotFoundException 존재하지 않거나 이미 삭제된 회원인 경우
     */
    @Transactional
    public void unregister(Long memberId) {
        Member findMember = findMember(memberId);
        findMember.remove();
    }


    //검증 로직-------------------------------------------------------------------------
    public void usernameDuplicateCheck(String newUsername) {

        if (newUsername == null) throw new InvalidMemberFieldException("usernameDuplicateCheck: newUsername == null");

        Optional<Member> member = memberRepository.findByUsernameAndIsRemovedFalse(newUsername);
        if (member.isPresent()) {
            throw new DuplicateMemberFieldException("해당 username이 이미 존재함.");
        }
    }

    public void emailDuplicateCheck(String newEmail) {

        if (newEmail == null) throw new InvalidMemberFieldException("emailDuplicateCheck: newEmail == null");

        Optional<Member> member = memberRepository.findByEmailAndIsRemovedFalse(newEmail);

        if (member.isPresent()) {
            throw new DuplicateMemberFieldException("해당 email이 이미 존재함.");

        }
    }

    public void nicknameDuplicateCheck(String newNickname) {

        if (newNickname == null) throw new InvalidMemberFieldException("nicknameDuplicateCheck: newNickname == null");

        Optional<Member> member = memberRepository.findByNicknameAndIsRemovedFalse(newNickname);

        if (member.isPresent()) {
            throw new DuplicateMemberFieldException("해당 nickname이 이미 존재함.");
        }
    }

//내부 로직-------------------------------------------------------------------------
    private static void checkIsUser(UserCreateRequest request) {
        if (request.getRole() == UserRole.ADMIN) throw new InvalidMemberFieldException("signUp: role == ADMIN");
    }

    private static void checkIsAdmin(UserCreateRequest request) {
        if (request.getRole() == UserRole.USER) throw new InvalidMemberFieldException("signUpAdmin: role == USER");
    }

    private String createPassword(UserCreateRequest request) {
        String rawPassword = request.getPassword();
        if (rawPassword == null) throw new InvalidMemberFieldException("signUp: password가 null일 수 없음");
        String password = encoder.encode(rawPassword);
        return password;
    }

    private void validateFields(String username, String email, String nickname) {
        usernameDuplicateCheck(username);
        if (email != null) emailDuplicateCheck(email);
        nicknameDuplicateCheck(nickname);
    }

    private void updateFields(MemberUpdateInfoRequest updateParam, Member findMember) {
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

    private String validatePasswordUpdateRequest(PasswordUpdateRequest updateParam, Member findMember) {
        String current = updateParam.getCurrentPassword();  // raw String
        String next = updateParam.getNewPassword(); //raw String

        //기존 비번 검증
        if (!encoder.matches(current, findMember.getPassword())) {  //raw data, encoded data 순으로
            throw new PasswordMismatchException();
        }

        //바꿀 비번이 현재 비번과 동일하지 않은지 검증
        if (encoder.matches(next, findMember.getPassword())) {
            throw new InvalidMemberFieldException("updatePassword: 기존 비밀번호와 동일한 비밀번호로는 변경할 수 없습니다.");
        }
        return next;
    }



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

    // 이미 인코딩된 값을 받는 것이 더 안전
    private void changePassword(Member member, String encodedPassword) {
        member.changePassword(encodedPassword);
    }

    private static void checkIsOAUthMember(Member findMember) {
        if (findMember.isOAuthMember()) throw new InvalidMemberFieldException("updateInfo: isOAuthMember == true");
    }
}
