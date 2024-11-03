package com.example.Project4.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfiguration is a configuration class that sets up the security for the application.
 * It configures HTTP security, session management, and user authentication.
 */
@Configuration
@EnableMethodSecurity // Enables method-level security annotations
public class SecurityConfiguration {

    private MyUserDetailsService myUserDetailsService;

    /**
     * Sets the MyUserDetailsService for this configuration.
     *
     * @param myUserDetailsService The MyUserDetailsService to be set.
     */
    @Autowired
    public void setMyUserDetailsService(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    /**
     * Provides a BCryptPasswordEncoder bean for password encryption.
     *
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures HTTP security for the application, including authorization rules,
     * session management, and CSRF protection.
     *
     * @param http The HttpSecurity object to configure.
     * @return The configured SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .requestMatchers(
                        "/auth/users",
                        "/auth/users/login",
                        "/auth/users/register",
                        "/auth/users/verify**",
                        "/auth/users/forgot-password**",
                        "/auth/users/reset-password**",
                        "/auth/users/request-password-reset",
                        "/auth/users/change-password**",
                        "/swagger-ui**",
                        "/swagger-ui/**",
                        "/api-docs**",
                        "/api-docs/**",
                        "files/**",
                        "/api/organizations/approved"
                )
                .permitAll()
                .anyRequest().authenticated();

        http.sessionManagement(
                sessionAuthenticationStrategy ->
                        sessionAuthenticationStrategy.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // Add the JWT authentication filter
        http.addFilterBefore(authenticationJWTTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.csrf(csrf -> csrf.disable()); // Disable Cross-Site Request Forgery protection

        return http.build();
    }

    /**
     * Provides a JwtRequestFilter bean to filter requests for JWT authentication.
     *
     * @return A JwtRequestFilter instance.
     */
    @Bean
    public JwtRequestFilter authenticationJWTTokenFilter() {
        return new JwtRequestFilter();
    }

    /**
     * Provides an AuthenticationManager bean for managing authentication processes.
     *
     * @param authenticationConfiguration The AuthenticationConfiguration to use.
     * @return The AuthenticationManager instance.
     * @throws Exception If an error occurs during the creation of the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures a DaoAuthenticationProvider bean for user authentication using a UserDetailsService and password encoder.
     *
     * @return A DaoAuthenticationProvider instance.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(myUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
