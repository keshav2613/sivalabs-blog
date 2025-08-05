package com.sivalabs.blog.admin.taxonomy;

import com.sivalabs.blog.shared.entities.Category;
import com.sivalabs.blog.shared.exceptions.BadRequestException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Long getCategoriesCount() {
        return categoryRepository.count();
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getBySlug(String categorySlug) {
        return categoryRepository.getBySlug(categorySlug);
    }

    @Transactional
    public void createCategory(String label, String slug) {
        boolean labelExists = categoryRepository.existsByLabelIgnoreCase(label);
        if (labelExists) {
            throw new BadRequestException("Category with label " + label + " already exists");
        }
        boolean slugExists = categoryRepository.existsBySlugIgnoreCase(slug);
        if (slugExists) {
            throw new BadRequestException("Category with slug " + slug + " already exists");
        }
        var category = new Category();
        category.setLabel(label);
        category.setSlug(slug);
        categoryRepository.save(category);
    }
}
