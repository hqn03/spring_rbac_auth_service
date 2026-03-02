package github.hqn03.auth_service.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import github.hqn03.auth_service.dto.auth.LoginRequest;
import github.hqn03.auth_service.dto.auth.LoginResponse;
import github.hqn03.auth_service.dto.auth.RegisterRequest;
import github.hqn03.auth_service.dto.auth.RegisterResponse;
import github.hqn03.auth_service.model.EmailVerificationToken;
import github.hqn03.auth_service.model.User;
import github.hqn03.auth_service.repository.EmailVerificationTokenRepository;
import github.hqn03.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @NonFinal
    protected static String SECRET_KEY = "YwMMyYEYNA6CMEl7lWcVQAd5sQ/U2wiDOG4VU+ZU0RQ=";

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        if(userRepository.existsByUsername(registerRequest.username())) {
            throw new RuntimeException("Username is exist");
        }

        if(userRepository.existsByEmail(registerRequest.email())) {
            throw new RuntimeException("Email is exist");
        }

        User user = new User();
        user.setUsername(registerRequest.username());
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        User saved = userRepository.save(user);

        // Generate token
        String token = UUID.randomUUID().toString();
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setUser(saved);
        emailVerificationToken.setExpiredAt(LocalDateTime.now().plusDays(1));
        emailVerificationTokenRepository.save(emailVerificationToken);

        return new RegisterResponse("Registration successful. Please check your email to verify your account.");
    };

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        try{
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.identifier(),
                    loginRequest.password()
            );

            Authentication authentication = authenticationManager.authenticate(authToken);
            User user = (User) authentication.getPrincipal();

            if (!user.isEmailVerified()) {
                throw new RuntimeException("Email not verified");
            }

            var token = generateToken(user);

            return new LoginResponse(token);

        }catch(BadCredentialsException e){
            log.error("Authentication failed. {}", e.getMessage());
            throw new RuntimeException("Invalid username or password");
        }catch(Exception e){
            log.error("Login error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

//    @Transactional(readOnly = true)
//    public LoginResponse login(LoginRequest loginRequest) {
//        User user = userRepository.findByUsernameOrEmail(loginRequest.identifier(), loginRequest.identifier())
//                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
//
//        if(!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
//            throw new RuntimeException("Invalid password");
//        }
//
//        if (!user.isEmailVerified()) {
//            throw new RuntimeException("Email not verified");
//        }
//
//        var token = generateToken(user);
//
//        return new LoginResponse(token);
//
//    };
//
    public String generateToken(User user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        String scope = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))

                .claim("scope", scope)
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        }catch (JOSEException e){
            log.error("Cannot create jwt token");
            throw new RuntimeException(e);
        }
    }

    public void forgotPassword() {};

}
