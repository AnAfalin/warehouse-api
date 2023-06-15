package ru.lazarenko.warehouse.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.lazarenko.warehouse.config.JwtConfig;
import ru.lazarenko.warehouse.config.SecurityConfig;
import ru.lazarenko.warehouse.dto.info.ResponseDto;
import ru.lazarenko.warehouse.dto.security.UserSecurityInfo;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class JwtAccessTokenVerifier extends OncePerRequestFilter {
    private final JwtService jwtService;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) {

        String authorizationHeader = request.getHeader(JwtConfig.HEADER);

        String accessToken = authorizationHeader.replace(JwtConfig.TOKEN_PREFIX, "");
        String message;
        try {
            UserSecurityInfo userSecurityInfo = jwtService.validateToken(accessToken);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userSecurityInfo.getUsername(), null, userSecurityInfo.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
            return;

        } catch (MalformedJwtException ex) {
            message = "Malformed JWT";
        } catch (ExpiredJwtException ex) {
            message = "Invalid signature";
        } catch (SignatureException ex) {
            message = "JWT expired";
        } catch (Throwable ex) {
            message = "Unexpected error";
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        new ObjectMapper().writeValue(response.getOutputStream(),
                new ResponseDto(HttpStatus.UNAUTHORIZED.toString(), message));
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        return "POST".equals(method) && SecurityConfig.whiteListUrls.contains(uri);
    }
}
