package com.dimitriskikidis.fuelappserver.owner;

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
public class OwnerService {

    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public OwnerSignUpResponse signUp(OwnerSignUpRequest request) {
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
                Role.OWNER
        );
        user = userRepository.save(user);

        Owner owner = new Owner(
                user.getId(),
                request.firstName(),
                request.lastName()
        );
        ownerRepository.save(owner);
        String token = jwtService.generateToken(request.email());
        return new OwnerSignUpResponse(
                token,
                owner.getId()
        );
    }

    public OwnerSignInResponse signIn(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = (User) authentication.getPrincipal();
        Optional<Owner> optionalOwner = ownerRepository.findByUserId(user.getId());
        if (optionalOwner.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED
            );
        }

        Owner owner = optionalOwner.get();
        String token = jwtService.generateToken(request.email());
        return new OwnerSignInResponse(
                token,
                owner.getId(),
                owner.getFirstName(),
                owner.getLastName(),
                owner.getFuelStationId()
        );
    }
}
