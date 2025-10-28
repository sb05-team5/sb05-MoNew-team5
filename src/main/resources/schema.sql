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
                           created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                           name VARCHAR(50) NOT NULL,
                           subscriber_count INT NOT NULL DEFAULT 0,
                           keyword VARCHAR(255) NOT NULL,
                           CONSTRAINT pk_interests PRIMARY KEY (id)
);

-- // 기사
CREATE TABLE articles (
                          id UUID NOT NULL,
                          source VARCHAR(255) NOT NULL,
                          source_url VARCHAR(255) NOT NULL UNIQUE,
                          title VARCHAR(255) NOT NULL,
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                          publish_date TIMESTAMP NOT NULL,
                          summary TEXT NOT NULL,
                          view_count INT NOT NULL,
                          deleted_at TIMESTAMP NULL,
                          interest_id UUID NOT NULL,
                          CONSTRAINT pk_articles PRIMARY KEY (id)


);
-- // 기사 백업 - 단순 백업을 위한 테이블이라 제약조건 최소화
CREATE TABLE article_backup (
                          id UUID ,
                          article_id UUID,
                          source VARCHAR(255) ,
                          source_url VARCHAR(255),
                          title VARCHAR(255),
                          created_at TIMESTAMP WITH TIME ZONE,
                          publish_date TIMESTAMP,
                          summary TEXT,
                          view_count INT,
                          deleted_at TIMESTAMP,
                          interest_id UUID,
                          CONSTRAINT pk_article_backup PRIMARY KEY (id)

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
                               deleted_at TIMESTAMP NULL,
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


-- ===============================
-- H2용 Spring Batch 5.x 스키마
-- ===============================

-- 시퀀스 생성
CREATE SEQUENCE BATCH_JOB_INSTANCE_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE BATCH_JOB_EXECUTION_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE BATCH_STEP_EXECUTION_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE BATCH_JOB_SEQ START WITH 1 INCREMENT BY 1;

-- ===============================
-- 테이블 생성
-- ===============================

CREATE TABLE BATCH_JOB_INSTANCE (
                                    JOB_INSTANCE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    VERSION BIGINT,
                                    JOB_NAME VARCHAR(100) NOT NULL,
                                    JOB_KEY VARCHAR(32) NOT NULL,
                                    UNIQUE (JOB_NAME, JOB_KEY)
);

CREATE TABLE BATCH_JOB_EXECUTION (
                                     JOB_EXECUTION_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     VERSION BIGINT NOT NULL,
                                     JOB_INSTANCE_ID BIGINT NOT NULL,
                                     CREATE_TIME TIMESTAMP NOT NULL,
                                     START_TIME TIMESTAMP,
                                     END_TIME TIMESTAMP,
                                     STATUS VARCHAR(10),
                                     EXIT_CODE VARCHAR(20),
                                     EXIT_MESSAGE VARCHAR(2500),
                                     LAST_UPDATED TIMESTAMP,
                                     JOB_CONFIGURATION_LOCATION VARCHAR(2500),
                                     CONSTRAINT JOB_INST_EXEC_FK FOREIGN KEY(JOB_INSTANCE_ID)
                                         REFERENCES BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION (
                                      STEP_EXECUTION_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      VERSION BIGINT NOT NULL,
                                      STEP_NAME VARCHAR(100) NOT NULL,
                                      JOB_EXECUTION_ID BIGINT NOT NULL,
                                      START_TIME TIMESTAMP,
                                      END_TIME TIMESTAMP,
                                      STATUS VARCHAR(10),
                                      COMMIT_COUNT BIGINT,
                                      READ_COUNT BIGINT,
                                      FILTER_COUNT BIGINT,
                                      WRITE_COUNT BIGINT,
                                      EXIT_CODE VARCHAR(100),
                                      EXIT_MESSAGE VARCHAR(2500),
                                      READ_SKIP_COUNT BIGINT,
                                      WRITE_SKIP_COUNT BIGINT,
                                      PROCESS_SKIP_COUNT BIGINT,
                                      ROLLBACK_COUNT BIGINT,
                                      LAST_UPDATED TIMESTAMP,
                                      CREATE_TIME TIMESTAMP,
                                      CONSTRAINT JOB_EXECUTION_FK FOREIGN KEY(JOB_EXECUTION_ID)
                                          REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_JOB_EXECUTION_PARAMS (
                                            JOB_EXECUTION_ID BIGINT NOT NULL,
                                            PARAMETER_NAME VARCHAR(100) NOT NULL,
                                            PARAMETER_TYPE VARCHAR(1000) NOT NULL,
                                            PARAMETER_VALUE VARCHAR(2500),
                                            IDENTIFYING CHAR(1) NOT NULL,
                                            CONSTRAINT JOB_EXEC_PARAMS_FK FOREIGN KEY(JOB_EXECUTION_ID)
                                                REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT (
                                              STEP_EXECUTION_ID BIGINT PRIMARY KEY,
                                              SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                              SERIALIZED_CONTEXT CLOB,
                                              CONSTRAINT STEP_EXEC_CTX_FK FOREIGN KEY(STEP_EXECUTION_ID)
                                                  REFERENCES BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
);

CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT (
                                             JOB_EXECUTION_ID BIGINT PRIMARY KEY,
                                             SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                             SERIALIZED_CONTEXT CLOB,
                                             CONSTRAINT JOB_EXEC_CTX_FK FOREIGN KEY(JOB_EXECUTION_ID)
                                                 REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);


-- --batch용 (postgreSQL 버전)
-- -- ===============================
-- -- Create Spring Batch tables (PostgreSQL, Spring Batch 5.x)
-- -- Create sequences
-- -- ===============================
-- CREATE SEQUENCE BATCH_JOB_INSTANCE_SEQ START WITH 1 INCREMENT BY 1;
-- CREATE SEQUENCE BATCH_JOB_EXECUTION_SEQ START WITH 1 INCREMENT BY 1;
-- CREATE SEQUENCE BATCH_STEP_EXECUTION_SEQ START WITH 1 INCREMENT BY 1;
-- CREATE SEQUENCE BATCH_JOB_SEQ START WITH 1 INCREMENT BY 1;
--
-- -- ===============================
-- -- 2. Create tables
-- -- ===============================
--
--
-- -- Job Instance
-- CREATE TABLE BATCH_JOB_INSTANCE (
--                                     JOB_INSTANCE_ID BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('BATCH_JOB_INSTANCE_SEQ'),
--                                     VERSION BIGINT,
--                                     JOB_NAME VARCHAR(100) NOT NULL,
--                                     JOB_KEY VARCHAR(32) NOT NULL,
--                                     UNIQUE (JOB_NAME, JOB_KEY)
-- );
--
-- -- Job Execution
-- CREATE TABLE BATCH_JOB_EXECUTION (
--                                      JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('BATCH_JOB_EXECUTION_SEQ'),
--                                      VERSION BIGINT NOT NULL,
--                                      JOB_INSTANCE_ID BIGINT NOT NULL,
--                                      CREATE_TIME TIMESTAMP NOT NULL,
--                                      START_TIME TIMESTAMP,
--                                      END_TIME TIMESTAMP,
--                                      STATUS VARCHAR(10),
--                                      EXIT_CODE VARCHAR(20),
--                                      EXIT_MESSAGE VARCHAR(2500),
--                                      LAST_UPDATED TIMESTAMP,
--                                      JOB_CONFIGURATION_LOCATION VARCHAR(2500),
--                                      CONSTRAINT JOB_INST_EXEC_FK FOREIGN KEY(JOB_INSTANCE_ID)
--                                          REFERENCES BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
-- );
--
-- -- Job Execution Parameters (Spring Batch 5.x)
-- CREATE TABLE BATCH_JOB_EXECUTION_PARAMS (
--                                             JOB_EXECUTION_ID BIGINT NOT NULL,
--                                             PARAMETER_NAME VARCHAR(100) NOT NULL,
--                                             PARAMETER_TYPE VARCHAR(1000) NOT NULL,
--                                             PARAMETER_VALUE VARCHAR(2500),
--                                             IDENTIFYING CHAR(1) NOT NULL,
--                                             CONSTRAINT JOB_EXEC_PARAMS_FK FOREIGN KEY(JOB_EXECUTION_ID)
--                                                 REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
-- );
--
-- -- Step Execution
-- CREATE TABLE BATCH_STEP_EXECUTION (
--                                       STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('BATCH_STEP_EXECUTION_SEQ'),
--                                       VERSION BIGINT NOT NULL,
--                                       STEP_NAME VARCHAR(100) NOT NULL,
--                                       JOB_EXECUTION_ID BIGINT NOT NULL,
--                                       START_TIME TIMESTAMP,
--                                       END_TIME TIMESTAMP,
--                                       STATUS VARCHAR(10),
--                                       COMMIT_COUNT BIGINT,
--                                       READ_COUNT BIGINT,
--                                       FILTER_COUNT BIGINT,
--                                       WRITE_COUNT BIGINT,
--                                       EXIT_CODE VARCHAR(100),
--                                       EXIT_MESSAGE VARCHAR(2500),
--                                       READ_SKIP_COUNT BIGINT,
--                                       WRITE_SKIP_COUNT BIGINT,
--                                       PROCESS_SKIP_COUNT BIGINT,
--                                       ROLLBACK_COUNT BIGINT,
--                                       LAST_UPDATED TIMESTAMP,
--                                       CREATE_TIME TIMESTAMP,
--                                       CONSTRAINT JOB_EXECUTION_FK FOREIGN KEY (JOB_EXECUTION_ID)
--                                           REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
-- );
-- -- Step Execution Context
-- CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT (
--                                               STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
--                                               SHORT_CONTEXT VARCHAR(2500) NOT NULL,
--                                               SERIALIZED_CONTEXT TEXT,
--                                               CONSTRAINT STEP_EXEC_CTX_FK FOREIGN KEY(STEP_EXECUTION_ID)
--                                                   REFERENCES BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
-- );
--
-- -- Job Execution Context
-- CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT (
--                                              JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
--                                              SHORT_CONTEXT VARCHAR(2500) NOT NULL,
--                                              SERIALIZED_CONTEXT TEXT,
--                                              CONSTRAINT JOB_EXEC_CTX_FK FOREIGN KEY(JOB_EXECUTION_ID)
--                                                  REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
-- );
--
-- -- ===============================
-- -- Drop existing Spring Batch tables and sequences
-- -- ===============================
-- -- DROP TABLE IF EXISTS batch_job_execution_context CASCADE;
-- -- DROP TABLE IF EXISTS batch_step_execution_context CASCADE;
-- -- DROP TABLE IF EXISTS batch_step_execution CASCADE;
-- -- DROP TABLE IF EXISTS batch_job_execution_params CASCADE;
-- -- DROP TABLE IF EXISTS batch_job_execution CASCADE;
-- -- DROP TABLE IF EXISTS batch_job_instance CASCADE;
-- --
-- -- DROP SEQUENCE IF EXISTS batch_job_instance_seq;
-- -- DROP SEQUENCE IF EXISTS batch_job_execution_seq;
-- -- DROP SEQUENCE IF EXISTS batch_step_execution_seq;
-- -- DROP SEQUENCE IF EXISTS BATCH_JOB_SEQ CASCADE;

