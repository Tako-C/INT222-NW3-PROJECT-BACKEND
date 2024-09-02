package sit.int221.mytasksservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sit.int221.mytasksservice.repositories.secondary.UsersRepository;
import sit.int221.mytasksservice.services.JwtUserDetailsService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    UsersRepository repository;

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, JwtUserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null) {
            request.setAttribute("error", "No Authorization header found");
        } else {
            try {
                String token = header.substring(7);
                String oid = jwtTokenUtil.getOid(token);

                if (oid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String username = repository.findByOid(oid).getUsername();
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtTokenUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                request.setAttribute("error", "Token Expired");
            } catch (io.jsonwebtoken.MalformedJwtException ex) {
                request.setAttribute("error", "Token is not well-formed JWT");
            } catch (io.jsonwebtoken.SignatureException ex) {
                request.setAttribute("error", "Token has been tampered with");
            } catch (Exception ex) {
                request.setAttribute("error", "Access is Denied");
            }
        }
        filterChain.doFilter(request, response);
    }
}