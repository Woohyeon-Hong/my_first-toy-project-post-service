package hong.postService.repository.memberRepository.v2.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hong.postService.domain.QMember;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;
}
