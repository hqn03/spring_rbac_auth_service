package github.hqn03.auth_service.service;

import github.hqn03.auth_service.dto.auth.LoginRequest;
import github.hqn03.auth_service.dto.auth.LoginResponse;
import github.hqn03.auth_service.dto.auth.RegisterRequest;
import github.hqn03.auth_service.dto.auth.RegisterResponse;
import github.hqn03.auth_service.exception.AppException;
import github.hqn03.auth_service.exception.ResourceNotFoundException;
import github.hqn03.auth_service.model.EmailVerificationToken;
import github.hqn03.auth_service.model.Role;
import github.hqn03.auth_service.model.User;
import github.hqn03.auth_service.repository.EmailVerificationTokenRepository;
import github.hqn03.auth_service.repository.RoleRepository;
import github.hqn03.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountLockedException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;


    @NonFinal
    protected static String SECRET_KEY = "YwMMyYEYNA6CMEl7lWcVQAd5sQ/U2wiDOG4VU+ZU0RQ=";

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        if(userRepository.existsByUsername(registerRequest.username())) {
            throw new AppException("Username is existed", HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(registerRequest.email())) {
            throw new AppException("Email is existed", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(registerRequest.username());
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("User role is not found!"));

        user.addRole(userRole);

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

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.identifier(),
                    loginRequest.password()
            );

            Authentication authentication = authenticationManager.authenticate(authToken);
            User user = (User) authentication.getPrincipal();

            var token = generateToken(user);

            return new LoginResponse(token);
    }

    private String generateToken(User user) {
        Instant now = Instant.now();

        String scope = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("dev-service")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(user.getUsername())
                .claim("scope", scope)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public void forgotPassword() {};

}
