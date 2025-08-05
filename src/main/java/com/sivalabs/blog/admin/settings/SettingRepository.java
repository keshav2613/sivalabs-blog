package com.sivalabs.blog.admin.settings;

import com.sivalabs.blog.shared.entities.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Settings, Long> {}
