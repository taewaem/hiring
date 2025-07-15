package gabia.hiring.global.security.service;

import gabia.hiring.domain.user.entity.User;
import gabia.hiring.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security에서 사용자 정보를 조회하는 서비스
 *
 * 역할:
 * 1. 로그인 시 사용자 인증에 사용
 * 2. JWT 필터에서 토큰의 사용자명으로 사용자 정보 조회
 * 3. Spring Security가 사용자 정보를 필요로 할 때마다 호출
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 사용자명으로 사용자 정보 조회
     * Spring Security가 인증 과정에서 자동으로 호출하는 메서드
     *
     * 호출 시점:
     * 1. 로그인 시 - AuthenticationManager가 사용자 존재 여부 확인
     * 2. JWT 필터에서 - 토큰에서 추출한 사용자명으로 사용자 정보 조회
     * 3. @AuthenticationPrincipal 사용 시 - 현재 인증된 사용자 정보 제공
     *
     * @param email 조회할 사용자명
     * @return UserDetails 인터페이스를 구현한 User 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없을 때
     */
    @Override
    @Transactional(readOnly = true)  // 조회용이므로 읽기 전용 트랜잭션
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.debug("사용자 조회 시도: {}", email);

        // 1. 사용자명으로 DB에서 사용자 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없습니다: {}", email);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
                });

        log.debug("사용자 조회 성공: {}, 권한: {}", email, user.getRole());

        // 2. User 엔티티가 UserDetails를 구현하므로 그대로 반환
        // Spring Security가 이 정보를 사용하여:
        // - 비밀번호 검증 (로그인 시)
        // - 권한 확인 (접근 제어)
        // - 사용자 정보 제공 (@AuthenticationPrincipal)
        return (UserDetails) user;
    }
}
