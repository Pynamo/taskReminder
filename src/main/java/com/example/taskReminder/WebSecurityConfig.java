package com.example.taskReminder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.example.taskReminder.filter.FormAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	@Autowired
    private FormAuthenticationProvider authenticationProvider;

    private static final String[] URLS = { "/css/**", "/images/**", "/scripts/**", "/h2-console/**", "/favicon.ico" };

    /**
    * 認証から除外する
    */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() throws Exception {
        return (web) -> web.ignoring().antMatchers(URLS);
    }
    
    /**
     * 認証を設定する
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	
    	// ログインの設定記述開始
		http.formLogin(login -> login
			// ログイン画面URL
			.loginPage("/login")
			// ログイン成功時のリダイレクト先URL
			.defaultSuccessUrl("/")
			// ログイン失敗時のリダイレクト先URL
			.failureUrl("/login-failure")
			// ログイン画面は非ログイン状態でもアクセス可能
			.permitAll()
			)
			// ログアウトをトリガーするURL
		  	.logout(logout -> logout.logoutUrl("/logout")
		  	// ログアウト成功時のリダイレクト先URL		
		    .logoutSuccessUrl("/logout-complete")
		    // ログアウト時に認証情報をクリア
		    .clearAuthentication(true)
		    // ログアウト成功時に削除するCookie名を指定
		    .deleteCookies("JSESSIONID")
		    // ログアウト時にHttpSession無効化
		    .invalidateHttpSession(true)
		    .permitAll()
		    )
		    .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		    )
		    .authorizeHttpRequests(authz -> authz
		    // CSS等の静的ファイルはログインなしでもアクセス可能
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            // ここで指定したURLにはログインなしでもアクセス可能
      		.antMatchers("/login", "/logout-complete", "/users/new", "/users/create", "/user", "/h2-console").permitAll()				
            .anyRequest().authenticated()
            )
		    .authenticationProvider(authenticationProvider);
		return http.build();
    }
  
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
  
}