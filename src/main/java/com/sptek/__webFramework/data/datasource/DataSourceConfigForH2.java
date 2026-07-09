package com.sptek.__webFramework.data.datasource;


import com.sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

//@Profile(value = { "!prd" }) //prd가 아닐때
@Profile(value = { "local"})
@Slf4j
@HasAnnotationOnMain_At_Bean(Enable_DatasourceOfH2_At_Main.class)
@Configuration
public class DataSourceConfigForH2 {

    @Bean(name = "dataSource")
    public DataSource dataSource(
            @Value("${h2.datasource.driverClassName}") String driverClassName
            , @Value("${h2.datasource.jdbcUrl}") String url
            , @Value("${h2.datasource.userName}") String username
            , @Value("${h2.datasource.password}") String password) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
