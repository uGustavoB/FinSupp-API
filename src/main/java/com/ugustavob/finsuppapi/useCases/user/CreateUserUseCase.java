package com.ugustavob.finsuppapi.useCases.user;

import com.ugustavob.finsuppapi.dto.users.RegisterRequestDTO;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.exception.UserAlreadyExistsException;
import com.ugustavob.finsuppapi.repositories.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity execute(@Valid RegisterRequestDTO registerRequest) {
        Optional<UserEntity> user = userRepository.findByEmail(registerRequest.email());

        if (user.isEmpty()) {
            UserEntity newUser = new UserEntity();
            newUser.setName(registerRequest.name());
            newUser.setEmail(registerRequest.email());
            newUser.setPassword(passwordEncoder.encode(registerRequest.password()));

            Set<String> roles = new HashSet<>();
            roles.add("ROLE_USER");
            newUser.setRole(roles);

            return userRepository.save(newUser);
        }

        throw new UserAlreadyExistsException();
    }
}
