package com.sivalabs.blog.admin.taxonomy;

import static com.sivalabs.blog.shared.utils.CommonUtils.isValidIdList;

import com.sivalabs.blog.shared.entities.Tag;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public Long getTagsCount() {
        return tagRepository.count();
    }

    @Transactional(readOnly = true)
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Transactional
    public void deleteTags(List<Long> tagIds) {
        if (isValidIdList(tagIds)) {
            tagRepository.deletePostTags(tagIds);
            tagRepository.deleteAllById(tagIds);
        }
    }
}
