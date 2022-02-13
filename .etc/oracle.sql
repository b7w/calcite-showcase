-- Create database
CREATE USER db1 IDENTIFIED BY q1w2e3r4;
GRANT CREATE SESSION TO db1;
GRANT CREATE TABLE TO db1;
GRANT UNLIMITED TABLESPACE TO db1;

-- Create table
CREATE TABLE db1.activity
(
    id            INTEGER      NOT NULL PRIMARY KEY,
    client_id     INTEGER      NOT NULL,
    system        VARCHAR(256) NOT NULL,
    creation_time TIMESTAMP    NOT NULL
);

-- Generate 1000000 records
INSERT INTO db1.activity
SELECT ser.i,
       dbms_random.value(1, 10000),
       dbms_random.string('L', 4),
       SYSDATE + dbms_random.value(0, SYSDATE - SYSDATE + 360)
FROM (SELECT (level - 1) AS i FROM dual CONNECT BY level - 1 < 1000000) ser;