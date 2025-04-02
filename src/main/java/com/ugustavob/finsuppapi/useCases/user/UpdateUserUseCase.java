package com.ugustavob.finsuppapi.useCases.user;

import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.exception.UserNotFoundException;
import com.ugustavob.finsuppapi.repositories.UserRepository;
import com.ugustavob.finsuppapi.utils.StringFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity execute(UserEntity user) {
        Optional<UserEntity> userOptional = userRepository.findById(user.getId());

        if (userOptional.isPresent()) {
            UserEntity userEntity = userOptional.get();

            UserEntity alreadyExists = userRepository.findByEmail(user.getEmail()).orElse(null);

            if (alreadyExists != null && !alreadyExists.getId().equals(user.getId())) {
                throw new RuntimeException("Email already exists");
            }

            userEntity.setName(StringFormatUtil.toTitleCase(user.getName()));
            userEntity.setEmail(user.getEmail());
            userEntity.setPassword(passwordEncoder.encode(user.getPassword()));

            return userRepository.save(userEntity);
        }

        throw new UserNotFoundException();
    }
}
