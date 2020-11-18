package com.richards275.wareneingang.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserDetailsServiceImpl userDetailsService;
  private final AuthEntryPointJwt unauthorizedHandler;

  public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, AuthEntryPointJwt unauthorizedHandler) {
    this.userDetailsService = userDetailsService;
    this.unauthorizedHandler = unauthorizedHandler;
  }

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
    authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().csrf().disable()
        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .authorizeRequests()
        .antMatchers("/swagger-ui.html").permitAll()
        .antMatchers("/swagger-ui/**").permitAll()
        .antMatchers("/v3/api-docs/**").permitAll()
        .antMatchers("/api/login").permitAll()
        .antMatchers("/api/v1/newpassword").permitAll()
        .antMatchers("/api/v1/changepassword").authenticated()
        .antMatchers("/api/v1/auth/**").hasRole("ADMIN")
        .antMatchers("/api/v1/ware/**").hasRole("MITARBEITERIN")
        .antMatchers("/api/v1/lieferung/**").hasAnyRole("MITARBEITERIN", "LIEFERANT")
        .antMatchers("/api/v1/checklieferung/**").hasRole("LIEFERANT")
        .antMatchers("/api/v1/bearbeitelieferung/**").hasRole("MITARBEITERIN")
        .antMatchers("/api/v1/csv/**").hasRole("LIEFERANT")
        .antMatchers("/api/v1/lieferant").hasAnyRole("MITARBEITERIN", "ADMIN")
        .antMatchers("/api/v1/lieferant/**").hasRole("ADMIN");

    http.addFilterBefore(
        authenticationJwtTokenFilter(),
        UsernamePasswordAuthenticationFilter.class
    );


  }
}

