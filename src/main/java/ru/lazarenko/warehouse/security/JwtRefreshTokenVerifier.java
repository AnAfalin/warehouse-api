package ru.lazarenko.warehouse.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.lazarenko.warehouse.config.SecurityConfig;
import ru.lazarenko.warehouse.dto.info.ResponseDto;
import ru.lazarenko.warehouse.dto.security.RefreshToken;
import ru.lazarenko.warehouse.dto.security.TokenDto;
import ru.lazarenko.warehouse.dto.security.UserSecurityInfo;
import ru.lazarenko.warehouse.dto.security.UserTokenResponse;
import ru.lazarenko.warehouse.model.TypeToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class JwtRefreshTokenVerifier extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) {

        String path = request.getRequestURI();

        if (!path.contains("/api/refresh-token")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException("Request should be POST");
        }

        RefreshToken refreshToken = objectMapper.readValue(request.getInputStream(), RefreshToken.class);

        String message;
        try {
            UserSecurityInfo userSecurityInfo = jwtService.validateToken(refreshToken.getToken());

            TokenDto newAccessToken = jwtService.generateToken(userSecurityInfo.getUsername(),
                    userSecurityInfo.getAuthorities(),
                    TypeToken.ACCESS);
            TokenDto newRefreshToken = jwtService.generateToken(userSecurityInfo.getUsername(),
                    userSecurityInfo.getAuthorities(),
                    TypeToken.REFRESH);

            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            objectMapper.writeValue(response.getOutputStream(),
                    new UserTokenResponse(userSecurityInfo.getUsername(), newAccessToken, newRefreshToken));

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

}
