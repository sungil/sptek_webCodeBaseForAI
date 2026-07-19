package com._sptek._webFrameworkExample.unit.authentication;

import com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import com._sptek.__webFramework.security.token.jwt.JwtAuthenticationFilter;
import com._sptek.__webFramework.security.token.jwt.JwtTokenProvider;
import com._sptek._webFrameworkExample.unit.authentication.userStore.dto.LoginRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * API 요청에서 Spring Security 인증 상태와 URL 인가 정책을 확인하는 예제 컨트롤러.
 *
 * <p>경로 기반 인가, 메서드 기반 인가, JWT 로그인 처리 흐름을 같은 API 진입점에서 비교할 수 있게 둔다.
 * 실제 업무 API에서는 예제 경로와 테스트용 권한명을 그대로 사용하지 말고 프로젝트 보안 정책에 맞는 matcher와
 * 권한 체계를 기준으로 분리한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Authentication (authentication.http 을 통해 추가 테스트 가능)", description = "")
public class AuthenticationApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationService authenticationService;

    @GetMapping("/01/example/authentication/authFree")
    @Operation(summary = "01. 권한 제한이 없는 path", description = "") //swagger
    public Object authFree() {
        return "Anyone can access.";
    }

    @GetMapping("/02/example/login/authentication/needAuth")
    @Operation(summary = "02. 로그인 필요함", description = "") //swagger
    public Object needAnyAuth() {
        return "you are logged in";
    }

    @GetMapping("/03/example/auth-special/authentication/needAuth")
    @Operation(summary = "03. Special Auth 가 필요함", description = "") //swagger
    public Object needSpecialAuth() {
        return "you have Auth (Special)";
    }

    @GetMapping("/04/example/role-user/authentication/needAuth")
    @Operation(summary = "04. User Role 이 필요함", description = "") //swagger
    public Object needUserRole() {
        return "you have Role (User)";
    }

    @GetMapping("/05/example/role-system/authentication/needAuth")
    @Operation(summary = "05. System Role 이 필요함", description = "") //swagger
    public Object needSystemRole() {
        return "you have Role (System)";
    }

    @GetMapping("/06/example/role-admin-adminSpecial/authentication/needAuth")
    @Operation(summary = "06. Admin or AdminSpecial Role 이 필요함", description = "") //swagger
    public Object needAdminOrAdminSpecialRole() {
        return "you have Role (Admin or AdminSpecial)";
    }

    @RequestMapping(value = "/07/example/postLogin/authentication/needAuth", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "07. 메소드 별 권한 제한 (POST 요청은 로그인 필요)", description = "") //swagger
    public Object needAnyAuthWhenPost() {
        return "you need Auth (Any) when you request with POST";
    }

    /**
     * URL matcher로는 허용된 API에서 서비스 메서드의 권한 검사를 통과해야 하는 흐름을 보여준다.
     */
    @GetMapping("/08/example/authentication/authFreeWithAuthCheckMethod")
    @Operation(summary = "08. Auth 경로가 아니지만 내부 적으로 Auth 체크 메소드 를 사용 하는 경우", description = "") //swagger
    public Object authFreeWithAuthCheckMethod() {
        return authenticationService.iNeedAuth();
    }

    /**
     * URL matcher로는 허용된 API에서 서비스 메서드의 Role 검사를 통과해야 하는 흐름을 보여준다.
     */
    @GetMapping("/09/example/authentication/authFreeWithRoleCheckMethod")
    @Operation(summary = "09. Auth 경로가 아니지만 내부 적으로 Role 체크 메소드 를 사용 하는 경우", description = "") //swagger
    public Object authFreeWithRoleCheckMethod() {
        return authenticationService.iNeedRole();
    }

    /**
     * 요청 본문의 로그인 정보로 인증을 수행하고, 생성한 JWT를 응답 헤더와 본문으로 반환한다.
     *
     * <p>인증 성공 후 현재 요청 내부의 후속 코드에서도 인증 정보를 참조할 수 있도록
     * {@link SecurityContextHolder}에 직접 저장한다. 보안상 로그인 처리는 POST 요청으로만 구성한다.</p>
     */
    @PostMapping("/10/example/authentication/login")
    @Operation(summary = "10. 로그인 처리 API (인증 토큰 반환)", description = "") //swagger
    public Object signin(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletResponse response) {
        // RequestBody 에서 id, pw 항목을 선정하여 UsernamePasswordAuthenticationToken 를 만들어 낸후
        // authenticationManager의 절차를 통해 Authentication을 생성하고 SecurityContextHolder 에 직접 저장하고 (form UI 방식의 경우는 직접 저장하지 않음)
        // Authentication을 JWT로 변환하여 해더에 넣어주는 것 까지 처리함

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication); //다음 요청을 위한 것이 아니라 현재 요청 내 이코드 이후 코드 에서 사용될 여지가 있기에 저장함
        String jwt = jwtTokenProvider.convertAuthenticationToJwt(authentication);

        //아래 방식으로 변경
        //HttpHeaders httpHeaders = new HttpHeaders();
        //httpHeaders.add(JwtAuthenticationFilter.getAuthorizationHeader(), JwtAuthenticationFilter.getAuthorizationPrefix() + jwt);
        //return ResponseEntity.ok().headers(httpHeaders).body(new ApiCommonSuccessResponseDto<>(jwt));

        response.setHeader(JwtAuthenticationFilter.getAuthorizationHeader(), JwtAuthenticationFilter.getAuthorizationPrefix() + jwt);
        return jwt;
    }

}
