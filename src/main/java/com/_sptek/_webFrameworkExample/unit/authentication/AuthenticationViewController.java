package com._sptek._webFrameworkExample.unit.authentication;

import com._sptek.__webFramework.view.error.Enable_ResponseOfViewGlobalException_At_ViewController;
import com._sptek.__webFramework.security.userStore.dto.*;
import com._sptek.__webFramework.security.util.AuthenticationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Thymeleaf 화면에서 회원 가입, 사용자 정보 조회/수정, Role-Authority 관리를 확인하는 인증 예제 컨트롤러.
 *
 * <p>로그인 자체는 시스템 로그인 화면을 사용하고, 이 컨트롤러는 로그인 전 가입 화면과 로그인 후 사용자/권한 관리 화면의
 * 접근 제어 예시를 제공한다. 실제 업무 화면에서는 예제 템플릿 경로와 테스트 권한명을 프로젝트 기준으로 치환한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Controller
@Enable_ResponseOfViewGlobalException_At_ViewController
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)
public class AuthenticationViewController {

    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";
    private final ModelMapper modelMapper;
    private final AuthenticationService authenticationService;


    /**
     * 가입 화면에서 주소, Role, 약관 선택 항목을 초기화해 렌더링한다.
     */
    @GetMapping("/authentication/signupForm")
    public String signupForm(Model model , SignupRequestDto signupRequestDto) { //thyleaf 쪽에서 입력 항목들의 default 값을 넣어주기 위해 signupRequestDto 필요함
        //화면에 그리기 위한 값들
        signupRequestDto.setUserAddresses(List.of(new UserAddressDto()));
        signupRequestDto.setAllRoles(authenticationService.findAllRoles());
        signupRequestDto.setAllTerms(authenticationService.findAllTerms());

        //model.addAttribute("signupRequestDto", signupRequestDto); //파람에 들어 있음으로 addAttribute 불필요
        return htmlBasePath + "signup";
    }

    /**
     * 가입 요청을 검증하고 저장한 뒤 로그인 화면으로 redirect 한다.
     *
     * <p>검증 실패 시 체크박스 목록을 다시 채워 동일 화면을 렌더링한다.</p>
     */
    @PostMapping("/authentication/signup")
    public String signup(Model model, RedirectAttributes redirectAttributes, @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            //체크박스를 다시 그리기 위해
            signupRequestDto.setAllRoles(authenticationService.findAllRoles());
            signupRequestDto.setAllTerms(authenticationService.findAllTerms());
            return htmlBasePath + "signup";
        }
        UserDto savedUser = authenticationService.saveUser(signupRequestDto);

        //redirect 페이지에 model을 보내기 위해 addFlashAttribute 사용(1회성으로 전달됨)
        redirectAttributes.addFlashAttribute("username", savedUser.getName());

