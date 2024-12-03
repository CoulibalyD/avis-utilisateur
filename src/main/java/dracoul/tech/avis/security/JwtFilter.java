package dracoul.tech.avis.security;

import dracoul.tech.avis.entity.Jwt;
import dracoul.tech.avis.service.UtilisateurService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Service
public class JwtFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final UtilisateurService utilisateurService;
    private final JwtService jwtService;

    public JwtFilter(HandlerExceptionResolver handlerExceptionResolver, UtilisateurService utilisateurService, JwtService jwtService) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.utilisateurService = utilisateurService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token;
        Jwt tokenDansLaBDD = null;
        String email = null;
        boolean isTokenExpired = true;
        try {
            final String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
                tokenDansLaBDD = this.jwtService.tokenByValue(token);
                System.out.println(tokenDansLaBDD.toString());
                isTokenExpired = jwtService.isTokenExpired(token);
                email = jwtService.extractEmail(token);
            }
            if(!isTokenExpired
                    //&& email != null
                    &&  tokenDansLaBDD.getUtilisateur().getEmail().equals(email)
                    && SecurityContextHolder.getContext().getAuthentication() == null
            ) {
                UserDetails userDetails = utilisateurService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        }catch (Exception exception){
            this.handlerExceptionResolver.resolveException(request, response, null, exception);
        }

    }

}
