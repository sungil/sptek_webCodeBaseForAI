package com._sptek.__webFramework.data.jpa;

import com._sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * MyBatis와 JPA를 함께 사용하는 hybrid persistence 모드의 JPA Bean 설정.
 *
 * <p>메인 클래스에 {@link Enable_JpaHybrid_At_Main}가 있을 때만 활성화되며,
 * 동일한 transactionManager Bean 이름으로 JpaTransactionManager를 등록해 JPA transaction 기준으로 동작하게 한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@HasAnnotationOnMain_At_Bean(Enable_JpaHybrid_At_Main.class)
@Configuration
public class JpaHybridConfig implements WebMvcConfigurer {

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
     * 공통 datasource를 사용하는 EntityManagerFactory를 구성한다.
     *
     * <p>현재 entity scan 범위는 프레임워크 보안 extras entity 패키지로 제한되어 있다.
     * 업무 도메인 entity를 JPA로 사용하려면 scan 패키지 확장이 필요하다.</p>
     */
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        // todo : 다른 패키지도 스캔할수 있도록 설정 필요
        localContainerEntityManagerFactoryBean.setPackagesToScan("com._sptek.__webFramework.security.userStore.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update"); //(create, create-drop, update, none)
        properties.put("hibernate.show_sql", "false");
        localContainerEntityManagerFactoryBean.setJpaPropertyMap(properties);

        return localContainerEntityManagerFactoryBean;
    }


}
