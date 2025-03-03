package com.ugustavob.finsuppapi.useCases.user;

import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.exception.UserNotFoundException;
import com.ugustavob.finsuppapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteUserUseCase {
    private final UserRepository userRepository;

    public void execute(UUID id) {
        UserEntity deletedUser = userRepository.deleteByIdAndReturnEntity(id)
                .orElseThrow(UserNotFoundException::new);
    }
}
