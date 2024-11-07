package com.dimitriskikidis.fuelappserver.owner;

import com.dimitriskikidis.fuelappserver.auth.AuthenticationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @PostMapping(path = "signUp")
    public ResponseEntity<OwnerSignUpResponse> signUp(
            @RequestBody OwnerSignUpRequest request
    ) {
        OwnerSignUpResponse response = ownerService.signUp(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(path = "signIn")
    public ResponseEntity<OwnerSignInResponse> signIn(
            @RequestBody AuthenticationRequest request
    ) {
        OwnerSignInResponse response = ownerService.signIn(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
