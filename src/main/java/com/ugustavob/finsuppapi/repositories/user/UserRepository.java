package com.ugustavob.finsuppapi.repositories.user;

import com.ugustavob.finsuppapi.entities.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findById(UUID id);
    Optional<UserEntity> findByEmail(String email);
    void deleteById(UUID id);

    default Optional<UserEntity> deleteByIdAndReturnEntity(UUID uuid) {
        Optional<UserEntity> user = findById(uuid);

        if (user.isPresent()) {
            deleteById(uuid);
            return user;
        }
        return Optional.empty();
    }
}
