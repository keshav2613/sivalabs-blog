package com.sivalabs.blog.blog.domain;

import com.sivalabs.blog.shared.entities.Tag;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlogTagService {
    private final BlogTagRepository tagRepository;

    public BlogTagService(BlogTagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }
}
