package com.ugustavob.finsuppapi.useCases.user;

import com.ugustavob.finsuppapi.dto.GetAllUsersResponseDTO;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllUsersUseCase {
    private final UserRepository userRepository;

    public List<GetAllUsersResponseDTO> execute() {
        List<UserEntity> users = userRepository.findAll();

        if (users.isEmpty()) {
            return null;
        }

        return users.stream().map(user -> new GetAllUsersResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        )).toList();
    }
}
