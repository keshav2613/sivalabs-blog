package com.sivalabs.blog.analytics.domain;

import com.sivalabs.blog.shared.entities.PageView;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PageViewRepository extends JpaRepository<PageView, Long> {

    @Query("SELECT COUNT(p) FROM PageView p WHERE p.path = :path AND p.createdAt >= :startTime")
    Long countViewsSince(@Param("path") String path, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT COUNT(p) FROM PageView p WHERE p.createdAt >= :startTime")
    Long countTotalViewsSince(@Param("startTime") LocalDateTime startTime);

    @Query("SELECT COUNT(DISTINCT p.sessionId) FROM PageView p WHERE p.path = :path AND p.createdAt >= :startTime")
    Long countUniqueVisitorsSince(@Param("path") String path, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT COUNT(DISTINCT p.sessionId) FROM PageView p WHERE p.createdAt >= :startTime")
    Long countTotalUniqueVisitorsSince(@Param("startTime") LocalDateTime startTime);

    @Query(
            "SELECT p.path, COUNT(p) as views FROM PageView p WHERE p.createdAt >= :startTime GROUP BY p.path ORDER BY views DESC")
    List<Object[]> findTopPagesSince(@Param("startTime") LocalDateTime startTime, Pageable pageable);

    @Query("SELECT p.path, COUNT(p) as views FROM PageView p GROUP BY p.path ORDER BY views DESC")
    List<Object[]> findTopPagesAllTime(Pageable pageable);

    @Query("SELECT COUNT(p) FROM PageView p WHERE p.path = :path")
    Long countTotalViewsForPath(@Param("path") String path);

    @Query("SELECT COUNT(DISTINCT p.sessionId) FROM PageView p WHERE p.path = :path")
    Long countTotalUniqueVisitorsForPath(@Param("path") String path);

    @Query("SELECT MAX(p.createdAt) FROM PageView p WHERE p.path = :path")
    LocalDateTime findLastViewedAt(@Param("path") String path);

    @Query("SELECT COUNT(p) FROM PageView p")
    Long countAllViews();

    @Query("SELECT COUNT(DISTINCT p.sessionId) FROM PageView p")
    Long countAllUniqueVisitors();

    @Query(
            "SELECT DATE(p.createdAt) as date, COUNT(p) as views FROM PageView p WHERE p.path = :path AND p.createdAt >= :startTime GROUP BY DATE(p.createdAt) ORDER BY date DESC")
    List<Object[]> findDailyViewsForPath(@Param("path") String path, @Param("startTime") LocalDateTime startTime);

    @Query(
            "SELECT DATE(p.createdAt) as date, COUNT(p) as views FROM PageView p WHERE p.createdAt >= :startTime GROUP BY DATE(p.createdAt) ORDER BY date DESC")
    List<Object[]> findDailyViewsTotal(@Param("startTime") LocalDateTime startTime);
}
