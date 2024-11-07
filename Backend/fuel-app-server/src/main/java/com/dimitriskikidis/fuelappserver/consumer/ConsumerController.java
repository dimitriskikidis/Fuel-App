package com.dimitriskikidis.fuelappserver.consumer;

import com.dimitriskikidis.fuelappserver.auth.AuthenticationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/consumers")
@RequiredArgsConstructor
public class ConsumerController {

    private final ConsumerService consumerService;

    @PostMapping(path = "signUp")
    public ResponseEntity<ConsumerSignUpResponse> signUp(
            @RequestBody ConsumerSignUpRequest request
    ) {
        ConsumerSignUpResponse response = consumerService.signUp(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(path = "signIn")
    public ResponseEntity<ConsumerSignInResponse> signIn(
            @RequestBody AuthenticationRequest request
    ) {
        ConsumerSignInResponse response = consumerService.signIn(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
