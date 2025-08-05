package com.sivalabs.blog.blog.domain;

import com.sivalabs.blog.blog.domain.models.CategorySummaryDTO;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlogCategoryService {
    private final BlogCategoryRepository categoryRepository;

    public BlogCategoryService(BlogCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategorySummaryDTO> getCategoriesWithPostCounts() {
        return categoryRepository.getCategoriesWithPostCounts();
    }
}
