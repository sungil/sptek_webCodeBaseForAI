package com._sptek.__webFramework.security.authentication.userStore;

import com._sptek.__webFramework.security.userStore.dto.UserDto;
import com._sptek.__webFramework.security.userStore.entity.User;
import com._sptek.__webFramework.security.userStore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//todo : custom하다고다 볼수 있나? custom이란 관점에서 UserDetailsService가 여러개 존재 할수 있지만 현재는 하나 임으로 "userDetailsService" 라는 기본형 네임을 여기에 달아줌
/**
 * 프레임워크 보안 User entity를 Spring Security UserDetails로 변환하는 서비스.
 *
 * <p>현재 기준 username은 email이며, JPA UserRepository에서 조회한 entity를 UserDto로 매핑해 CustomUserDetails로 감싼다.</p>
 */
@Slf4j
@Service("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * email 기준으로 사용자를 조회해 Spring Security가 사용할 UserDetails를 반환한다.
     */
    @Override
    public CustomUserDetails loadUserByUsername(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User(%s) not found.", userEmail)));
        //log.debug("user info: {}", user);

        UserDto userDto = modelMapper.map(user, UserDto.class);
        return CustomUserDetails.builder().userDto(userDto).build();
    }
}
