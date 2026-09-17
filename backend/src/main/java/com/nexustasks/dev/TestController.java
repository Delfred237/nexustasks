package com.nexustasks.dev;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/secured")
    public ResponseEntity<String> testSecuredEndpoint(Authentication authentication) {
        // Si on arrive ici, c'est que le JwtAuthenticationFilter a validé le token
        // et a peuplé le SecurityContext.
        String userEmail = authentication.getName();
        return ResponseEntity.ok("Hello " + userEmail + ", you have successfully accessed a protected resource!");
    }
}
