package com._sptek._webFrameworkExample.unit.database;

import com._sptek._webFrameworkExample.dto.TbTestDto;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 프레임워크 예제용 DB 접근 API를 제공하는 RestController.
 *
 * <p>업무 API가 아니라 MyBatis 공통 DAO, transaction readOnly 기반 datasource routing,
 * result handler, pagination 사용 예를 HTTP 단위로 확인하기 위한 컨트롤러이다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Database", description = "")
public class DatabaseApiController {
    private final DatabaseService databaseService;

    /**
     * 현재 활성화된 datasource 로 기본 select 문을 실행해 DB 연결 가능 여부를 확인한다.
     */
    @GetMapping("/01/example/databasea/checkDbConnection")
    @Operation(summary = "01. DB 연결 상태 체크", description = "")
    public Object checkDbConnection() {
        return databaseService.checkDbConnection() == 1 ? "success" : "fail";
    }

    /**
     * 쓰기 transaction 설정에서 replication master datasource 로 라우팅되는지 확인한다.
     */
    @GetMapping("/02/example/databasea/checkReplicationMaster")
    @Operation(summary = "02. @Transactional(readOnly = false) 통해 Master DB로 연결 체크", description = "")
    public Object checkReplicationMaster(Model model) {
        return databaseService.checkReplicationMaster() == 1 ? "success" : "fail";
    }

    /**
     * 읽기 전용 transaction 설정에서 replication slave datasource 로 라우팅되는지 확인한다.
     */
    @GetMapping("/03/example/databasea/checkReplicationSlave")
    @Operation(summary = "03. @Transactional(readOnly = true) 통해 Slave DB로 연결 체크", description = "")
    public Object checkReplicationSlave(Model model) {
        return databaseService.checkReplicationSlave() == 1 ? "success" : "fail";
    }

    /**
     * MyBatis 공통 DAO의 insert 호출 예제를 실행한다.
     */
    @GetMapping("/04/example/databasea/myBatisCommonDaoInsert")
    @Operation(summary = "04. myBatisCommonDao insert", description = "")
    public Object myBatisCommonDaoInsert() {
        TbTestDto tbTestDto = TbTestDto.builder()
                .c1((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c2((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c3((int) (System.currentTimeMillis() % Integer.MAX_VALUE)).build();
        return databaseService.insertTbTest(tbTestDto) == 1 ? "success" : "fail";
    }

    /**
     * MyBatis 공통 DAO의 update 호출 예제를 실행한다.
     */
    @GetMapping("/05/example/databasea/myBatisCommonDaoUpdate")
    @Operation(summary = "05. myBatisCommonDao update", description = "")
    public Object myBatisCommonDaoUpdate() {
        TbTestDto tbTestDto = TbTestDto.builder()
                .c1((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c2((int) (System.currentTimeMillis() % Integer.MAX_VALUE))
                .c3((int) (System.currentTimeMillis() % Integer.MAX_VALUE)).build();
        return databaseService.updateTbTest(tbTestDto) > 0 ? "success" : "fail";
    }

    /**
     * MyBatis 공통 DAO의 delete 호출 예제를 실행한다.
     */
    @GetMapping("/06/example/databasea/myBatisCommonDaoDelete")
    @Operation(summary = "06. myBatisCommonDao delete", description = "")
    public Object myBatisCommonDaoDelete() {
        return databaseService.deleteTbTest() > 0 ? "success" : "fail";
    }

    /**
     * MyBatis 공통 DAO의 단건 조회 예제를 실행한다.
     */
    @GetMapping("/07/example/databasea/myBatisCommonDaoSelectOne")
    @Operation(summary = "07. myBatisCommonDao selectOne", description = "")
    public Object myBatisCommonDaoSelectOne() {
        return databaseService.getOneTbTest();
    }

    /**
     * MyBatis 공통 DAO의 목록 조회 예제를 실행한다.
     */
    @GetMapping("/08/example/databasea/myBatisCommonDaoSelectList")
    @Operation(summary = "08. myBatisCommonDao selectList", description = "")
    public Object myBatisCommonDaoSelectList() {
        return databaseService.getListTbTest();
    }

    /**
     * MyBatis ResultHandler 기반 목록 조회 예제를 실행한다.
     */
    @GetMapping("/09/example/databasea/myBatisCommonDaoSelectListWithResultHandler")
    @Operation(summary = "09. myBatisCommonDao selectListWithResultHandler", description = "")
    public Object myBatisCommonDaoSelectListWithResultHandler() {
        return databaseService.getListTbTestWithResultHandler();
    }

    /**
     * 특정 컬럼 값을 key 로 사용하는 MyBatis map 조회 예제를 실행한다.
     */
    @GetMapping("/10/example/databasea/myBatisCommonDaoSelectMap")
    @Operation(summary = "10. myBatisCommonDao selectMap(단일, 리스트)", description = "")
    public Object myBatisCommonDaoSelectMap() {
        return databaseService.getMapTbTest();
    }

    /**
     * 요청 파라미터 기반 MyBatis pagination 조회 예제를 실행한다.
     */
    @GetMapping("/11/example/databasea/myBatisCommonDaoSelectListWithPagination")
    @Operation(summary = "11. myBatisCommonDao selectListWithPagination", description = "")
    public Object myBatisCommonDaoSelectListWithPagination(
            @RequestParam(name = "currentPageNum", required = false, defaultValue = "1") int currentPageNum,
            @RequestParam(name = "setRowSizePerPage", required = false, defaultValue = "20") int setRowSizePerPage,
            @RequestParam(name = "setBottomPageNavigationSize", required = false, defaultValue = "10") int setBottomPageNavigationSize) {
        return databaseService.getListTbTestWithPagination();
    }
}
