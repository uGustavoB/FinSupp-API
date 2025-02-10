package com.ugustavob.finsuppapi.useCases.role;

import com.ugustavob.finsuppapi.dto.AssignRoleRequestDTO;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.exceptions.UserAlreadyHasRoleException;
import com.ugustavob.finsuppapi.repositories.user.UserRepository;
import com.ugustavob.finsuppapi.useCases.user.GetUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignRoleUseCase {
    private final GetUserUseCase getUserUseCase;
    private final UserRepository userRepository;

    public UserEntity execute(@Valid AssignRoleRequestDTO assignRoleRequestDTO, UUID userId) {
        UserEntity user = getUserUseCase.execute(userId);

        if (user.getRole().contains("ROLE_" + assignRoleRequestDTO.role().toUpperCase())) {
            throw new UserAlreadyHasRoleException("User already has role: " + assignRoleRequestDTO.role());
        }
        user.getRole().add("ROLE_" + assignRoleRequestDTO.role().toUpperCase());

        return userRepository.save(user);
    }
}
