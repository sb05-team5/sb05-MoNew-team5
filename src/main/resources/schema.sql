-- // 자식 테이블부터 삭제 (외래키 종속 때문에)
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS subscriptions CASCADE;
DROP TABLE IF EXISTS comment_likes CASCADE;
DROP TABLE IF EXISTS article_views CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

-- // 테이블 삭제
DROP TABLE IF EXISTS articles CASCADE;
DROP TABLE IF EXISTS interests CASCADE;
DROP TABLE IF EXISTS users CASCADE;


-- // 사용자
CREATE TABLE users (
                       id UUID NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       nickname VARCHAR(20) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT now(),
                       deleted_at TIMESTAMP NULL,
                       CONSTRAINT pk_users PRIMARY KEY (id)
);


-- // 관심사
CREATE TABLE interests (
                           id UUID NOT NULL,
                           name VARCHAR(50) NOT NULL,
                           subscriber_count INT NOT NULL DEFAULT 0,
                           keyword VARCHAR(255) NOT NULL,
                           CONSTRAINT pk_interests PRIMARY KEY (id)
);

-- // 기사
CREATE TABLE articles (
                          id UUID NOT NULL,
                          source VARCHAR(255) NOT NULL,
                          source_url VARCHAR(255) NOT NULL,
                          title VARCHAR(255) NOT NULL,
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                          publish_date TIMESTAMP NOT NULL,
                          summary TEXT NOT NULL,
                          view_count INT NOT NULL,
                          deleted_at TIMESTAMP NULL,
                          interest_id UUID NOT NULL,
                          CONSTRAINT pk_articles PRIMARY KEY (id)


);

-- // 댓글
CREATE TABLE comments (
                          id UUID NOT NULL,
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                          deleted_at TIMESTAMP WITH TIME ZONE NULL,
                          content VARCHAR(1000) NOT NULL,
                          article_id UUID NOT NULL,
                          user_id UUID NOT NULL,
                          CONSTRAINT pk_comments PRIMARY KEY (id),
                          CONSTRAINT fk_comments_articles FOREIGN KEY (article_id)
                              REFERENCES articles (id) ON DELETE CASCADE,
                          CONSTRAINT fk_comments_users FOREIGN KEY (user_id)
                              REFERENCES users (id) ON DELETE CASCADE
);

-- // 댓글 좋아요
CREATE TABLE comment_likes (
                               id UUID NOT NULL,
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                               deleted_at TIMESTAMP WITH TIME ZONE NULL,
                               comment_id UUID NOT NULL,
                               CONSTRAINT pk_comment_likes PRIMARY KEY (id),
                               CONSTRAINT fk_comment_likes_comments FOREIGN KEY (comment_id)
                                   REFERENCES comments (id) ON DELETE CASCADE
);

-- // 조회수
CREATE TABLE article_views (
                               id UUID NOT NULL,
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                               article_id UUID NOT NULL,
                               viewed_by UUID NOT NULL,
                               CONSTRAINT pk_article_views PRIMARY KEY (id),
                               CONSTRAINT fk_article_views_articles FOREIGN KEY (article_id)
                                   REFERENCES articles (id) ON DELETE CASCADE,
                               CONSTRAINT fk_article_views_users FOREIGN KEY (viewed_by)
                                   REFERENCES users (id)
);

-- // 구독
CREATE TABLE subscriptions (
                               id UUID NOT NULL,
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                               user_id UUID NOT NULL,
                               interest_id UUID NOT NULL,
                               CONSTRAINT pk_subscriptions PRIMARY KEY (id),
                               CONSTRAINT fk_subscriptions_users FOREIGN KEY (user_id)
                                   REFERENCES users (id),
                               CONSTRAINT fk_subscriptions_interests FOREIGN KEY (interest_id)
                                   REFERENCES interests (id) ON DELETE CASCADE
);

-- // 알림
CREATE TABLE notifications (
                               id UUID NOT NULL,
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                               updated_at TIMESTAMP WITH TIME ZONE NULL,
                               confirmed BOOLEAN NOT NULL DEFAULT FALSE,
                               content VARCHAR(255) NOT NULL,
                               resource_type VARCHAR(10) NOT NULL,
                               resource_id UUID NOT NULL,
                               user_id UUID NOT NULL,
                               CONSTRAINT pk_notifications PRIMARY KEY (id),
                               CONSTRAINT fk_notifications_users FOREIGN KEY (user_id)
                                   REFERENCES users (id) ON DELETE CASCADE
);