package com.sivalabs.blog.admin.settings;

import com.sivalabs.blog.shared.entities.Settings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsService {
    private final SettingRepository settingRepository;

    public SettingsService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Transactional(readOnly = true)
    public Settings getSettings() {
        return settingRepository.findAll().getFirst();
    }

    @Transactional
    public void update(Settings settings) {
        var settingsEntity = settingRepository.findAll().getFirst();
        settingsEntity.setAdminContactName(settings.getAdminContactName());
        settingsEntity.setAdminContactEmail(settings.getAdminContactEmail());
        settingsEntity.setAdminContactAddress(settings.getAdminContactAddress());
        settingsEntity.setAdminContactTwitter(settings.getAdminContactTwitter());
        settingsEntity.setAdminContactGithub(settings.getAdminContactGithub());
        settingsEntity.setAdminContactLinkedin(settings.getAdminContactLinkedin());
        settingsEntity.setAdminContactYoutube(settings.getAdminContactYoutube());
        settingsEntity.setAutoApproveComment(settings.getAutoApproveComment());

        settingRepository.save(settingsEntity);
    }
}
