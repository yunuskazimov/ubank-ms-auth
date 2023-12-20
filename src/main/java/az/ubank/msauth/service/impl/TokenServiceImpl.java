package az.ubank.msauth.service.impl;

import az.ubank.msauth.dto.response.UserResponseDto;
import az.ubank.msauth.exception.RefreshTokenException;
import az.ubank.msauth.service.TokenService;
import az.ubank.msauth.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final UserService userService;

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String customerPin = decodedJWT.getSubject();
                UserResponseDto userEntity
                        = userService.getUser(customerPin);
                String accessToken = JWT.create()
                        .withSubject(userEntity.getCustomerPin())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURI())
                        .sign(algorithm);

                response.setHeader("access_token", accessToken);
                response.setHeader("refresh_token", refreshToken);
            } catch (Exception ex) {
                log.error("Error loging in: {}", ex.getMessage());
                response.setHeader("error", ex.getMessage());
                response.setStatus(FORBIDDEN.value());
                response.sendError(FORBIDDEN.value());

                Map<String, String> error = new HashMap<>();
                error.put("error_message", ex.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RefreshTokenException("Refresh token is missing");
        }
    }

    @Override
    public void activeToken(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String customerPin = decodedJWT.getSubject();
            log.info("customerPin: {}", customerPin);
            String pin = userService.getUser(customerPin).getCustomerPin();
            response.addHeader("Customer-Pin", pin);
        }
    }

}
