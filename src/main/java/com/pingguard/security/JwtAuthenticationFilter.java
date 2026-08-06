package com.pingguard.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Get the token from the user's HTTP request header
        String token = getJWTFromRequest(request);

        // 2. Check if they gave us a token, AND if the Ticket Printer says it's real
        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {

            // 3. It's real! Read the email off the token
            String email = tokenProvider.getEmailFromToken(token);

            // 4. Load the user's details from the database
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 5. Create a new "ID Badge" (Authentication object) that says this user is officially logged in
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 6. Tell Spring Security: "This person is safe, let them into the club!"
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // 7. Move on to the next filter or let them through the door
        filterChain.doFilter(request, response);
    }

    // A helper method to pull the token out of the HTTP header
    private String getJWTFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Removes the word "Bearer " and returns just the token
        }
        return null;
    }
}
