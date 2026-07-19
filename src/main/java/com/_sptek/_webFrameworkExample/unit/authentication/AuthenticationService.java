package com._sptek._webFrameworkExample.unit.authentication;

import com._sptek.__webFramework.core.exception.ServiceException;
import com._sptek._webFrameworkExample.unit.authentication.userStore.dto.*;
import com._sptek._webFrameworkExample.unit.authentication.userStore.entity.*;
import com._sptek._webFrameworkExample.unit.authentication.userStore.repository.AuthorityRepository;
import com._sptek._webFrameworkExample.unit.authentication.userStore.repository.RoleRepository;
import com._sptek._webFrameworkExample.unit.authentication.userStore.repository.TermsRepository;
import com._sptek._webFrameworkExample.unit.authentication.userStore.repository.UserRepository;
import com._sptek._webFrameworkExample.common.resultCode.ServiceErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 인증 예제 화면과 API에서 사용하는 사용자, Role, Authority, 약관 데이터를 처리하는 서비스.
 *
 * <p>Spring Security extras 패키지의 기본 엔티티와 repository를 사용하는 예제이며,
 * 회원 가입/수정, Role-Authority 매핑 수정, 메서드 보안 검증 흐름을 한곳에서 보여준다.
 * 서비스 외부로는 entity 대신 DTO를 반환해 조회 트랜잭션에서 가져온 entity가 수정 흐름에 섞이지 않게 한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TermsRepository termsRepository;
    private final AuthorityRepository authorityRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 가입 요청의 Role, 약관 이름을 기준 데이터와 매칭하고 비밀번호를 암호화해 사용자를 저장한다.
     */
    @Transactional
    public UserDto saveUser(SignupRequestDto signupRequestDto){
        List<RoleDto> roles = findRolesByRoleNameIn(signupRequestDto.getRoles().stream().map(RoleDto::getRoleName).collect(Collectors.toList()));
        List<TermsDto> terms = findTermsByTermsNameIn(signupRequestDto.getTerms().stream().map(TermsDto::getTermsName).collect(Collectors.toList()));

        signupRequestDto.setRoles(roles);
        signupRequestDto.setTerms(terms);
        signupRequestDto.setPassword(bCryptPasswordEncoder.encode(signupRequestDto.getPassword()));
        User user = modelMapper.map(signupRequestDto, User.class);
        log.debug("new userEntity : {}", user);
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    /**
     * 이메일로 기존 사용자를 찾아 가입 정보, 주소, Role, 약관 동의 정보를 갱신한다.
     *
     * <p>기존 영속 entity를 수정해 JPA dirty checking으로 반영하는 예제이므로 쓰기 트랜잭션에서만 사용한다.</p>
     */
    @Transactional
    public UserDto updateUser(UserUpdateRequestDto userUpdateRequestDto){
        List<RoleDto> roles = findRolesByRoleNameIn(userUpdateRequestDto.getRoles().stream().map(RoleDto::getRoleName).collect(Collectors.toList()));
        List<TermsDto> terms = findTermsByTermsNameIn(userUpdateRequestDto.getTerms().stream().map(TermsDto::getTermsName).collect(Collectors.toList()));

        userUpdateRequestDto.setRoles(roles);
        userUpdateRequestDto.setTerms(terms);
        userUpdateRequestDto.setPassword(bCryptPasswordEncoder.encode(userUpdateRequestDto.getPassword()));

        User originUser = userRepository.findByEmail(userUpdateRequestDto.getEmail())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, String.format("No user found with this email : %s", userUpdateRequestDto.getEmail())));

        originUser.setName(userUpdateRequestDto.getName());
        originUser.setEmail(userUpdateRequestDto.getEmail());
        originUser.setPassword(userUpdateRequestDto.getPassword());
        originUser.setUserAddresses(modelMapper.map(userUpdateRequestDto.getUserAddresses(), new TypeToken<List<UserAddress>>() {}.getType()));
        originUser.setRoles(modelMapper.map(userUpdateRequestDto.getRoles(), new TypeToken<List<Role>>() {}.getType()));
        originUser.setTerms(modelMapper.map(userUpdateRequestDto.getTerms(), new TypeToken<List<Terms>>() {}.getType()));


        log.debug("update userEntity : {}", originUser);
        return modelMapper.map(originUser, UserDto.class);
    }

    /**
     * 화면 선택 항목 구성을 위해 전체 Role 목록을 DTO로 반환한다.
     */
    @Transactional(readOnly = true)
    public List<RoleDto> findAllRoles(){
        List<Role> roles = requireNotEmpty(roleRepository.findAll(), "any Role found");

        return modelMapper.map(roles, new TypeToken<List<RoleDto>>() {}.getType());

//        return Optional.ofNullable(roleRepository.findAll())
//                .filter(roles -> !roles.isEmpty())
//                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Role found"))
//                .stream()
//                .map(role -> modelMapper.map(role, RoleDto.class))
//                .collect(Collectors.toList());
    }

    /**
     * 요청에 포함된 Role 이름 목록을 기준 데이터 Role DTO 목록으로 변환한다.
     */
    @Transactional(readOnly = true)
    public List<RoleDto> findRolesByRoleNameIn(List<String> roleNames){
        List<Role> roles = requireNotEmpty(roleRepository.findByRoleNameIn(roleNames), "any Role found");

        return modelMapper.map(roles, new TypeToken<List<RoleDto>>() {}.getType());
    }

    /**
     * 화면 선택 항목 구성을 위해 전체 약관 목록을 DTO로 반환한다.
     */
    @Transactional(readOnly = true)
    public List<TermsDto> findAllTerms(){
        List<Terms> terms = requireNotEmpty(termsRepository.findAll(), "any Terms found");

        return modelMapper.map(terms, new TypeToken<List<TermsDto>>() {}.getType());
    }

    /**
     * 요청에 포함된 약관 이름 목록을 기준 데이터 약관 DTO 목록으로 변환한다.
     */
    @Transactional(readOnly = true)
    public List<TermsDto> findTermsByTermsNameIn(List<String> termsNames){
        List<Terms> terms = requireNotEmpty(termsRepository.findByTermsNameIn(termsNames), "any Terms found");

        return modelMapper.map(terms, new TypeToken<List<TermsDto>>() {}.getType());
    }

    /**
     * 이메일에 해당하는 사용자 정보를 DTO로 조회한다.
     */
    @Transactional(readOnly = true)
    public UserDto findUserByEmail(String email) {
        return modelMapper
                .map(userRepository.findByEmail(email)
                                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, String.format("No user found with this email : %s", email)))
                        , UserDto.class);
    }

    /**
     * Role 관리 화면의 선택 항목 구성을 위해 전체 Authority 목록을 DTO로 반환한다.
     */
    @Transactional(readOnly = true)
    public List<AuthorityDto> findAllAuthorities() {
        List<Authority> authorities = requireNotEmpty(authorityRepository.findAll(), "any Authority found");

        return modelMapper.map(authorities, new TypeToken<List<AuthorityDto>>() {}.getType());
    }

    /**
     * Role 관리 요청에 포함된 Role 이름과 Authority 목록을 기존 Role entity에 반영한다.
     */
    @Transactional
    public List<RoleDto> saveRoles(RoleMngRequestDto roleMngRequestDto){
        Map<Long, RoleDto> reqRolesMap = roleMngRequestDto.getAllRoles().stream().collect(Collectors.toMap(RoleDto::getId, role -> role));
        List<Role> originRoles = roleRepository.findAllById(reqRolesMap.keySet());

        for(Role originRole : originRoles){
            originRole.setRoleName(reqRolesMap.get(originRole.getId()).getRoleName());

            Optional.ofNullable(reqRolesMap.get(originRole.getId()).getAuthorities())
                    .ifPresentOrElse(
                            authorities -> originRole.setAuthorities(
                                    authorityRepository.findByAuthorityIn(
                                            authorities.stream()
                                                    .map(AuthorityDto::getAuthority)
                                                    .toList()
                                    )
                            ),
                            () -> originRole.setAuthorities(Collections.emptyList())
                    );
        }

        return modelMapper.map(originRoles, new TypeToken<List<RoleDto>>() {}.getType());
    }

    /**
     * 메서드 보안에서 특정 Authority 보유 여부를 검사하는 예제 메서드.
     */
    @PreAuthorize("hasAuthority(T(com._sptek._webFrameworkExample.unit.authentication.authorization.AuthorityEnum).AUTH_SPECIAL_FOR_TEST.name())")
    public String iNeedAuth() {
        return "I Need Specific Auth";
    }

    /**
     * 메서드 보안에서 ADMIN Role 보유 여부를 검사하는 예제 메서드.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public String iNeedRole() {
        return "I Need Specific Role";
    }

    private <T> List<T> requireNotEmpty(List<T> values, String exceptionMessage) {
        if (values == null || values.isEmpty()) {
            throw new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, exceptionMessage);
        }

        return values;
    }

}
