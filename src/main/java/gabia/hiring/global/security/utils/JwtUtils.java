package gabia.hiring.global.security.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 토큰을 생성하고 검증하는 클래스
 * - 토큰 생성: 로그인 성공 시 JWT 토큰을 만듦
 * - 토큰 검증: 클라이언트가 보낸 토큰이 유효한지 확인
 * - 사용자명 추출: 토큰에서 사용자 정보를 뽑아냄
 */
@Component
@Slf4j
public class JwtUtils {

    //jwt 비밀키 가져오기
    @Value("${jwt.secret}")
    private String secretKey;

    //만료시간 가져오기
    @Value("${jwt.expiration")
    private int jwtExpiration;

    /**
     * 인증(로그인) 성공 후 JWT 토큰 생성
     *
     * @param authentication Spring Security의 인증객체(로그인한 사용자 정보 포함)
     * @return
     */
    public String generateToken(Authentication authentication) {

        //인증 객체에서 사용자명 추출(loginId)
        String username = authentication.getName();

        //토큰 만료 시간 계산 (현재 시간 + 설정된 만료 시간)
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpiration);

        //JWT 토큰 생성
        return Jwts.builder()
                .setSubject(username)                           //토큰의 주체(누구 토큰인지)
                .setIssuedAt(new Date())                        //토큰 발급 시간
                .setExpiration(expiryDate)                      //토큰 만료 시간(30분)
                .signWith(getSignKey())  //서명 (토큰 위조 방지)
                .compact();                                     //최종 토큰 문자열 생성
    }


    /**
     * JWT 토큰에서 사용자명을 추출하는 메서드
     * 클라이언트가 보낸 토큰에서 "누구의 토큰인지" 알아내기 위해 사용
     *
     * @param token JWT 토큰 문자열
     * @return 토큰에 포함된 사용자명
     */
    public String getUsernameFromToken(String token) {

        //JWT 토큰을 파싱해서 클레임(정보) 추출
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())                //서명 검증용 키 설정
                .build()                                   // 파서 빌드
                .parseClaimsJwt(token)                    //토큰 파싱(서명도 함께 검증)
                .getBody();                                //토큰 내용(클레임) 추출

        //Subject(주체)가 사용자명으로 반환
        return claims.getSubject();
    }

    /**
     * JWT 토큰의 유효성 검증
     * 토큰이 위조되지 않았고, 만료되지 않았는지 확인
     * @param token 검증할 JWT 토큰 문자열
     * @return 유효하면 true, 뮤효하면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJwt(token);

            return true;

        } catch (ExpiredJwtException e) {
            //토큰 만료될 경우
            log.info("JWT Token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            //지원하지 않는 JWT 토큰 형식인 경우
            log.info("JWT Token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            //토큰 형식이 잘못된 경우
            log.info("Invalid JWT token: {}", e.getMessage());
        } catch (SecurityException e) {
            //서명이 잘못된 경우
            log.info("Invalid JWT Signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            //토큰이 비어있는 경우
            log.info("JWT claims String is empty {}", e.getMessage());
        }

        //예외 발생 = 무효한 토큰
        return false;
    }

    /**
     * JWT 서명에 사용할 비밀 키를 생성
     * secretKey 값을 암호화 키로 변환
     * @return 암호화된 비밀 킴
     */
    private SecretKey getSignKey() {
        byte[] bytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(bytes);
    }


}
