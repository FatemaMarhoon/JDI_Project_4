package com.example.Project4.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtRequestFilter is a filter that processes incoming HTTP requests to extract and validate JWT tokens.
 * It checks for the presence of a JWT in the Authorization header, validates it, and sets the user authentication
 * in the SecurityContext if the token is valid.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JWTUtils jwtUtils;
    /**
     * Parses the JWT from the Authorization header of the HTTP request.
     *
     * @param request The HttpServletRequest containing the Authorization header.
     * @return The JWT if present and valid; otherwise, null.
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization"); //getting the authorization from header request
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) { //checking for the bearer
            return headerAuth.substring(7, headerAuth.length()); //getting the actual token
        }
        return null;
    }
    /**
     * Filters the incoming HTTP request to extract the JWT and validate it.
     * If the token is valid, it sets the authentication in the SecurityContext.
     *
     * @param request The HttpServletRequest to filter.
     * @param response The HttpServletResponse to modify.
     * @param filterChain The FilterChain to pass the request and response to the next filter.
     * @throws ServletException If an error occurs during filtering.
     * @throws IOException If an I/O error occurs during filtering.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);  //get the username
                UserDetails userDetails = myUserDetailsService.loadUserByUsername(username); //get  the user  details by username
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
        filterChain.doFilter(request, response);
    }


}
