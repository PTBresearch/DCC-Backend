package de.ptb.dsi.dcc_backend.service;


import de.ptb.dsi.dcc_backend.dto.LoginRequest;
import de.ptb.dsi.dcc_backend.entity.User;
import de.ptb.dsi.dcc_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUserName(request.getUserName());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid login data");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid login data");
        }

        if (!user.isActiv()) {
            throw new RuntimeException("User is not active");
        }

        return user;
    }
}
