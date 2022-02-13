package me.b7w.calcite.showcase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Primary
    @ConfigurationProperties("datasource.oracle")
    public DataSource oracleDatasource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("datasource.postgres")
    public DataSource postgresDatasource() {
        return DataSourceBuilder.create().build();
    }



}
