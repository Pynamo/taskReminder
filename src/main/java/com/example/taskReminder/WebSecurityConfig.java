package com.example.taskReminder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import com.example.taskReminder.entity.User;
import com.example.taskReminder.common.Authority;
import com.example.taskReminder.entity.SocialUser;


import com.example.taskReminder.filter.FormAuthenticationProvider;
import com.example.taskReminder.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	@Autowired
    private FormAuthenticationProvider authenticationProvider;
	@Autowired
	private UserRepository userRepository;

    private static final String[] URLS = { "/css/**", "/js/**", "/images/**", "/scripts/**", "/h2-console/**", "/favicon.ico" };

    /**
    * 認証から除外する
    */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() throws Exception {
        return web -> web.ignoring().antMatchers(URLS);
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
			).oauth2Login(oauth2 -> oauth2
			.loginPage("/login")
			.defaultSuccessUrl("/")
			.failureUrl("/login-failure")
			.permitAll()
			.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.oidcUserService(this.oidcUserService()))
			)
			// ログアウトをトリガーするURL
		  	.logout(logout -> logout.logoutUrl("/logout")
		  	// ログアウト成功時のリダイレクト先URL		
		    .logoutSuccessUrl("/login")
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
            //.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            // ここで指定したURLにはログインなしでもアクセス可能
      		//.antMatchers("/login", "/logout-complete", "/users/new", "/users/create", "/user", "/h2-console", "/css/**", "/js/**").permitAll()	
		    .antMatchers("/login", "/logout-complete", "/users/new", "/users/create", "/user", "/css/**", "/js/**").permitAll()	
            .anyRequest().authenticated()
            )
		    .authenticationProvider(authenticationProvider);
		return http.build();
    }
  
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();
        return (userRequest) -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);
            OAuth2AccessToken accessToken = userRequest.getAccessToken();

            //log.debug("accessToken={}", accessToken);

            oidcUser = new DefaultOidcUser(oidcUser.getAuthorities(), oidcUser.getIdToken(), oidcUser.getUserInfo());
            String email = oidcUser.getEmail();
            User user = userRepository.findByUsername(email);
            if (user == null) {
                user = new User(email, oidcUser.getFullName(), "", Authority.ROLE_USER);
                userRepository.saveAndFlush(user);
            }
            oidcUser = new SocialUser(oidcUser.getAuthorities(), oidcUser.getIdToken(), oidcUser.getUserInfo(),
                    user.getUserId());

            return oidcUser;
        };
    }
  
}