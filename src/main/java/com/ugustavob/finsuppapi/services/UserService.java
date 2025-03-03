package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.users.GetAllUsersResponseDTO;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.exception.UserNotFoundException;
import com.ugustavob.finsuppapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserEntity validateUserByIdAndReturn(UUID userId) {
        Optional<UserEntity> userEntity = userRepository.findById(userId);

        if (userEntity.isEmpty()) {
            throw new UserNotFoundException();
        }
        return userEntity.get();
    }

    public Page<GetAllUsersResponseDTO> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> usersPage = userRepository.findAll(pageable);

        return usersPage.map(user -> new GetAllUsersResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        ));
    }
}
