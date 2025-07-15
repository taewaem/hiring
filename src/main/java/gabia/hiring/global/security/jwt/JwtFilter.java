package gabia.hiring.global.security.jwt;

import gabia.hiring.global.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터
 * 매 HTTP 요청마다 실행되어 Access Token을 확인하고 인증 처리
 * Spring Security 필터 체인에 등록되어 컨트롤러 실행 전에 동작
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;



    /**
     * 매 요청마다 실행되는 필터 메서드
     * 1. 요청 헤더에서 JWT 토큰 추출
     * 2. 토큰 유효성 검증
     * 3. 유효한 토큰이면 Spring Security Context에 인증 정보 설정
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 다음 필터로 전달하기 위한 체인
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //요청 헤더에서 JWT 토큰 추출
        String jwt = getJwtFromRequest(request);

        //토큰이 존재하고 유효한지 확인
        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {

            //Access Token인지 확인(Refresh Token은 API 호출에 사용 불가)
            if (jwtTokenProvider.isAccessToken(jwt)) {

                //토큰에서 사용자명 추출
                String loginId = jwtTokenProvider.getUsernameFromToken(jwt);

                //사용자 정보 조회
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginId);

                //인증 객체 생성 (비밀번호는 NULL로 설정 - 이미 토큰으로 인증됨)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,           // 사용자 정보
                                null,                 // 비밀번호 (토큰 인증이므로 null)
                                userDetails.getAuthorities()  // 권한 정보
                        );

                //요청 세부정보 설정(IP, 세션 ID 등)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //Spring Security Context에 인증 정보 설정
                //이후 컨트롤러에서 @AuthenticationPrincipal로 사용자 정보 접근 가능
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("사용자  '{}' 인증 완료", loginId);
            }else {
                log.warn("Access Token이 아닌 토큰으로 API 호출 시도");
            }
        }

        //다음 필터로 요청 전달(필터 체인 계속 진행)
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청 헤더에서 JWT 토큰 추출
     * Authorization 헤더에서 "Bearer " 접두사를 제거하고 토큰만 반환
     *
     * @param request HTTP 요청 객체
     * @return JWT 토큰 문자열 (없으면 null)
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        // Authorization 헤더 값 조회
        String bearerToken = request.getHeader("Authorization");

        // "Bearer "로 시작하는지 확인하고 토큰 부분만 추출
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후 문자열 반환
        }

        return null; // 토큰이 없거나 형식이 잘못된 경우
    }
}
