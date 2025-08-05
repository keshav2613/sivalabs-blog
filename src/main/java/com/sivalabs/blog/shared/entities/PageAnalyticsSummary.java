package com.sivalabs.blog.shared.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "page_analytics_summary")
public class PageAnalyticsSummary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "page_analytics_id_gen")
    @SequenceGenerator(name = "page_analytics_id_gen", sequenceName = "page_analytics_id_seq", initialValue = 100)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 500, unique = true)
    private String path;

    @Column(name = "total_views")
    private Long totalViews = 0L;

    @Column(name = "views_today")
    private Long viewsToday = 0L;

    @Column(name = "views_this_week")
    private Long viewsThisWeek = 0L;

    @Column(name = "views_this_month")
    private Long viewsThisMonth = 0L;

    @Column(name = "unique_visitors_total")
    private Long uniqueVisitorsTotal = 0L;

    @Column(name = "unique_visitors_today")
    private Long uniqueVisitorsToday = 0L;

    @Column(name = "unique_visitors_this_week")
    private Long uniqueVisitorsThisWeek = 0L;

    @Column(name = "unique_visitors_this_month")
    private Long uniqueVisitorsThisMonth = 0L;

    @Column(name = "last_viewed_at")
    private LocalDateTime lastViewedAt;

    public PageAnalyticsSummary() {}

    public PageAnalyticsSummary(String path) {
        this.path = path;
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

    public Long getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(Long totalViews) {
        this.totalViews = totalViews;
    }

    public Long getViewsToday() {
        return viewsToday;
    }

    public void setViewsToday(Long viewsToday) {
        this.viewsToday = viewsToday;
    }

    public Long getViewsThisWeek() {
        return viewsThisWeek;
    }

    public void setViewsThisWeek(Long viewsThisWeek) {
        this.viewsThisWeek = viewsThisWeek;
    }

    public Long getViewsThisMonth() {
        return viewsThisMonth;
    }

    public void setViewsThisMonth(Long viewsThisMonth) {
        this.viewsThisMonth = viewsThisMonth;
    }

    public Long getUniqueVisitorsTotal() {
        return uniqueVisitorsTotal;
    }

    public void setUniqueVisitorsTotal(Long uniqueVisitorsTotal) {
        this.uniqueVisitorsTotal = uniqueVisitorsTotal;
    }

    public Long getUniqueVisitorsToday() {
        return uniqueVisitorsToday;
    }

    public void setUniqueVisitorsToday(Long uniqueVisitorsToday) {
        this.uniqueVisitorsToday = uniqueVisitorsToday;
    }

    public Long getUniqueVisitorsThisWeek() {
        return uniqueVisitorsThisWeek;
    }

    public void setUniqueVisitorsThisWeek(Long uniqueVisitorsThisWeek) {
        this.uniqueVisitorsThisWeek = uniqueVisitorsThisWeek;
    }

    public Long getUniqueVisitorsThisMonth() {
        return uniqueVisitorsThisMonth;
    }

    public void setUniqueVisitorsThisMonth(Long uniqueVisitorsThisMonth) {
        this.uniqueVisitorsThisMonth = uniqueVisitorsThisMonth;
    }

    public LocalDateTime getLastViewedAt() {
        return lastViewedAt;
    }

    public void setLastViewedAt(LocalDateTime lastViewedAt) {
        this.lastViewedAt = lastViewedAt;
    }
}
