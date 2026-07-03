package com.sptek._frameworkWebCore.support;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * PageHelper 라이브러리 호출 방식을 프레임워크 페이징 규약으로 감싼 유틸리티.
 *
 * <p>조회 직전에 {@link #setPageForSelect(int, int)}로 PageHelper 컨텍스트를 설정하고,
 * 조회 결과 List를 {@link #selectPaginatedList(List, int)}로 변환해 화면용 페이징 정보를 만든다.</p>
 */
@Slf4j
public class PageHelperSupport {
    /**
     * 다음 MyBatis SELECT에 적용될 현재 페이지와 페이지당 row 수를 PageHelper에 설정한다.
     */
    public static void setPageForSelect(int currentPageNum, int setRowSizePerPage) {
        PageHelper.startPage(currentPageNum, setRowSizePerPage);
    }

    /**
     * PageHelper가 채운 List 메타데이터를 {@link PageInfoSupport}로 변환한다.
     */
    public static <T> PageInfoSupport<T> selectPaginatedList(List<? extends T> list, int setBottomPageNavigationSize) {
        PageInfo<T> pageInfo =  PageInfo.of(list, setBottomPageNavigationSize);
        return new PageInfoSupport<>(pageInfo);
    }
}
