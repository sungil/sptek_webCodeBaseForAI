package com._sptek._webFrameworkExample.unit.authentication.userStore.repository;

import com._sptek._webFrameworkExample.unit.authentication.userStore.entity.TestJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository 예제용 TestJpa entity repository.
 */
public interface TestJpaRepository extends JpaRepository<TestJpa, Long> {
    /**
     * myKey 값으로 단건 entity를 조회한다.
     */
    TestJpa findByMyKey(String key);

    /**
     * myKey 값으로 Optional 단건 entity를 조회한다.
     */
    Optional<TestJpa> findOptByMyKey(String key); //spEL 구성의 메소드 이름을 바꾸고 싶으면 findXXXBy 형식으로 By 앞쪽에 덮붙인다.

    /**
     * myKey 목록에 해당하는 entity 목록을 조회한다.
     */
    List<TestJpa> findByMyKeyIn (List<String> keys); //List형 리턴 메소드의 경우 결과가 없어도 null 이 아니라 empty List로 넘어옴

}
