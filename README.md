# Calcite multi datasource showcase

Showcase of oracle and postgres tables sql join with help of calcite

## Start guide

* Run `docker-compose up -d; docker-compose up logs -f;`
* Waite 10-15 minutes for oracle
* Build artifact `mvn clean package`
* Start application `java -jar target/calcite-0.0.1-SNAPSHOT.jar`
* Execute query.sql file `curl -X POST -H 'Content-Type: application/json' http://127.0.0.1:8080/api/query-template --data-binary @query.sql`


## About

Look, feel, be happy :-)
