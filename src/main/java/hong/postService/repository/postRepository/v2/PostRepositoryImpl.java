package hong.postService.repository.postRepository.v2;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hong.postService.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static hong.postService.domain.QMember.member;
import static hong.postService.domain.QPost.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> searchPosts(SearchCond cond, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (cond.getWriter() != null) {
            builder.and(post.writer.username.contains(cond.getWriter()));
        }

        if (cond.getTitle() != null) {
            builder.and(post.title.contains(cond.getTitle()));
        }

        List<Post> posts = queryFactory
                .selectFrom(post)
                .leftJoin(post.writer, member).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(builder);

        return PageableExecutionUtils.getPage(posts, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Post> searchPosts(SearchCond cond) {

        BooleanBuilder builder = new BooleanBuilder();

        if (cond.getWriter() != null) {
            builder.and(post.writer.username.contains(cond.getWriter()));
        }

        if (cond.getTitle() != null) {
            builder.and(post.title.contains(cond.getTitle()));
        }

        return queryFactory
                .selectFrom(post)
                .leftJoin(post.writer, member).fetchJoin()
                .where(builder)
                .fetch();
    }
}
