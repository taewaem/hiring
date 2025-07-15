package gabia.hiring.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 토큰 생성, 검증, 파싱을 담당하는 핵심 클래스
 * 로그인 성공 시 토큰 생성, 매 요청마다 토큰 검증에 사용됨
 */
@Component
@Slf4j
public class JwtTokenProvider {


    // JWT 토큰 서명에 사용할 비밀키
    private final SecretKey secretKey;

    // Access Token 만료 시간 (짧게 설정 - 보통 15분~1시간)
    private final long accessTokenValidityInMilliseconds;

    // Refresh Token 만료 시간 (길게 설정 - 보통 1주일~1개월)
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInMilliseconds,
                            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInMilliseconds) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds * 1000;
    }



    /**
     * Access Token 생성
     * 실제 API 호출에 사용되는 토큰 (만료시간이 짧음)
     *
     * @param authentication Spring Security의 인증 객체
     * @return 생성된 Access Token
     */
    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())    // 사용자명
                .claim("type", "access")       // 토큰 타입 명시
                .setIssuedAt(now)                           // 발행 시간
                .setExpiration(expiryDate)                  // 만료 시간 (짧음)
                .signWith(secretKey)                        // 서명
                .compact();
    }

    /**
     * Refresh Token 생성
     * Access Token 재발급에 사용되는 토큰 (만료시간이 김)
     *
     * @param authentication Spring Security의 인증 객체
     * @return 생성된 Refresh Token
     */
    public String generateRefreshToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())    // 사용자명
                .claim("type", "refresh")       // 토큰 타입 명시
                .setIssuedAt(now)                           // 발행 시간
                .setExpiration(expiryDate)                  // 만료 시간 (김)
                .signWith(secretKey)                        // 서명
                .compact();
    }

    /**
     * JWT 토큰에서 사용자명 추출
     * Access Token과 Refresh Token 모두에서 사용 가능
     *
     * @param token JWT 토큰
     * @return 토큰에 포함된 사용자명
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * JWT 토큰의 타입 확인 (access 또는 refresh)
     *
     * @param token JWT 토큰
     * @return 토큰 타입 ("access" 또는 "refresh")
     */
    public String getTokenType(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("type", String.class);
    }

    /**
     * JWT 토큰 유효성 검증
     * Access Token과 Refresh Token 모두 검증 가능
     *
     * @param token 검증할 JWT 토큰
     * @return 유효하면 true, 무효하면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }

        return false;
    }

    /**
     * Access Token인지 확인
     *
     * @param token 확인할 토큰
     * @return Access Token이면 true
     */
    public boolean isAccessToken(String token) {
        try {
            return "access".equals(getTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Refresh Token인지 확인
     *
     * @param token 확인할 토큰
     * @return Refresh Token이면 true
     */
    public boolean isRefreshToken(String token) {
        try {
            return "refresh".equals(getTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }
}
