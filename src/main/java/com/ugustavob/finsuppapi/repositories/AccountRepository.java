package com.ugustavob.finsuppapi.repositories;

import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findById(UUID id);
    Optional<AccountEntity> findByUserId(UUID id);

    Optional<AccountEntity> findByDescription(@NotBlank(message = "Account description is required") String description);
    List<AccountEntity> findAllByUserId(UUID userId);

    default Optional<AccountEntity> deleteByIdAndReturnEntity(UUID uuid) {
        Optional<AccountEntity> account = findById(uuid);

        if (account.isPresent()) {
            deleteById(uuid);
            return account;
        }

        return Optional.empty();
    }

}
