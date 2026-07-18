package com._sptek._webFrameworkExample.unit.authentication.userStore.repository;

import com._sptek._webFrameworkExample.unit.authentication.userStore.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserAddress entity 기본 CRUD를 제공하는 Spring Data JPA repository.
 */
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

}
