package example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

    // デフォルトと同じ動きの参考実装：特別なことをしないなら、この設定は不要
    public static class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
                    UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(request.getParameter("username"), request.getParameter("password"));
            return this.getAuthenticationManager().authenticate(token);
        }
    }

    @Configuration
    public static class CustomAuthenticationProvider implements AuthenticationProvider {
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            Object principal = authentication.getPrincipal();
            String password = (String)authentication.getCredentials();

            // 認証の参照実装
            if (! principal.equals("user")) throw new UsernameNotFoundException("");
            if (! password.equals("pass")) throw new BadCredentialsException("");

            // ロールの付与
            Collection<GrantedAuthority> authorityList = List.of();

            return new UsernamePasswordAuthenticationToken(principal, password, authorityList);
        }

        @Override
        public boolean supports(Class<?> authentication) {
            return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
        }
    }

    public static class CustomUserDetails extends User {
        public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
            super(username, password, authorities);
        }
    }

    public static class CustomUserDetailsService implements UserDetailsService {

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

            return null;
        }
    }

    @Configuration
    @EnableWebSecurity
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {

        CustomAuthenticationProvider authenticationProvider;

        public SecurityConfig( CustomAuthenticationProvider authenticationProvider) {
            this.authenticationProvider = authenticationProvider;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/", "/home").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .and()
                    .logout()
                    .permitAll();

            // デフォルトと同じ動きの参考実装：特別なことをしないなら、この設定は不要
            CustomAuthenticationFilter filter = new CustomAuthenticationFilter();
            filter.setAuthenticationManager(authenticationManagerBean());
            http.addFilterBefore(filter, CustomAuthenticationFilter.class);
        }

        @Override
        protected void configure(AuthenticationManagerBuilder builder) throws Exception {
            builder.authenticationProvider(authenticationProvider);
        }
    }
}
