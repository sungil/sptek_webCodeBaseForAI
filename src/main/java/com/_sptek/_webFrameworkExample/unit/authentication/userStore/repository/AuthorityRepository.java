package com._sptek._webFrameworkExample.unit.authentication.userStore.repository;

import com._sptek._webFrameworkExample.unit.authentication.authorization.DomainAuthorityEnum;
import com._sptek._webFrameworkExample.unit.authentication.userStore.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Authority entity 조회를 위한 Spring Data JPA repository.
 */
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    /**
     * DomainAuthorityEnum 목록에 해당하는 권한 entity들을 조회한다.
     */
    List<Authority> findByAuthorityIn(List<DomainAuthorityEnum> authorities);
}
