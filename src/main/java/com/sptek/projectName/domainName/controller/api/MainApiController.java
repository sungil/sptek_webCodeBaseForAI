package com.sptek.projectName.domainName.controller.api;

import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiGlobalException_At_RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
// @Tag(name = "domainName-main", description = "")
// 실 Project 쪽의 Security filter chain Test Code

public class MainApiController {

    /*
    @GetMapping("/01/domainName/main/authFree")
    @Operation(summary = "01. 권한 제한이 없는 path", description = "") //swagger
    public Object authFree() {
        return "Any one can access.";
    }

    @GetMapping("/02/login/domainName/main/needAuth")
    @Operation(summary = "02. 로그인 필요함", description = "") //swagger
    public Object needAnyAuth() {
        return "you are logged in";
    }

    @GetMapping("/03/auth-special/domainName/main/needAuth")
    @Operation(summary = "03. Special Auth 가 필요함", description = "") //swagger
    public Object needSpecialAuth() {
        return "you have Auth (Special)";
    }

    @GetMapping("/04/role-user/domainName/main/needAuth")
    @Operation(summary = "04. User Role 이 필요함", description = "") //swagger
    public Object needUserRole() {
        return "you have Role (User)";
    }

    @GetMapping("/05/role-system/domainName/main/needAuth")
    @Operation(summary = "05. System Role 이 필요함", description = "") //swagger
    public Object needSystemRole() {
        return "you have Role (System)";
    }

    @GetMapping("/06/role-admin-adminSpecial/domainName/main/needAuth")
    @Operation(summary = "06. Admin or AdminSpecial Role 이 필요함", description = "") //swagger
    public Object needAdminOrAdminSpecialRole() {
        return "you have Role (Admin or AdminSpecial)";
    }

    @RequestMapping(value = "/07/postLogin/domainName/main/needAuth", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "07. POST 요청은 로그인 필요함", description = "") //swagger
    public Object needAnyAuthWhenPost() {
        return "you need Auth (Any) when you request with POST";
    }
     */
}
