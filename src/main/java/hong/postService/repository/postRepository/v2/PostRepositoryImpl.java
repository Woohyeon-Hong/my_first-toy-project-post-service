package hong.postService.repository.postRepository.v2;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hong.postService.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.ArrayList;
import java.util.List;

import static hong.postService.domain.QMember.member;
import static hong.postService.domain.QPost.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    /*
    - 서능 최적화를 위해 contains가 아닌 startsWith을 사용 -> 문자열의 모든 문자들을 확인하는 것을 방지하기 위함
    - queryDsl에서는 orderBy가 별도로 적용되야하기 때문에 OrderSpecifier 필요
     */
    @Override
    public Page<Post> searchPosts(SearchCond cond, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(post.isRemoved.isFalse());

        if (cond.getWriter() != null) {
            builder.and(post.writer.nickname.startsWith(cond.getWriter()));
        }

        if (cond.getTitle() != null) {
            builder.and(post.title.startsWith(cond.getTitle()));
        }


        List<Post> posts = queryFactory
                .selectFrom(post)
                .leftJoin(post.writer, member).fetchJoin()
                .where(builder)
                .orderBy(getOrderSpecifiers(pageable, Post.class, "post").toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(builder);

        return PageableExecutionUtils.getPage(posts, pageable, countQuery::fetchOne);
    }

    private List<OrderSpecifier<?>> getOrderSpecifiers(Pageable pageable, Class<?> type, String alias) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        PathBuilder<?> pathBuilder = new PathBuilder<>(type, alias);

        for (Sort.Order sortOrder : pageable.getSort()) {
            Order direction = sortOrder.isAscending() ? Order.ASC : Order.DESC;
            orders.add(new OrderSpecifier<>(
                    direction,
                    pathBuilder.getComparable(sortOrder.getProperty(), Comparable.class)
            ));
        }

        return orders;
    }
}
