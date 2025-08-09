package hong.postService.repository.postRepository.v2;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hong.postService.domain.Post;
import hong.postService.service.postService.dto.PostSummaryResponse;
import hong.postService.service.postService.dto.QPostSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.ArrayList;
import java.util.List;

import static hong.postService.domain.QComment.comment;
import static hong.postService.domain.QFile.file;
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
    public Page<PostSummaryResponse> searchPosts(SearchCond cond, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(post.isRemoved.isFalse());

        if (cond.getWriter() != null) {
            builder.and(post.writer.nickname.startsWith(cond.getWriter()));
        }

        if (cond.getTitle() != null) {
            builder.and(post.title.startsWith(cond.getTitle()));
        }


        List<PostSummaryResponse> contents = queryFactory
                .select(new QPostSummaryResponse(
                        post.id,
                        post.title,
                        post.writer.nickname,
                        post.createdDate,
                        JPAExpressions.select(comment.count())
                                .from(comment)
                                .where(comment.post.eq(post)
                                        .and(comment.isRemoved.isFalse())),
                        JPAExpressions.selectOne()
                                .from(file)
                                .where(file.post.eq(post)
                                        .and(file.isRemoved.isFalse()))
                                .exists()
                ))
                .from(post)
                .leftJoin(post.writer, member)
                .where(builder)
                .orderBy(getOrderSpecifiers(pageable, Post.class, "post").toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(builder);

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
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
