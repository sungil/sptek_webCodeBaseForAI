package com.sptek._frameworkWebCore.persistence.mybatis.dao;

import com.sptek._frameworkWebCore.support.MybatisResultHandlerSupport;
import com.sptek._frameworkWebCore.support.PageHelperSupport;
import com.sptek._frameworkWebCore.support.PageInfoSupport;
import com.sptek._frameworkWebCore.util.SpringUtil;
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
/*
mybatis를 이용한 db 기본 템플릿을 제공함
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

    public Integer insert(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return this.sqlSessionTemplate.insert(statementId, parameter);
    }

    public Integer update(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return this.sqlSessionTemplate.update(statementId, parameter);
    }

    public Integer delete(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return this.sqlSessionTemplate.delete(statementId, parameter);
    }

    public <T> T selectOne(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return (T)(this.sqlSessionTemplate.selectOne(statementId, parameter));
    }

    public <T> List<T> selectList(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return  (List<T>) this.sqlSessionTemplate.selectList(statementId, parameter);
    }

    public Map<?, ?> selectMap(String statementId, @Nullable Object parameter, String columnNameForMapkey) {
        log.debug("statementId = {}", statementId);
        return this.sqlSessionTemplate.selectMap(statementId, parameter, columnNameForMapkey);
    }

    // DB로 부터 result row를 하나씩 받아가며 중간처리 작업을 진행할 수 있게 해준다.
    // 조회 범위를 러프하게 잡고 원하는 요소만 모을수 있다, 메모리 절약가능, 반대로 DB 커넥션을 잡고 있음, 커넥션 타임아웃 주의
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
