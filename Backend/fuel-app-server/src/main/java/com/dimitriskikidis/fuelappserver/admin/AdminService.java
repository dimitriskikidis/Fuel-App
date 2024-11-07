package com.dimitriskikidis.fuelappserver.admin;

import com.dimitriskikidis.fuelappserver.auth.AuthenticationRequest;
import com.dimitriskikidis.fuelappserver.jwt.JwtService;
import com.dimitriskikidis.fuelappserver.user.Role;
import com.dimitriskikidis.fuelappserver.user.User;
import com.dimitriskikidis.fuelappserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AdminSignUpResponse signUp(AdminSignUpRequest request) {
        boolean emailExists = userRepository.existsByEmail(request.email());
        if (emailExists) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This email is taken."
            );
        }

        User user = new User(
                request.email(),
                passwordEncoder.encode(request.password()),
                Role.ADMIN
        );
        userRepository.save(user);
        String token = jwtService.generateToken(request.email());
        return new AdminSignUpResponse(token);
    }

    public AdminSignInResponse signIn(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        String token = jwtService.generateToken(request.email());
        return new AdminSignInResponse(token);
    }
}
