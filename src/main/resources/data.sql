-- noinspection SqlResolveForFile
INSERT INTO article(id, title, content)
VALUES (1, '가가가가', '1111');
INSERT INTO article(id, title, content)
VALUES (2, '나나나나', '2222');
INSERT INTO article(id, title, content)
VALUES (3, '다다다다', '3333');

-- article 더미 데이터
INSERT INTO article(id, title, content)
VALUES (4, '당신의 인생 영화는?', '댓글ㄱ');
INSERT INTO article(id, title, content)
VALUES (5, '당신의 인생 푸드는?', '댓글ㄱㄱ');
INSERT INTO article(id, title, content)
VALUES (6, '당신의 취미는?', '댓글ㄱㄱㄱ');

-- comment 더미 데이터
---- 1개 부모인 [4번 게시글(당신의 인생영화는?)]에 대한 댓글들
---- id 다음으로 fk를 넣고 고정시켜서 생성한다.
INSERT INTO comment (id, article_id, nickname, body)
VALUES (1, 4, 'Park', '굳 월 헌팅');
INSERT INTO comment (id, article_id, nickname, body)
VALUES (2, 4, 'Kim', '아이 엠 샘');
INSERT INTO comment (id, article_id, nickname, body)
VALUES (3, 4, 'Choi', '쇼생크 탈출');

---- 1개 부모인 [5번 게시글(당신의 인생 푸드는?)]에 대한 댓글들
INSERT INTO comment (id, article_id, nickname, body)
VALUES (4, 5, 'Park', '치킨');
INSERT INTO comment (id, article_id, nickname, body)
VALUES (5, 5, 'Kim', '샤브샤브');
INSERT INTO comment (id, article_id, nickname, body)
VALUES (6, 5, 'Choi', '초밥');

---- 1개 부모인 [6번 게시글(당신의 취미는?)]에 대한 댓글들
INSERT INTO comment (id, article_id, nickname, body)
VALUES (7, 6, 'Park', '조깅');
INSERT INTO comment (id, article_id, nickname, body)
VALUES (8, 6, 'Kim', '유튜브');
INSERT INTO comment (id, article_id, nickname, body)
VALUES (9, 6, 'Choi', '독서');
