package com.sptek._frameworkWebCore.persistence.config;

import com.sptek._frameworkWebCore._annotation.Enable_JpaHybrid_At_Main;
import com.sptek._frameworkWebCore._annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

/**
 * MyBatis SqlSessionFactory, SqlSessionTemplate, 기본 transaction manager를 구성하는 설정.
 *
 * <p>공통 MyBatis 설정 XML과 mapper XML은 _projectCommonResources 하위에서 읽는다.
 * JPA hybrid 모드가 활성화되지 않은 경우에는 MyBatis/JDBC 기반 transactionManager도 함께 등록한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class MybatisConfig implements WebMvcConfigurer {
    private final ApplicationContext applicationContext;

    /**
     * 공통 datasource와 MyBatis XML 설정을 사용해 SqlSessionFactory를 구성한다.
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        //설정이 하나도 없는 경우 일부러 에러를 발생하기 위해 [0] 을 하드 코딩함(설정이 안되었음을 인지 할수 있도록)
        sqlSessionFactoryBean.setConfigLocation(this.applicationContext.getResources("classpath:/_projectCommonResources/mybatis/*-config.xml")[0]);
        // Resource[] resources = this.applicationContext.getResources("classpath:/_projectCommonResources/mybatis/*-config.xml");
        // if (resources.length > 0) {
        //    sqlSessionFactoryBean.setConfigLocation(resources[0]);
        // }

        // java 와 동일한 패키지 않에 xml 을 넗는 방식은 일반적이지 않으며 굳이 한다면 xml 위치의 패키지를 ClassPathResource 로 설정해줘야 함
        // 위 config.xml 을 통한 설정이 아니라 코딩으로 설정 가능
        // org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        // configuration.setMapUnderscoreToCamelCase(true);
        // configuration.setJdbcTypeForNull(JdbcType.NULL);
        // sqlSessionFactoryBean.setConfiguration(configuration);

        sqlSessionFactoryBean.setMapperLocations(this.applicationContext.getResources("classpath:/_projectCommonResources/mybatis/**/*Mapper.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    /**
     * MyBatis DAO에서 사용할 thread-safe SqlSessionTemplate을 등록한다.
     */
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * JPA hybrid 모드가 아닐 때 JDBC/MyBatis transaction manager를 등록한다.
     */
    @HasAnnotationOnMain_At_Bean(value = Enable_JpaHybrid_At_Main.class, negate = true)
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);

        // todo : 트렌젝션 정책 설정은 상황에 맞게 조절 필요!!
        dataSourceTransactionManager.setGlobalRollbackOnParticipationFailure(false);
        return dataSourceTransactionManager;
    }
}
