package gabia.hiring.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 인증 실패 시 처리하는 EntryPoint
 *
 * 동작 시점:
 * 1. JWT 토큰이 없는 경우
 * 2. JWT 토큰이 유효하지 않은 경우
 * 3. 인증이 필요한 리소스에 미인증 사용자가 접근한 경우
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 인증 실패 시 호출되는 메서드
     * 401 Unauthorized 응답을 JSON 형태로 반환
     *
     * @param request 요청 객체
     * @param response 응답 객체
     * @param authException 인증 예외
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.error("인증 실패 - URI: {}, 메시지: {}", request.getRequestURI(), authException.getMessage());

        // 1. 응답 헤더 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드

        // 2. 에러 응답 JSON 생성
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", "인증이 필요합니다. 토큰을 확인해주세요.");
        errorResponse.put("status", 401);
        errorResponse.put("path", request.getRequestURI());

        // 3. JSON 응답 전송
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}