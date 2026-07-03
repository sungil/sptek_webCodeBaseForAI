package com.sptek._frameworkWebCore.springSecurity.extras.repository;

import com.sptek._frameworkWebCore.springSecurity.extras.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Role entity 조회를 위한 Spring Data JPA repository.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * role 이름 목록에 해당하는 role entity들을 조회한다.
     */
    List<Role> findByRoleNameIn(List<String> roleNames);
}
