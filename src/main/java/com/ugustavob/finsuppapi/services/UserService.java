package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.roles.AssignRoleRequestDTO;
import com.ugustavob.finsuppapi.dto.users.GetAllUsersResponseDTO;
import com.ugustavob.finsuppapi.dto.users.LoginRequestDTO;
import com.ugustavob.finsuppapi.dto.users.RegisterRequestDTO;
import com.ugustavob.finsuppapi.entities.user.UserEntity;
import com.ugustavob.finsuppapi.exception.InvalidCredentialsException;
import com.ugustavob.finsuppapi.exception.UserAlreadyExistsException;
import com.ugustavob.finsuppapi.exception.UserAlreadyHasRoleException;
import com.ugustavob.finsuppapi.exception.UserNotFoundException;
import com.ugustavob.finsuppapi.repositories.UserRepository;
import com.ugustavob.finsuppapi.security.TokenService;
import com.ugustavob.finsuppapi.utils.StringFormatUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UserEntity validateUserByIdAndReturn(UUID userId) {
        Optional<UserEntity> userEntity = userRepository.findById(userId);

        if (userEntity.isEmpty()) {
            throw new UserNotFoundException();
        }
        return userEntity.get();
    }

    public UserEntity getUserById(UUID id) {
        return userRepository.findById(UUID.fromString(id.toString()))
                .orElseThrow(UserNotFoundException::new);
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

    public UserEntity createUser(@Valid RegisterRequestDTO registerRequest) {
        Optional<UserEntity> user = userRepository.findByEmail(registerRequest.email());

        if (user.isPresent()) {
            throw new UserAlreadyExistsException();
        }

        UserEntity newUser = new UserEntity();
        newUser.setName(StringFormatUtil.toTitleCase(registerRequest.name()));
        newUser.setEmail(registerRequest.email());
        newUser.setPassword(passwordEncoder.encode(registerRequest.password()));

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        newUser.setRole(roles);

        return userRepository.save(newUser);
    }

    public UserEntity updateUser(UserEntity user) {
        Optional<UserEntity> userOptional = userRepository.findById(user.getId());

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();
        }

        UserEntity userEntity = userOptional.get();

        UserEntity alreadyExists = userRepository.findByEmail(user.getEmail()).orElse(null);

        if (alreadyExists != null && !alreadyExists.getId().equals(user.getId())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        userEntity.setName(StringFormatUtil.toTitleCase(user.getName()));
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(userEntity);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteByIdAndReturnEntity(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public UserEntity loginUser(@Valid LoginRequestDTO loginRequest) {
        Optional<UserEntity> user = userRepository.findByEmail(loginRequest.email());

        if (user.isPresent() && passwordEncoder.matches(loginRequest.password(), user.get().getPassword())) {
            return user.get();
        }

        throw new InvalidCredentialsException();
    }

    public UserEntity assignRole(@Valid AssignRoleRequestDTO assignRoleRequestDTO, UUID userId) {
        UserEntity user = getUserById(userId);

        if (user.getRole().contains("ROLE_" + assignRoleRequestDTO.role().toUpperCase())) {
            throw new UserAlreadyHasRoleException("User already has role: " + assignRoleRequestDTO.role());
        }
        user.getRole().add(StringFormatUtil.formatRole(assignRoleRequestDTO.role()));

        return userRepository.save(user);
    }
}
