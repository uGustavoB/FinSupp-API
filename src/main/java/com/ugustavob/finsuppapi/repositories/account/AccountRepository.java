package com.ugustavob.finsuppapi.repositories.account;

import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findById(UUID id);

    Optional<AccountEntity> findByDescription(@NotBlank(message = "Account description is required") String description);
    List<AccountEntity> findAllByUserId(UUID userId);
}
