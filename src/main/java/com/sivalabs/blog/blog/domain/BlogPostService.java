package com.sivalabs.blog.blog.domain;

import static com.sivalabs.blog.shared.models.PagedResult.getPagedResult;

import com.sivalabs.blog.ApplicationProperties;
import com.sivalabs.blog.blog.domain.models.CommentSummaryDTO;
import com.sivalabs.blog.blog.domain.models.CreateCommentParams;
import com.sivalabs.blog.blog.domain.models.PostDetailsDTO;
import com.sivalabs.blog.blog.domain.models.PostSummaryDTO;
import com.sivalabs.blog.shared.entities.Comment;
import com.sivalabs.blog.shared.entities.Post;
import com.sivalabs.blog.shared.models.PagedResult;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlogPostService {
    private final BlogPostRepository postRepository;
    private final BlogCommentRepository commentRepository;
    private final BlogPostMapper blogPostMapper;
    private final ApplicationProperties properties;

    public BlogPostService(
            BlogPostRepository postRepository,
            BlogCommentRepository commentRepository,
            BlogPostMapper blogPostMapper,
            ApplicationProperties properties) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.blogPostMapper = blogPostMapper;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public PagedResult<PostSummaryDTO> getLatestPosts(int pageNo) {
        return getPagedResult(
                pageNo,
                properties.blogPostsPageSize(),
                postRepository::findPostSummaries,
                blogPostMapper::toPostSummaryDTO);
    }

    @Transactional(readOnly = true)
    public PagedResult<PostSummaryDTO> searchPosts(String query, Integer pageNo) {
        String searchQuery = "%" + query.toLowerCase() + "%";
        return getPagedResult(
                pageNo,
                properties.blogPostsPageSize(),
                pageable -> postRepository.searchPosts(searchQuery, pageable),
                blogPostMapper::toPostSummaryDTO);
    }

    @Transactional(readOnly = true)
    public PagedResult<PostSummaryDTO> getPostsByCategory(String categorySlug, Integer pageNo) {
        return getPagedResult(
                pageNo,
                properties.blogPostsPageSize(),
                pageable -> postRepository.findPostsByCategory(categorySlug, pageable),
                blogPostMapper::toPostSummaryDTO);
    }

    @Transactional(readOnly = true)
    public PagedResult<PostSummaryDTO> getPostsByTag(String tagSlug, Integer pageNo) {
        return getPagedResult(
                pageNo,
                properties.blogPostsPageSize(),
                pageable -> postRepository.findPostsByTag(tagSlug, pageable),
                blogPostMapper::toPostSummaryDTO);
    }

    @Transactional(readOnly = true)
    public PostDetailsDTO getPostBySlug(String slug) {
        Post post = postRepository.getBySlug(slug);
        return blogPostMapper.toPostDetailsDTO(post);
    }

    @Transactional(readOnly = true)
    public List<CommentSummaryDTO> getCommentsByPostSlug(String postSlug) {
        return commentRepository.findCommentsByPost(postSlug).stream()
                .map(blogPostMapper::toCommentSummaryDTO)
                .toList();
    }

    @Transactional
    public void createComment(CreateCommentParams params) {
        Comment comment = new Comment();
        comment.setPost(postRepository.findById(params.postId()).orElseThrow());
        comment.setName(params.name());
        comment.setEmail(params.email());
        comment.setContent(params.content());
        comment.setStatus(params.status());
        commentRepository.save(comment);
    }
}
