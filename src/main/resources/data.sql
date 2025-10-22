// users
INSERT INTO users (id, email, nickname, password, created_at, deleted_at) VALUES
('11111111-1111-1111-1111-111111111111', 'user1@example.com', 'user1', 'password1', now(), NULL),
('22222222-2222-2222-2222-222222222222', 'user2@example.com', 'user2', 'password2', now(), NULL),
('33333333-3333-3333-3333-333333333333', 'user3@example.com', 'user3', 'password3', now(), NULL),
('44444444-4444-4444-4444-444444444444', 'user4@example.com', 'user4', 'password4', now(), NULL),
('55555555-5555-5555-5555-555555555555', 'user5@example.com', 'user5', 'password5', now(), NULL);

// interests
INSERT INTO interests (id, name, subscriber_count, keyword) VALUES
('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'Technology', 100, 'tech, ai, programming'),
('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'Sports', 200, 'soccer, basketball, olympics'),
('aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'Finance', 150, 'stock, crypto, economy'),
('aaaaaaa4-aaaa-aaaa-aaaa-aaaaaaaaaaa4', 'Health', 80, 'fitness, nutrition, wellness'),
('aaaaaaa5-aaaa-aaaa-aaaa-aaaaaaaaaaa5', 'Travel', 120, 'tourism, adventure, travel');

// articles
INSERT INTO articles (id, source, source_url, title, publish_date, summary, view_count, deleted_at, interest_id) VALUES
('bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'TechDaily', 'https://techdaily.com/article1', 'AI Advances in 2025', now(), 'Summary of AI advances...', 1000, NULL, 'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
('bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 'SportsNews', 'https://sportsnews.com/article2', 'Soccer World Cup Highlights', now(), 'Summary of world cup...', 800, NULL, 'aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2'),
('bbbbbbb3-bbbb-bbbb-bbbb-bbbbbbbbbbb3', 'FinanceToday', 'https://financetoday.com/article3', 'Stock Market Update', now(), 'Summary of stocks...', 600, NULL, 'aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaa3'),
('bbbbbbb4-bbbb-bbbb-bbbb-bbbbbbbbbbb4', 'HealthMagazine', 'https://healthmag.com/article4', '10 Tips for Wellness', now(), 'Summary of wellness tips...', 400, NULL, 'aaaaaaa4-aaaa-aaaa-aaaa-aaaaaaaaaaa4'),
('bbbbbbb5-bbbb-bbbb-bbbb-bbbbbbbbbbb5', 'TravelGuide', 'https://travelguide.com/article5', 'Top 5 Destinations', now(), 'Summary of travel...', 500, NULL, 'aaaaaaa5-aaaa-aaaa-aaaa-aaaaaaaaaaa5');

// comments
INSERT INTO comments (id, created_at, deleted_at, content, article_id, user_id) VALUES
('ccccccc1-cccc-cccc-cccc-ccccccccccc1', now(), NULL, 'Great article!', 'bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbb1', '11111111-1111-1111-1111-111111111111'),
('ccccccc2-cccc-cccc-cccc-ccccccccccc2', now(), NULL, 'Interesting read', 'bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbb2', '22222222-2222-2222-2222-222222222222'),
('ccccccc3-cccc-cccc-cccc-ccccccccccc3', now(), NULL, 'Helpful information', 'bbbbbbb3-bbbb-bbbb-bbbb-bbbbbbbbbbb3', '33333333-3333-3333-3333-333333333333'),
('ccccccc4-cccc-cccc-cccc-ccccccccccc4', now(), NULL, 'I learned a lot', 'bbbbbbb4-bbbb-bbbb-bbbb-bbbbbbbbbbb4', '44444444-4444-4444-4444-444444444444'),
('ccccccc5-cccc-cccc-cccc-ccccccccccc5', now(), NULL, 'Nice tips', 'bbbbbbb5-bbbb-bbbb-bbbb-bbbbbbbbbbb5', '55555555-5555-5555-5555-555555555555');

// comment_likes
INSERT INTO COMMENT_LIKES (ID, CREATED_AT, DELETED_AT, COMMENT_ID) VALUES
(CAST('a1111111-aaaa-aaaa-aaaa-aaaaaaaaaaa1' AS UUID), CURRENT_TIMESTAMP, NULL, CAST('ccccccc1-cccc-cccc-cccc-ccccccccccc1' AS UUID)),
(CAST('a2222222-aaaa-aaaa-aaaa-aaaaaaaaaaa2' AS UUID), CURRENT_TIMESTAMP, NULL, CAST('ccccccc1-cccc-cccc-cccc-ccccccccccc1' AS UUID)),
(CAST('a3333333-aaaa-aaaa-aaaa-aaaaaaaaaaa3' AS UUID), CURRENT_TIMESTAMP, NULL, CAST('ccccccc2-cccc-cccc-cccc-ccccccccccc2' AS UUID)),
(CAST('a4444444-aaaa-aaaa-aaaa-aaaaaaaaaaa4' AS UUID), CURRENT_TIMESTAMP, NULL, CAST('ccccccc3-cccc-cccc-cccc-ccccccccccc3' AS UUID)),
(CAST('a5555555-aaaa-aaaa-aaaa-aaaaaaaaaaa5' AS UUID), CURRENT_TIMESTAMP, NULL, CAST('ccccccc4-cccc-cccc-cccc-ccccccccccc4' AS UUID));

// article_views
INSERT INTO article_views (id, created_at, article_id, viewed_by) VALUES
('eeeeeee1-eeee-eeee-eeee-eeeeeeeeeee1', now(), 'bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbb1', '11111111-1111-1111-1111-111111111111'),
('eeeeeee2-eeee-eeee-eeee-eeeeeeeeeee2', now(), 'bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbb2', '22222222-2222-2222-2222-222222222222'),
('eeeeeee3-eeee-eeee-eeee-eeeeeeeeeee3', now(), 'bbbbbbb3-bbbb-bbbb-bbbb-bbbbbbbbbbb3', '33333333-3333-3333-3333-333333333333'),
('eeeeeee4-eeee-eeee-eeee-eeeeeeeeeee4', now(), 'bbbbbbb4-bbbb-bbbb-bbbb-bbbbbbbbbbb4', '44444444-4444-4444-4444-444444444444'),
('eeeeeee5-eeee-eeee-eeee-eeeeeeeeeee5', now(), 'bbbbbbb5-bbbb-bbbb-bbbb-bbbbbbbbbbb5', '55555555-5555-5555-5555-555555555555');

// subscriptions
INSERT INTO subscriptions (id, created_at, user_id, interest_id) VALUES
('fffffff1-ffff-ffff-ffff-fffffffffff1', now(), '11111111-1111-1111-1111-111111111111', 'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1'),
('fffffff2-ffff-ffff-ffff-fffffffffff2', now(), '22222222-2222-2222-2222-222222222222', 'aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2'),
('fffffff3-ffff-ffff-ffff-fffffffffff3', now(), '33333333-3333-3333-3333-333333333333', 'aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaa3'),
('fffffff4-ffff-ffff-ffff-fffffffffff4', now(), '44444444-4444-4444-4444-444444444444', 'aaaaaaa4-aaaa-aaaa-aaaa-aaaaaaaaaaa4'),
('fffffff5-ffff-ffff-ffff-fffffffffff5', now(), '55555555-5555-5555-5555-555555555555', 'aaaaaaa5-aaaa-aaaa-aaaa-aaaaaaaaaaa5');

// notifications
INSERT INTO notifications (id, created_at, updated_at, confirmed, content, resource_type, resource_id, user_id) VALUES
(CAST('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1' AS UUID), CURRENT_TIMESTAMP, NULL, FALSE,'New comment on your article', 'comment',
    CAST('ccccccc1-cccc-cccc-cccc-ccccccccccc1' AS UUID), CAST('11111111-1111-1111-1111-111111111111' AS UUID)),
(CAST('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2' AS UUID), CURRENT_TIMESTAMP, NULL, FALSE,'Article you follow has new update', 'article',
    CAST('bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbb2' AS UUID), CAST('11111111-1111-1111-1111-111111111111' AS UUID)),
(CAST('aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaa3' AS UUID), CURRENT_TIMESTAMP, NULL, FALSE,'Subscription interest updated', 'interest',
    CAST('aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaa3' AS UUID), CAST('11111111-1111-1111-1111-111111111111' AS UUID)),
(CAST('aaaaaaa4-aaaa-aaaa-aaaa-aaaaaaaaaaa4' AS UUID), CURRENT_TIMESTAMP, NULL, FALSE,'Your comment was liked', 'comment',
    CAST('ccccccc4-cccc-cccc-cccc-ccccccccccc4' AS UUID), CAST('11111111-1111-1111-1111-111111111111' AS UUID)),
(CAST('aaaaaaa5-aaaa-aaaa-aaaa-aaaaaaaaaaa5' AS UUID), CURRENT_TIMESTAMP, NULL, FALSE,'New article published', 'article',
    CAST('bbbbbbb5-bbbb-bbbb-bbbb-bbbbbbbbbbb5' AS UUID), CAST('11111111-1111-1111-1111-111111111111' AS UUID));