//package gabia.hiring.global.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//
//@Configuration
//@EnableWebSecurity  // 웹 보안(Spring Security) 활성화
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final CustomUserDetailsService customUserDetailsService;
//    private final JwtFilter jwtAuthenticationFilter;
//    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//
//    /**
//     * 비밀번호 해시화(공개키 암호화방식)
//     * 비밀번호 암호화를 위한 PasswordEncoder 빈 등록
//     * BCrypt 해시 함수를 사용하여 비밀번호를 안전하게 저장
//     * @return BCryptPasswordEncoder 인스턴스
//     */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    /**
//     * 인증 관리자 빈 등록
//     * 로그인 처리 시 사용자 인증을 담당
//     * @param authConfig 인증 설정 객체
//     * @return AuthenticationManager 인스턴스
//     */
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//
//    /**
//     * Spring Security 필터 체인 설정
//     * 보안 정책, 인증/인가 규칙, JWT 필터 등록을 구성
//     * @param http HttpSecurity 객체
//     * @return SecurityFilterChain 인스턴스
//     */
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                // 1. CSRF 비활성화 (JWT 사용으로 불필요)
//                .csrf(AbstractHttpConfigurer::disable)
//
//                // 2. 세션 사용 안함 (JWT는 Stateless)
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//
//                // 3. 인증 실패 시 처리할 EntryPoint 설정
//                .exceptionHandling(exception ->
//                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                )
//
//                // 4. 요청별 인증/인가 규칙 설정
//                .authorizeHttpRequests(auth -> auth
//                        // 인증 없이 접근 가능한 URL들 (회원가입, 로그인 등)
//                        .requestMatchers("/api/auth/**").permitAll()
//                        // Swagger 관련 URL들 (개발 시에만 사용)
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                        // 기타 정적 리소스들
//                        .requestMatchers("/favicon.ico", "/error").permitAll()
//
//                        // 나머지 모든 요청은 인증 필요
//                        .anyRequest().authenticated()
//                )
//                // 5. JWT 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 추가
//                // 요청이 들어오면 JWT 필터가 먼저 토큰을 검증하고 인증 정보를 설정
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }