package com._sptek.__webFramework.data.mybatis;

import com._sptek.__webFramework.core.util.SpringUtil;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MyBatis statementId 기반의 공통 CRUD, stream 처리, 페이징 조회 기능을 제공하는 DAO.
 *
 * <p>업무 서비스가 mapper namespace와 statementId를 직접 지정해 호출하는 얇은 템플릿이다.
 * 복잡한 도메인 규칙은 이 DAO가 아니라 서비스 계층이나 전용 repository/mapper에서 처리한다.</p>
 */
@Slf4j
@Component("myBatisCommonDao")
public class MyBatisCommonDao {

    private final SqlSessionTemplate sqlSessionTemplate;
    private final int defaultCurrentPageNum;
    private final int defaultSetRowSizePerPage;
    private final int defaultSetBottomPageNavigationSize;
    private final int maxSetRowSizePerPage;

    public MyBatisCommonDao(
            @Qualifier("sqlSessionTemplate") SqlSessionTemplate sqlSessionTemplate,
            @Value("${daoPagination.default.currentPageNum}") int defaultCurrentPageNum,
            @Value("${daoPagination.default.setRowSizePerPage}") int defaultSetRowSizePerPage,
            @Value("${daoPagination.default.setBottomPageNavigationSize}") int defaultSetBottomPageNavigationSize,
            @Value("${daoPagination.max.setRowSizePerPage}") int maxSetRowSizePerPage1) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.defaultCurrentPageNum = defaultCurrentPageNum;
        this.defaultSetRowSizePerPage = defaultSetRowSizePerPage;
        this.defaultSetBottomPageNavigationSize = defaultSetBottomPageNavigationSize;
        this.maxSetRowSizePerPage = maxSetRowSizePerPage1;
    }

    /**
     * 지정한 MyBatis statement로 insert를 실행한다.
     */
    public Integer insert(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return this.sqlSessionTemplate.insert(statementId, parameter);
    }

    /**
     * 지정한 MyBatis statement로 update를 실행한다.
     */
    public Integer update(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return this.sqlSessionTemplate.update(statementId, parameter);
    }

    /**
     * 지정한 MyBatis statement로 delete를 실행한다.
     */
    public Integer delete(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return this.sqlSessionTemplate.delete(statementId, parameter);
    }

    /**
     * 지정한 MyBatis statement로 단건을 조회한다.
     */
    public <T> T selectOne(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return (T)(this.sqlSessionTemplate.selectOne(statementId, parameter));
    }

    /**
     * 지정한 MyBatis statement로 목록을 조회한다.
     */
    public <T> List<T> selectList(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return  (List<T>) this.sqlSessionTemplate.selectList(statementId, parameter);
    }

    /**
     * 지정한 컬럼 값을 key로 사용하는 Map 형태로 조회한다.
     */
    public Map<?, ?> selectMap(String statementId, @Nullable Object parameter, String columnNameForMapkey) {
        log.debug("statementId = {}", statementId);
        return this.sqlSessionTemplate.selectMap(statementId, parameter, columnNameForMapkey);
    }

    /**
     * DB 결과 row를 하나씩 handler에 전달하며 필요한 결과만 모아 반환한다.
     *
     * <p>대량 조회에서 메모리를 줄일 수 있지만, 처리 중 DB 커넥션을 점유하므로 handler 작업 시간과 timeout을 함께 고려한다.</p>
     */
    public <T, R> List<R> selectListWithResultHandler(
            String statementId, Object parameter,
            final MybatisResultHandlerSupport<T, R> mybatisResultHandlerSupport)
    {
        log.debug("statementId = {}", statementId);
        final List<R> finalHeandledResults = new ArrayList<R>();
        try {
            mybatisResultHandlerSupport.open();
            this.sqlSessionTemplate.select(statementId, parameter
                    , context -> {
                        R handledResult = mybatisResultHandlerSupport.handleResultRow((T) context.getResultObject());
                        if (handledResult != null) finalHeandledResults.add(handledResult);
                        if (mybatisResultHandlerSupport.isStop()) context.stop();
                    });
        } finally {
            mybatisResultHandlerSupport.close();
        }

        return finalHeandledResults;
    }

    /**
     * 현재 요청의 페이징 파라미터를 읽어 MyBatis 목록 조회에 PageHelper 페이징을 적용한다.
     *
     * <p>request thread가 아닌 곳에서는 설정 기본값을 사용한다. {@code setRowSizePerPage}는
     * 설정된 최대값을 넘지 않도록 제한한다.</p>
     */
    public <T> PageInfoSupport<T> selectListWithPagination(String statementId, @Nullable Object parameter)
    {
        log.debug("statementId = {}", statementId);

        //이부분 해결해야 함!!!
        //todo : 전체 row 의 total size 를 매번 구하지 않도록 캐싱 방안을 고려 해야함

        HttpServletRequest httpServletRequest = SpringUtil.getRequestOrNull();
        int currentPageNum = (httpServletRequest != null
                && httpServletRequest.getParameter("currentPageNum") != null
                && Integer.parseInt(httpServletRequest.getParameter("currentPageNum")) > 0)
                ? Integer.parseInt(httpServletRequest.getParameter("currentPageNum"))
                : defaultCurrentPageNum;

        int setRowSizePerPage = (httpServletRequest != null
                && httpServletRequest.getParameter("setRowSizePerPage") != null
                && Integer.parseInt(httpServletRequest.getParameter("setRowSizePerPage")) > 0)
                ? Math.min(Integer.parseInt(httpServletRequest.getParameter("setRowSizePerPage")), maxSetRowSizePerPage)
                : defaultSetRowSizePerPage;

        int setBottomPageNavigationSize = (httpServletRequest != null
                && httpServletRequest.getParameter("setBottomPageNavigationSize") != null
                && Integer.parseInt(httpServletRequest.getParameter("setBottomPageNavigationSize")) > 0)
                ? Integer.parseInt(httpServletRequest.getParameter("setBottomPageNavigationSize"))
                : defaultSetBottomPageNavigationSize;

        //setPageForSelect 내부 에서 PageHelper.startPage 가 호출 되면서 다음 mybatis 쿼리에 자동 으로 limit 처리를 해줌
        PageHelperSupport.setPageForSelect(currentPageNum, setRowSizePerPage);
        PageInfoSupport<T> pageInfoSupport;
        if (parameter == null) {
            pageInfoSupport = PageHelperSupport.selectPaginatedList(this.sqlSessionTemplate.selectList(statementId), setBottomPageNavigationSize);
        } else {
            pageInfoSupport = PageHelperSupport.selectPaginatedList(this.sqlSessionTemplate.selectList(statementId, parameter), setBottomPageNavigationSize);
        }
        return pageInfoSupport;
    }


}
