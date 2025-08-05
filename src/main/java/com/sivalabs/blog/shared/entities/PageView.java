package com.sivalabs.blog.shared.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "page_views")
public class PageView extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "page_view_id_gen")
    @SequenceGenerator(name = "page_view_id_gen", sequenceName = "page_view_id_seq", initialValue = 100)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 500)
    private String path;

    @Column(length = 500)
    private String title;

    @Column(length = 1000)
    private String referer;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "session_id", length = 64)
    private String sessionId;

    @Column(name = "user_id")
    private Long userId;

    public PageView() {}

    public PageView(
            String path,
            String title,
            String referer,
            String userAgent,
            String ipAddress,
            String sessionId,
            Long userId) {
        this.path = path;
        this.title = title;
        this.referer = referer;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.sessionId = sessionId;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
