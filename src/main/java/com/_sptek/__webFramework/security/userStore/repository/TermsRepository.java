package com._sptek.__webFramework.security.userStore.repository;

import com._sptek.__webFramework.security.userStore.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Terms entity 조회를 위한 Spring Data JPA repository.
 */
public interface TermsRepository extends JpaRepository<Terms, Long> {

    /**
     * 약관 이름 목록에 해당하는 terms entity들을 조회한다.
     */
    List<Terms> findByTermsNameIn(List<String> termsName);
}
