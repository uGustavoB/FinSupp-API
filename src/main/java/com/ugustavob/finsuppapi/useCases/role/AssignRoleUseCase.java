package com.ugustavob.finsuppapi.useCases.role;

import com.ugustavob.finsuppapi.dto.roles.AssignRoleRequestDTO;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.exception.UserAlreadyHasRoleException;
import com.ugustavob.finsuppapi.repositories.UserRepository;
import com.ugustavob.finsuppapi.useCases.user.GetUserUseCase;
import com.ugustavob.finsuppapi.utils.StringFormatUtil;
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
        user.getRole().add(StringFormatUtil.formatRole(assignRoleRequestDTO.role()));

        return userRepository.save(user);
    }
}
