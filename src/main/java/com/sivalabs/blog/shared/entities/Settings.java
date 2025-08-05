package com.sivalabs.blog.shared.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "settings")
public class Settings extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "settings_id_gen")
    @SequenceGenerator(name = "settings_id_gen", sequenceName = "setting_id_seq", initialValue = 100)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "admin_contact_name", nullable = false, length = 250)
    private String adminContactName;

    @Column(name = "admin_contact_email", nullable = false, length = 300)
    private String adminContactEmail;

    @Column(name = "admin_contact_address", nullable = false, length = 300)
    private String adminContactAddress;

    @Column(name = "admin_contact_twitter", length = 300)
    private String adminContactTwitter;

    @Column(name = "admin_contact_github", length = 300)
    private String adminContactGithub;

    @Column(name = "admin_contact_linkedin", length = 300)
    private String adminContactLinkedin;

    @Column(name = "admin_contact_youtube", length = 300)
    private String adminContactYoutube;

    @ColumnDefault("false")
    @Column(name = "auto_approve_comment", nullable = false)
    private Boolean autoApproveComment = false;

    public Settings() {}

    public Settings(
            Long id,
            String adminContactName,
            String adminContactEmail,
            String adminContactAddress,
            String adminContactTwitter,
            String adminContactGithub,
            String adminContactLinkedin,
            String adminContactYoutube,
            Boolean autoApproveComment) {
        this.id = id;
        this.adminContactName = adminContactName;
        this.adminContactEmail = adminContactEmail;
        this.adminContactAddress = adminContactAddress;
        this.adminContactTwitter = adminContactTwitter;
        this.adminContactGithub = adminContactGithub;
        this.adminContactLinkedin = adminContactLinkedin;
        this.adminContactYoutube = adminContactYoutube;
        this.autoApproveComment = autoApproveComment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdminContactName() {
        return adminContactName;
    }

    public void setAdminContactName(String adminContactName) {
        this.adminContactName = adminContactName;
    }

    public String getAdminContactEmail() {
        return adminContactEmail;
    }

    public void setAdminContactEmail(String adminContactEmail) {
        this.adminContactEmail = adminContactEmail;
    }

    public String getAdminContactAddress() {
        return adminContactAddress;
    }

    public void setAdminContactAddress(String adminContactAddress) {
        this.adminContactAddress = adminContactAddress;
    }

    public String getAdminContactTwitter() {
        return adminContactTwitter;
    }

    public void setAdminContactTwitter(String adminContactTwitter) {
        this.adminContactTwitter = adminContactTwitter;
    }

    public String getAdminContactGithub() {
        return adminContactGithub;
    }

    public void setAdminContactGithub(String adminContactGithub) {
        this.adminContactGithub = adminContactGithub;
    }

    public String getAdminContactLinkedin() {
        return adminContactLinkedin;
    }

    public void setAdminContactLinkedin(String adminContactLinkedin) {
        this.adminContactLinkedin = adminContactLinkedin;
    }

    public String getAdminContactYoutube() {
        return adminContactYoutube;
    }

    public void setAdminContactYoutube(String adminContactYoutube) {
        this.adminContactYoutube = adminContactYoutube;
    }

    public Boolean getAutoApproveComment() {
        return autoApproveComment;
    }

    public void setAutoApproveComment(Boolean autoApproveComment) {
        this.autoApproveComment = autoApproveComment;
    }
}
