package com._sptek.__webFramework.data.jpa;

import com._sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MyBatis와 JPA를 함께 사용하는 hybrid persistence 모드의 JPA Bean 설정.
 *
 * <p>메인 클래스에 {@link Enable_JpaHybrid_At_Main}가 있을 때만 활성화되며,
 * 동일한 transactionManager Bean 이름으로 JpaTransactionManager를 등록해 JPA transaction 기준으로 동작하게 한다.</p>
 *
 * <p>중요!! 같은 서비스 흐름에서 JPA와 MyBatis가 같은 aggregate/table을 동시에 수정하면 JPA 영속성 컨텍스트와
 * MyBatis 직접 SQL 사이의 상태가 어긋날 수 있으므로, 업무 코드는 같은 aggregate/table을 한 방식으로만 수정하는 것을 원칙으로 한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@HasAnnotationOnMain_At_Bean(Enable_JpaHybrid_At_Main.class)
@Configuration
public class JpaHybridConfig implements WebMvcConfigurer {

    private final ApplicationContext applicationContext;
    private final Environment environment;

    /**
     * hybrid 모드에서 사용할 JPA transaction manager를 등록한다.
     */
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(localContainerEntityManagerFactoryBean.getObject());

        // todo : 트렌젝션 정책 설정은 상황에 맞게 조절 필요!!
        jpaTransactionManager.setGlobalRollbackOnParticipationFailure(false);
        return jpaTransactionManager;
    }

    /**
     * 공통 datasource와 메인 클래스의 {@code @EntityScan} 선언을 사용해 EntityManagerFactory를 구성한다.
     *
     * <p>repository scan은 메인 클래스의 {@code @EnableJpaRepositories}에서, entity scan은
     * {@code @EntityScan}에서 함께 선언해 실행 애플리케이션의 JPA 경계를 한 곳에서 확인할 수 있게 한다.</p>
     */
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setPackagesToScan(resolveEntityPackagesToScan());

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaPropertyMap(resolveJpaProperties());

        return localContainerEntityManagerFactoryBean;
    }

    private String[] resolveEntityPackagesToScan() {
        List<String> packageNames = EntityScanPackages.get(applicationContext).getPackageNames();
        if (packageNames.isEmpty()) {
            throw new IllegalStateException("@Enable_JpaHybrid_At_Main requires @EntityScan on the main application class.");
        }
        return packageNames.toArray(String[]::new);
    }

    private Map<String, Object> resolveJpaProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.jpa.hibernate.ddl-auto", "none"));
        properties.put("hibernate.show_sql", environment.getProperty("spring.jpa.show-sql", "false"));

        String dialect = environment.getProperty("spring.jpa.database-platform");
        if (StringUtils.hasText(dialect)) {
            properties.put("hibernate.dialect", dialect);
        }
        return properties;
    }

}
