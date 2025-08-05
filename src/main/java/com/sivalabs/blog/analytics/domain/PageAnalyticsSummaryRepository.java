package com.sivalabs.blog.analytics.domain;

import com.sivalabs.blog.shared.entities.PageAnalyticsSummary;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PageAnalyticsSummaryRepository extends JpaRepository<PageAnalyticsSummary, Long> {

    Optional<PageAnalyticsSummary> findByPath(String path);

    @Query("SELECT p FROM PageAnalyticsSummary p ORDER BY p.totalViews DESC")
    List<PageAnalyticsSummary> findTopPagesByTotalViews(Pageable pageable);

    @Query("SELECT p FROM PageAnalyticsSummary p ORDER BY p.viewsToday DESC")
    List<PageAnalyticsSummary> findTopPagesByViewsToday(Pageable pageable);

    @Query("SELECT p FROM PageAnalyticsSummary p ORDER BY p.viewsThisWeek DESC")
    List<PageAnalyticsSummary> findTopPagesByViewsThisWeek(Pageable pageable);

    @Query("SELECT p FROM PageAnalyticsSummary p ORDER BY p.viewsThisMonth DESC")
    List<PageAnalyticsSummary> findTopPagesByViewsThisMonth(Pageable pageable);

    @Query("SELECT SUM(p.totalViews) FROM PageAnalyticsSummary p")
    Long sumTotalViews();

    @Query("SELECT SUM(p.viewsToday) FROM PageAnalyticsSummary p")
    Long sumViewsToday();

    @Query("SELECT SUM(p.viewsThisWeek) FROM PageAnalyticsSummary p")
    Long sumViewsThisWeek();

    @Query("SELECT SUM(p.viewsThisMonth) FROM PageAnalyticsSummary p")
    Long sumViewsThisMonth();

    @Query("SELECT SUM(p.uniqueVisitorsTotal) FROM PageAnalyticsSummary p")
    Long sumUniqueVisitorsTotal();

    @Query("SELECT SUM(p.uniqueVisitorsToday) FROM PageAnalyticsSummary p")
    Long sumUniqueVisitorsToday();

    @Query("SELECT SUM(p.uniqueVisitorsThisWeek) FROM PageAnalyticsSummary p")
    Long sumUniqueVisitorsThisWeek();

    @Query("SELECT SUM(p.uniqueVisitorsThisMonth) FROM PageAnalyticsSummary p")
    Long sumUniqueVisitorsThisMonth();
}
