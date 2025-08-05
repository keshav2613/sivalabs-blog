package com.sivalabs.blog.blog.domain;

import com.sivalabs.blog.shared.entities.Settings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlogSettingsService {
    private final BlogSettingRepository settingRepository;

    public BlogSettingsService(BlogSettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Transactional(readOnly = true)
    public Settings getSettings() {
        return settingRepository.findAll().getFirst();
    }
}
