SELECT *
FROM oracle.activity as a
 LEFT JOIN postgres.client as c ON a.client_id = c.id
LIMIT 1000
