package com.sivalabs.blog.admin.posts;

import static com.sivalabs.blog.shared.models.PagedResult.getPagedResult;
import static com.sivalabs.blog.shared.utils.CommonUtils.isValidIdList;

import com.sivalabs.blog.ApplicationProperties;
import com.sivalabs.blog.admin.taxonomy.CategoryRepository;
import com.sivalabs.blog.admin.taxonomy.TagRepository;
import com.sivalabs.blog.admin.users.UserRepository;
import com.sivalabs.blog.shared.entities.Category;
import com.sivalabs.blog.shared.entities.Comment;
import com.sivalabs.blog.shared.entities.Post;
import com.sivalabs.blog.shared.entities.Tag;
import com.sivalabs.blog.shared.entities.User;
import com.sivalabs.blog.shared.models.CommentStatus;
import com.sivalabs.blog.shared.models.PagedResult;
import com.sivalabs.blog.shared.models.PostStatus;
import com.sivalabs.blog.shared.utils.CommonUtils;
import com.sivalabs.blog.shared.utils.MarkdownUtils;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ApplicationProperties properties;

    public PostService(
            PostRepository postRepository,
            CommentRepository commentRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            TagRepository tagRepository,
            ApplicationProperties properties) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public PagedResult<Post> getAllPosts(int pageNo, int pageSize) {
        return getPagedResult(pageNo, pageSize, postRepository::findAllPostSummaries, post -> post);
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long id) {
        return postRepository.getByIdWithDetails(id);
    }

    @Transactional
    public void updatePost(UpdatePostParams params) {
        Post post = postRepository.getByIdWithDetails(params.id());
        Category category = categoryRepository.getBySlug(params.categorySlug());
        Set<Tag> tags = mapToTags(params.tagLabels());

        post.setTitle(params.title());
        post.setMdContent(params.mdContent());
        post.setContent(MarkdownUtils.toHTML(params.mdContent()));
        post.setCategory(category);
        post.setTags(tags);
        post.setStatus(params.status());

        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<Post> findPostsCreatedBetween(LocalDateTime start, LocalDateTime end) {
        return postRepository.findByCreatedDate(start, end);
    }

    @Transactional(readOnly = true)
    public PagedResult<Comment> getComments(Integer pageNo) {
        return getPagedResult(
                pageNo, properties.blogPostsPageSize(), commentRepository::findComments, comment -> comment);
    }

    @Transactional(readOnly = true)
    public Long getPostsCount() {
        return postRepository.count();
    }

    @Transactional(readOnly = true)
    public Long getCommentsCount() {
        return commentRepository.count();
    }

    @Transactional
    public Long createPost(CreatePostParams params) {
        Category category = categoryRepository.getBySlug(params.categorySlug());
        User user = userRepository.getById(params.createdBy());
        Set<Tag> tags = mapToTags(params.tagLabels());
        Post post = new Post();
        post.setTitle(params.title());
        post.setSlug(CommonUtils.toSlug(params.title()));
        post.setMdContent(params.mdContent());
        post.setContent(MarkdownUtils.toHTML(params.mdContent()));
        post.setCategory(category);
        post.setTags(tags);
        post.setCreatedBy(user);
        post.setStatus(params.status());
        postRepository.save(post);
        return post.getId();
    }

    @Transactional
    public void deletePosts(List<Long> postIds) {
        if (isValidIdList(postIds)) {
            commentRepository.deleteByPostIds(postIds);
            postRepository.deleteAllById(postIds);
        }
    }

    @Transactional
    public void updatePostStatus(List<Long> postIds, PostStatus status) {
        if (isValidIdList(postIds)) {
            postRepository.updateStatus(postIds, status);
        }
    }

    @Transactional
    public void deleteComments(List<Long> commentIds) {
        if (isValidIdList(commentIds)) {
            commentRepository.deleteAllById(commentIds);
        }
    }

    @Transactional
    public void updateCommentStatus(List<Long> commentIds, CommentStatus status) {
        if (isValidIdList(commentIds)) {
            commentRepository.updateStatus(commentIds, status);
        }
    }

    private Set<Tag> mapToTags(Set<String> tags) {
        Set<Tag> tagSet = new HashSet<>();
        for (String tagLabel : tags) {
            String tagSlug = CommonUtils.toSlug(tagLabel);
            Tag tag = tagRepository.findBySlugIgnoreCase(tagSlug).orElse(null);
            if (tag == null) {
                tag = tagRepository.findByLabelIgnoreCase(tagLabel).orElse(null);
            }
            if (tag == null) {
                tag = new Tag();
                tag.setLabel(tagLabel);
                tag.setSlug(tagSlug);
                tag = tagRepository.save(tag);
            }
            tagSet.add(tag);
        }
        return tagSet;
    }
}
