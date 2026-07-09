package com.sptek.__webFramework.data.mybatis;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * PageHelper {@link PageInfo}의 페이징 결과를 화면/응답에서 읽기 쉬운 필드명으로 재구성한 값 객체.
 *
 * <p>게시판형 목록의 현재 페이지, 전체 건수, 하단 네비게이션 번호, 조회 결과 목록을 함께 제공한다.
 * 일반 사용 흐름에서는 {@code MybatisCommonDao.selectPaginatedList()}를 통해 생성된다.</p>
 */
@Getter
@ToString
public class PageInfoSupport<T> {

    //입력 받는 파람(반환도 됨)
    int currentPageNum; //조회된 현재 페이지의 페이지 넘버
    int setRowSizePerPage; //한페이지당 노출할 row 수 (셋팅값으로 동적 변경 가능)

    //네비게이션바에 최대 몇개의 넘버릴을 해줄지의 값 (셋팅값으로 동적 변경 가능, 5로 셋팅시 현재 페이지가 3이면 [1, 2, 3, 4, 5],
    // 현재 페이지가 6이면 [4, 5, 6, 7, 8], 조회된 결과 전체 페이지가 3개고 현재 페이지가 1 이면 [1, 2, 3] 으로 셋팅값보다 작을수 있음)
    int setBottomPageNavigationSize;



    //반환 되는 값
    int totalPageCount; //해당 조회의 전체 페이지 수
    long totalRowCount; //해당 조회의 전체 row 건수

    //현재 페이지에 노출된 row 건수(보통의 경우 setRowSizePerPage 값과 같겠지만
    //조회 전체 건수가 한페이지 개수보다 적거나 마지막 페이지의 경우 setRowSizePerPage 보다 작을 수 있다)
    int currentPageRowCount;
    long currentPageStartRowNum; //전체 row 번호기준, 현재 페이지의 첫번째 row 번호
    long currentPageEndRowNum; //전체 row 번호기준, 현재 페이지의 마지막 row 번호

    boolean isFirstPage; //현재 페이지가 첫번째 페이지인지 여부
    boolean isLastPage; //현재 페이지가 마지막 페이지인지 여부
    boolean hasPrePage; //현재 페이지의 이전 페이지가 있는지 여부
    boolean hasNextPage; //현재 페이지의 다음 페이지가 있는지 여부
    int prePageNum; //현재 페이지의 이전 페이지 번호(없다면 0)
    int netPageNum; //현재 페이지의 다음 페이지 번호(없다면 0)

    //네비게이션바는 게시판 하단에 다음 페이지로 넘어가기 위한 [pre 1. 2. 3. 4. 5 nxt] 이런 모양의 넘버링 정보를 말함
    int currentNavigationFirstNum; //하단 네비게이션바의 첫 페이지 번호
    int currentNavigationLastNum; //하단 네비게이션바의 마지막 페이지 번호
    int[] currentNavigationAllNums; //하단 네비게이션바의 모든 페이지 번호

    List selectedList; //조회된 실제 데이터 리스트

    /**
     * PageHelper 결과 객체를 프레임워크 표준 페이징 응답 구조로 변환한다.
     */
    public PageInfoSupport(PageInfo<T> pageInfo) {
        MappingPageInfoToPageInfoSupport(pageInfo);
    }

    /**
     * PageInfo의 불명확한 메서드명과 불필요한 내부 데이터를 제거하고 필요한 페이징 값만 복사한다.
     */
    private void MappingPageInfoToPageInfoSupport(PageInfo pageInfo) {
        //PageInfo의 불명확한 메소드명과 불필요한 데이터를 제거하여 사용자가 이해하기 쉽게하기 위한 처리
        currentPageNum = pageInfo.getPageNum();
        setRowSizePerPage = pageInfo.getPageSize();
        setBottomPageNavigationSize = pageInfo.getNavigatePages();

        totalPageCount = pageInfo.getPages();
        totalRowCount = pageInfo.getTotal();
        currentPageRowCount = pageInfo.getSize();
        currentPageStartRowNum = pageInfo.getStartRow();
        currentPageEndRowNum = pageInfo.getEndRow();

        isFirstPage = pageInfo.isIsFirstPage();
        isLastPage = pageInfo.isIsLastPage();
        hasPrePage = pageInfo.isHasPreviousPage();
        hasNextPage = pageInfo.isHasNextPage();
        prePageNum = pageInfo.getPrePage();
        netPageNum = pageInfo.getNextPage();

        currentNavigationFirstNum = pageInfo.getNavigateFirstPage();
        currentNavigationLastNum = pageInfo.getNavigateLastPage();
        currentNavigationAllNums = pageInfo.getNavigatepageNums();

        //PageInfo 객체를 toString 했을때 불필요한 데이터가 포함되어 있어 List 정보만을 그데로 카피해서 기존 list 정보의 toString의 고리를 끊음
        selectedList = pageInfo.getList().subList(0, currentPageRowCount);
    }
}
