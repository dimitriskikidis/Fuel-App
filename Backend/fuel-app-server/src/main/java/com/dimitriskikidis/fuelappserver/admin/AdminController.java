package com.dimitriskikidis.fuelappserver.admin;

import com.dimitriskikidis.fuelappserver.auth.AuthenticationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping(path = "signUp")
    public ResponseEntity<AdminSignUpResponse> signUp(
            @RequestBody AdminSignUpRequest request
    ) {
        AdminSignUpResponse response = adminService.signUp(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(path = "signIn")
    public ResponseEntity<AdminSignInResponse> signIn(
            @RequestBody AuthenticationRequest request
    ) {
        AdminSignInResponse response = adminService.signIn(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
