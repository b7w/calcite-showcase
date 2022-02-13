-- Create table
DROP TABLE IF EXISTS clients;

CREATE TABLE client
(
    id            BIGINT       NOT NULL PRIMARY KEY,
    name          VARCHAR(256) NOT NULL,
    email         VARCHAR(256) NOT NULL,
    creation_time TIMESTAMP    NOT NULL
);

-- Generate 10000 records
INSERT INTO client
SELECT i, MD5(RANDOM()::TEXT), i || '@mail.com', NOW() + (RANDOM() * (NOW() + '90 days' - NOW())) + '30 days'
FROM GENERATE_SERIES(1, 10000) AS i;
