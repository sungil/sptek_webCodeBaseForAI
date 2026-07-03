package com.sptek._frameworkWebCore.springSecurity.extras.repository;

import com.sptek._frameworkWebCore.springSecurity.AuthorityEnum;
import com.sptek._frameworkWebCore.springSecurity.extras.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Authority entity 조회를 위한 Spring Data JPA repository.
 */
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    /**
     * AuthorityEnum 목록에 해당하는 권한 entity들을 조회한다.
     */
    List<Authority> findByAuthorityIn(List<AuthorityEnum> authorities);
}
