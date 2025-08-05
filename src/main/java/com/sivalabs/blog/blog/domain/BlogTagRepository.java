package com.sivalabs.blog.blog.domain;

import com.sivalabs.blog.shared.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogTagRepository extends JpaRepository<Tag, Long> {}
