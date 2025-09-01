package todo_demo.config;

import todo_demo.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 無効化（H2コンソール用）
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            // H2コンソールのフレーム許可
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            // 認可設定
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register", "/login", "/css/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            // ログインフォーム
            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/todos", true) // ログイン成功後は必ず /todos
                .permitAll()
            )
            // ログアウト設定
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout") // ログアウト後は /login に戻る
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }
}
