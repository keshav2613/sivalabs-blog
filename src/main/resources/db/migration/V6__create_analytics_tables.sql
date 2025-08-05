create sequence page_view_id_seq start with 100 increment by 50;

CREATE TABLE page_views
(
    id         BIGINT       NOT NULL DEFAULT nextval('page_view_id_seq'),
    path       VARCHAR(500) NOT NULL,
    title      VARCHAR(500),
    referer    VARCHAR(1000),
    user_agent TEXT,
    ip_address VARCHAR(45),
    session_id VARCHAR(64),
    user_id    BIGINT,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    primary key (id)
);

-- Create indexes for better query performance
CREATE INDEX idx_page_views_path ON page_views (path);
CREATE INDEX idx_page_views_created_at ON page_views (created_at);
CREATE INDEX idx_page_views_user_id ON page_views (user_id);
CREATE INDEX idx_page_views_session_id ON page_views (session_id);
CREATE INDEX idx_page_views_path_created_at ON page_views (path, created_at);


-- Page analytics summary for quick access
create sequence page_analytics_id_seq start with 100 increment by 50;

CREATE TABLE page_analytics_summary
(
    id                         BIGINT       NOT NULL DEFAULT nextval('page_analytics_id_seq'),
    path                       VARCHAR(500) NOT NULL UNIQUE,
    total_views                BIGINT                DEFAULT 0,
    views_today                BIGINT                DEFAULT 0,
    views_this_week            BIGINT                DEFAULT 0,
    views_this_month           BIGINT                DEFAULT 0,
    unique_visitors_total      BIGINT                DEFAULT 0,
    unique_visitors_today      BIGINT                DEFAULT 0,
    unique_visitors_this_week  BIGINT                DEFAULT 0,
    unique_visitors_this_month BIGINT                DEFAULT 0,
    last_viewed_at             TIMESTAMP,
    created_at                 TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                 TIMESTAMP,
    primary key (id)
);

-- Create indexes for aggregation tables
CREATE INDEX idx_page_analytics_summary_path ON page_analytics_summary (path);