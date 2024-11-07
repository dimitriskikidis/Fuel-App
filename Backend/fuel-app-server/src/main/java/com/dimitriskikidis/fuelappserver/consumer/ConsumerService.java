package com.dimitriskikidis.fuelappserver.consumer;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final UserRepository userRepository;
    private final ConsumerRepository consumerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public ConsumerSignUpResponse signUp(ConsumerSignUpRequest request) {
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
                Role.CONSUMER
        );
        user = userRepository.save(user);

        Consumer consumer = new Consumer(
                user.getId(),
                request.username()
        );
        consumer = consumerRepository.save(consumer);

        String token = jwtService.generateToken(request.email());
        return new ConsumerSignUpResponse(
                token,
                consumer.getId()
        );
    }

    public ConsumerSignInResponse signIn(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = (User) authentication.getPrincipal();
        Optional<Consumer> optionalConsumer = consumerRepository.findByUserId(user.getId());
        if (optionalConsumer.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED
            );
        }

        Consumer consumer = optionalConsumer.get();
        String token = jwtService.generateToken(request.email());
        return new ConsumerSignInResponse(
                token,
                consumer.getId(),
                consumer.getUsername()
        );
    }
}
