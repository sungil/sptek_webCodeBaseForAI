package com.sptek.__webFramework.example.unit.database;

import com.sptek.__webFramework.example.dto.TbTestDto;
import com.sptek.__webFramework.data.mybatis.MyBatisCommonDao;
import com.sptek.__webFramework.data.mybatis.MybatisResultHandlerSupport;
import com.sptek.__webFramework.data.mybatis.PageInfoSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 프레임워크 예제에서 MyBatis 공통 DAO 기반 DB 접근 방식을 보여주는 서비스.
 *
 * <p>업무 규칙을 담는 서비스가 아니라 transaction readOnly 값에 따른 datasource routing,
 * statement id 기반 CRUD, result handler, pagination 지원 기능을 검증하는 예제 코드를 모아둔다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DatabaseService {
    private final MyBatisCommonDao myBatisCommonDao;

    /**
     * 현재 활성화된 datasource 에서 기본 select 문을 실행한다.
     */
    @Transactional(readOnly = true)
    public int checkDbConnection(){
        return this.myBatisCommonDao.selectOne("framework_example.return1", null);
    }

    /**
     * 쓰기 transaction 으로 master datasource 라우팅을 확인한다.
     */
    @Transactional(readOnly = false)
    public int checkReplicationMaster(){
        return this.myBatisCommonDao.selectOne("framework_example.return1", null);
    }

    /**
     * 읽기 전용 transaction 으로 slave datasource 라우팅을 확인한다.
     */
    @Transactional(readOnly = true)
    public int checkReplicationSlave() {
        return this.myBatisCommonDao.selectOne("framework_example.return1", null);
    }

    /**
     * MyBatis mapper statement 를 통해 예제 테이블 데이터를 insert 한다.
     */
    @Transactional(readOnly = false)
    public int insertTbTest(TbTestDto tbTestDto) {
        return this.myBatisCommonDao.insert("framework_example.insertTbTest", tbTestDto);
    }

    /**
     * MyBatis mapper statement 를 통해 예제 테이블 데이터를 update 한다.
     */
    @Transactional(readOnly = false)
    public int updateTbTest(TbTestDto tbTestDto) {
        return this.myBatisCommonDao.update("framework_example.updateTbTest", tbTestDto);
    }

    /**
     * MyBatis mapper statement 를 통해 예제 테이블 데이터를 delete 한다.
     */
    @Transactional(readOnly = false)
    public int deleteTbTest() {
        return this.myBatisCommonDao.delete("framework_example.deleteTbTest", null);
    }

    /**
     * 예제 테이블에서 limit 조건을 사용해 단건 조회 결과를 반환한다.
     */
    @Transactional(readOnly = true)
    public TbTestDto getOneTbTest() {
        int limit = 1;
        return this.myBatisCommonDao.selectOne("framework_example.selectTbTestWithLimit", limit);
    }

    /**
     * 예제 테이블에서 limit 조건을 사용해 목록 조회 결과를 반환한다.
     */
    @Transactional(readOnly = true)
    public List<TbTestDto> getListTbTest() {
        int limit = 100;
        return this.myBatisCommonDao.selectList("framework_example.selectTbTestWithLimit", limit);
    }

    /**
     * ResultHandler 로 row 단위 필터링과 조기 종료를 적용한 목록 조회 예제를 실행한다.
     *
     * <p>대량 결과를 한 번에 List 로 받기 어렵거나 row 별 판단 후 일부만 수집해야 하는 경우의
     * 사용 방식을 보여준다. handler 처리 중에는 DB cursor/connection 이 유지될 수 있으므로
     * 실제 업무 코드에서는 처리 시간과 timeout 조건을 함께 고려해야 한다.</p>
     */
    @Transactional(readOnly = true)
    public List<TbTestDto> getListTbTestWithResultHandler(){
        MybatisResultHandlerSupport<TbTestDto, TbTestDto> mybatisResultHandlerSupport = new MybatisResultHandlerSupport<>()
        {
            int maxCount = 0;

            /**
             * 조회된 row 를 결과 목록에 포함할지 판단하고 필요한 시점에 처리를 중단한다.
             */
            @Override
            public @Nullable TbTestDto handleResultRow(TbTestDto resultRow) {

                if (Integer.parseInt(Objects.toString(resultRow.getC1(), "0")) > 2133368224) {
                    log.debug("maxCount = {}, {} was excepted", maxCount, resultRow.getC1());
                    return null;
                } else {
                    maxCount++;
                    log.debug("maxCount = {}, {} was added", maxCount, resultRow.getC1());
                }

                if(maxCount == 2) {
                    stop();
                }

                return resultRow;
            }

            //필요시 override
            /*
            @Override
            public void open(){
                log.info("called open");
            }

             */
            //필요시 override
            /*
            @Override
            public void close(){
                log.info("called close");
            }
             */
        };

        return this.myBatisCommonDao.selectListWithResultHandler("framework_example.selectAllTbTest", null, mybatisResultHandlerSupport);
    }

    /**
     * 특정 컬럼 값을 key 로 사용하는 MyBatis map 조회 결과를 반환한다.
     */
    @Transactional(readOnly = true)
    public Map<?, ?> getMapTbTest() {
        int limit = 3;
        return this.myBatisCommonDao.selectMap("framework_example.selectTbTestWithLimit", limit, "c1");
    }

    /**
     * 요청 파라미터와 기본 pagination 설정을 사용해 페이징된 목록 조회 결과를 반환한다.
     *
     * <p>currentPageNum, setRowSizePerPage, setBottomPageNavigationSize 파라미터 해석은
     * {@link MyBatisCommonDao#selectListWithPagination(String, Object)}에서 처리한다.</p>
     */
    @Transactional(readOnly = true)
    public PageInfoSupport<TbTestDto> getListTbTestWithPagination() {
        return this.myBatisCommonDao.selectListWithPagination("framework_example.selectAllTbTest", null);
    }

}
