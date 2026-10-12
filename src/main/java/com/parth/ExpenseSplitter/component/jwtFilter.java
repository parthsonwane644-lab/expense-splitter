package com.parth.ExpenseSplitter.component;

import com.parth.ExpenseSplitter.service.jwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.List;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class jwtFilter extends OncePerRequestFilter {
    private final jwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String email = jwtService.getEmail(header.substring(7));
                var auth= new UsernamePasswordAuthenticationToken(email, null,List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            catch (Exception e) {
                // user stay not logged in
            }
            filterChain.doFilter(request, response);
        }
    }
}
