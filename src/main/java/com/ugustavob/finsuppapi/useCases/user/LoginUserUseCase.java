package com.ugustavob.finsuppapi.useCases.user;

import com.ugustavob.finsuppapi.dto.users.LoginRequestDTO;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.exception.InvalidCredentialsException;
import com.ugustavob.finsuppapi.repositories.user.UserRepository;
import com.ugustavob.finsuppapi.security.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginUserUseCase {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public UserEntity execute(@Valid LoginRequestDTO loginRequest) {
        Optional<UserEntity> user = userRepository.findByEmail(loginRequest.email());

        if (user.isPresent() && passwordEncoder.matches(loginRequest.password(), user.get().getPassword())) {
            String token = tokenService.generateToken(user.get());
            return user.get();
        }

        throw new InvalidCredentialsException();
    }
}