        //저장 후 페이지 뒤로가기에서 데이터를 다시 전달하려 하는것을 막기위해 redirect를 사용함
        return "redirect:/view/login";
    }

    /**
     * 현재 세션의 Spring Security Authentication 객체를 화면에 표시한다.
     */
    @GetMapping("/authentication/curAuthentication")
    public String curAuthentication(Model model) {
        Authentication curAuthentication = AuthenticationUtil.getMyAuthentication();
        //curAuthentication 내 RemoteIpAddress는 로그인을 요청한 ip주소, SessionId는 로그인 을 요청 했던 당시의 세션값(로그인 이후 새 값으로 변경됨)

        model.addAttribute("result", curAuthentication);
        return htmlBasePath + "simpleModelView";
    }

    /**
     * 자신의 사용자 정보를 조회하거나 ADMIN Role 사용자가 지정 이메일의 사용자 정보를 조회한다.
     */
    @GetMapping({"/login/authentication/userInfoView", "/login/authentication/userInfoView/{email}"})
    @PreAuthorize("#email == null or #email == T(com._sptek.__webFramework.security.util.AuthenticationUtil).getMyEmail() or hasRole('ADMIN')")
    public String userInfoView(@PathVariable(value = "email", required = false) String email, Model model) {
        email = email != null ? email : AuthenticationUtil.getMyEmail();
        UserDto resultUserDto = authenticationService.findUserByEmail(email);
        model.addAttribute("result", resultUserDto);
        return htmlBasePath + "simpleModelView";
    }

    /**
     * 자신의 사용자 수정 화면을 렌더링하거나 특별 Authority 사용자가 지정 이메일 사용자의 수정 화면을 렌더링한다.
     */
    @GetMapping({"/login/authentication/userUpdateForm", "/login/authentication/userUpdateForm/{email}"})
    //hasRole 과 hasAuthority 차이는 둘다 Authentication 의 authorities 에서 찾는데 hasRole('USER') 은 내부적으로 ROLE_USER 처럼 ROLE_ 를 붙여서 찾고 hasAuthority 는 그대로 찾는다.
    @PreAuthorize("#email == null or #email == T(com._sptek.__webFramework.security.util.AuthenticationUtil).getMyEmail() or hasAuthority(T(com._sptek.__webFramework.security.authorization.AuthorityEnum).AUTH_SPECIAL_FOR_TEST)")
    public String userUpdateForm(@PathVariable(value = "email", required = false) String email, Model model , UserUpdateRequestDto userUpdateRequestDto) { //thyleaf 쪽에서 입력 항목들의 default 값을 넣어주기 위해 signupRequestDto 필요함
        email = email != null ? email : AuthenticationUtil.getMyEmail();
        UserDto userDto = authenticationService.findUserByEmail(email);
        userUpdateRequestDto = modelMapper.map(userDto, UserUpdateRequestDto.class);
        userUpdateRequestDto.setPassword("");

        //화면에 그리기 위한 값들
        userUpdateRequestDto.setAllRoles(authenticationService.findAllRoles());
        userUpdateRequestDto.setAllTerms(authenticationService.findAllTerms());

        model.addAttribute("userUpdateRequestDto", userUpdateRequestDto);
        return htmlBasePath + "userUpdate";
    }

    /**
     * 사용자 수정 요청을 검증하고 저장한 뒤 수정 화면으로 redirect 한다.
     *
     * <p>검증 실패 시 Role과 약관 선택 목록을 다시 채워 동일 화면을 렌더링한다.</p>
     */
    @PostMapping("/login/authentication/userUpdate")
    @PreAuthorize("#userUpdateRequestDto.email == T(com._sptek.__webFramework.security.util.AuthenticationUtil).getMyEmail() or hasAuthority(T(com._sptek.__webFramework.security.authorization.AuthorityEnum).AUTH_SPECIAL_FOR_TEST)")
    public String userUpdate(Model model, RedirectAttributes redirectAttributes, @Valid UserUpdateRequestDto userUpdateRequestDto, BindingResult bindingResult) {
        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            //체크박스를 다시 그리기 위해
            userUpdateRequestDto.setAllRoles(authenticationService.findAllRoles());
            userUpdateRequestDto.setAllTerms(authenticationService.findAllTerms());

            return htmlBasePath + "userUpdate";
        }
        UserDto savedUser = authenticationService.updateUser(userUpdateRequestDto);

        redirectAttributes.addFlashAttribute("savedUserEmail", savedUser.getEmail());
        return "redirect:/view/example/login/authentication/userUpdateForm/" + userUpdateRequestDto.getEmail();
    }

    /**
     * Role-Authority 매핑 관리 화면에 전체 Role과 Authority 목록을 제공한다.
     */
    @GetMapping("/role-system/authentication/roleUpdateForm")
    public String roleUpdateForm(Model model, RoleMngRequestDto roleMngRequestDto) {
        roleMngRequestDto.setAllRoles(authenticationService.findAllRoles());
        roleMngRequestDto.setAllAuthorities(authenticationService.findAllAuthorities());
        return htmlBasePath + "role";
    }

    /**
     * Role-Authority 매핑 수정 요청을 검증하고 저장한 뒤 관리 화면으로 redirect 한다.
     */
    @PostMapping("/role-system/authentication/roleUpdate")
    public String roleUpdate(Model model, RedirectAttributes redirectAttributes, @Valid RoleMngRequestDto roleMngRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            roleMngRequestDto.setAllRoles(authenticationService.findAllRoles());
            roleMngRequestDto.setAllAuthorities(authenticationService.findAllAuthorities());
            return htmlBasePath + "role";
        }

        authenticationService.saveRoles(roleMngRequestDto);
        return "redirect:/view/example/role-system/authentication/roleUpdateForm";
    }


}
