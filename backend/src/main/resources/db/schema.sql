-- 热点总结表
CREATE TABLE IF NOT EXISTS hot_summaries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    platform_id INTEGER NOT NULL,
    content TEXT NOT NULL,
    fetch_date VARCHAR(255) NOT NULL,
    generated_time TIMESTAMP NOT NULL,
    FOREIGN KEY (platform_id) REFERENCES platforms(id)
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_summary_platform_date ON hot_summaries(platform_id, fetch_date);
CREATE INDEX IF NOT EXISTS idx_summary_fetch_date ON hot_summaries(fetch_date); 