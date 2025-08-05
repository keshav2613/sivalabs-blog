package com.sivalabs.blog.blog.domain;

import com.sivalabs.blog.shared.entities.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogSettingRepository extends JpaRepository<Settings, Long> {}
