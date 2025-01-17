package classfit.example.classfit.common.config;

import classfit.example.classfit.auth.exception.CustomAccessDeniedHandler;
import classfit.example.classfit.auth.exception.CustomAuthenticationEntryPoint;
import classfit.example.classfit.auth.security.custom.CustomAuthenticationProvider;
import classfit.example.classfit.auth.security.custom.CustomUserDetailService;
import classfit.example.classfit.auth.security.filter.CustomLoginFilter;
import classfit.example.classfit.auth.security.jwt.JWTFilter;
import classfit.example.classfit.auth.security.jwt.JWTUtil;
import classfit.example.classfit.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomUserDetailService customUserDetailService;
    private final RedisUtil redisUtil;
    private final JWTUtil jwtUtil;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(customUserDetailService, bCryptPasswordEncoder());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {

        CustomLoginFilter customLoginFilter = new CustomLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, redisUtil);
        customLoginFilter.setFilterProcessesUrl("/api/v1/signin");

        security
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(customAuthenticationProvider());

        security
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**").permitAll()  // 스웨거 관련 엔드포인트 허용
                .requestMatchers("/api/v1/signin", "/api/v1/signup", "/api/v1/reissue", "/api/v1/mail/send", "/api/v1/mail/verify").permitAll()
                .requestMatchers("/api/v1/academy/code", "/api/v1/academy/register", "/api/v1/academy/create", "/api/v1/academy/invite").permitAll()
                .requestMatchers("/api/v1/**").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated());

        security
            .addFilterBefore(new JWTFilter(customUserDetailService, jwtUtil), CustomLoginFilter.class)
            .addFilterAt(customLoginFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // 인증되지 않은 사용자 처리
                .accessDeniedHandler(new CustomAccessDeniedHandler()) // 인증
            );

        return security.build();
    }
}
