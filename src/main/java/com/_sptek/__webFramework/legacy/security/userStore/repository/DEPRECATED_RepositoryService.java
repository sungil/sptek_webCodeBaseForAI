package com._sptek.__webFramework.legacy.security.userStore.repository;

import com._sptek.__webFramework.security.userStore.entity.TestJpa;
import com._sptek.__webFramework.security.userStore.repository.TestJpaRepository;
import com.cesco.__projectsCommon.commonObject.code.ServiceErrorCodeEnum;
import com._sptek.__webFramework.core.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Spring Data JPA repository 반환 타입별 동작을 확인하던 deprecated 예제 서비스.
 *
 * <p>단건 entity, Optional, List 반환의 null/empty 처리 차이를 확인하기 위한 샘플이며 신규 업무 코드에서 사용하지 않는다.</p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class DEPRECATED_RepositoryService {

    private final TestJpaRepository testJpaRepository;

    // TEST **************** 결론은 단일 반환의 경우 Optional 로 받고 복수는 List<T>로 받자 (조회값이 없더라도 empty List를 내리지 null 을 내리지 않음으로 Optional을 이중으로 할 필요가 없음)

    /**
     * key 형식에 따라 단건 또는 다건 repository 조회 예제를 실행한다.
     */
    public Map<String, Object> testRepository(String key) {
        List<String> keys = Arrays.asList(key.split("-"));
        Map<String, Object> returnMap = new HashMap<>();

        if(keys.size() == 1){
            TestJpa returnObj = testJpaRepository.findByMyKey(keys.get(0));
            testRepositoryWithObj(returnObj);
            returnMap.put("returnObj", returnObj);

            Optional<TestJpa> returnOpt = testJpaRepository.findOptByMyKey(keys.get(0));
            testRepositoryWithOptional(returnOpt);
            returnMap.put("returnOpt", returnOpt);

        }else {
            List<TestJpa> returnList = testJpaRepository.findByMyKeyIn(keys);
            testRepositoryWithList(returnList);
            returnMap.put("returnList", returnList);
        }

        log.debug("strMap : {}", new HashMap<String, String>());
        return returnMap;
    }


    /**
     * 단건 entity 반환값이 없을 때 null로 내려오는 케이스를 확인한다.
     */
    public void testRepositoryWithObj(TestJpa testJpa) {
        if(testJpa == null)
            log.debug("test obj is null");
        else
            log.debug("test obj is not null : {}", testJpa);

    }

    /**
     * Optional 단건 반환의 empty/present 처리 방법을 확인한다.
     */
    public void testRepositoryWithOptional(Optional<TestJpa> testOpt) {
        //서로 반대 의미
        log.debug("isEmpty : {}" , testOpt.isEmpty());
        log.debug("isPresent : {}" , testOpt.isPresent());

        //조재하는 케이스만 처리
        testOpt.ifPresent(testJpa -> log.debug("ifPresent : {}", testJpa));

        //존재할때와 안할때 케이스 처리
        testOpt.ifPresentOrElse(
                testJpa -> log.debug("ifpresendt : {}", testJpa)
                ,() -> log.debug("ifPresentOrElse"));

        //존재하지 않을때의 대체값
        log.debug("orElse : {} ", testOpt.orElse(TestJpa.builder().myKey("TEST NAME").build()));

        //존재하지 않을때의 Exception 처리
        try {
            testOpt.orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR));
        } catch (ServiceException ex) {
            log.debug(ex.getMessage());
        }

        try {
            log.debug("testOpt.get() : {}", testOpt.get()); //empty 상태 일때는 NoSuchElementException 발생
        } catch (NoSuchElementException ex) {
            log.debug(ex.getMessage());
        }
    }

    /**
     * List 반환값이 조회 결과 없음에서 empty list로 내려오는 케이스를 확인한다.
     */
    public void testRepositoryWithList(List<TestJpa> testJpas) {
        if (testJpas == null){
            log.debug("tests is null"); //조회값이 없어도 List 를 내려줌 (empty 상태)
        } else {
            log.debug("tests is not null");

            if (testJpas.isEmpty()) {
                log.debug("But tests is empty"); //조회값이 없으면 empty List
            } else {
                log.debug("first test : {}", testJpas.get(0));
            }
        }
    }

}
