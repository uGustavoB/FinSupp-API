package com.ugustavob.finsuppapi.repositories;

import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {
    Optional<AccountEntity> findById(Integer id);
    Optional<AccountEntity> findByUserId(UUID id);

    Optional<AccountEntity> findByDescription(@NotBlank(message = "Account description is required") String description);
    List<AccountEntity> findAllByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    default Optional<AccountEntity> deleteByIdAndReturnEntity(Integer id) {
        Optional<AccountEntity> account = findById(id);

        if (account.isPresent()) {
            deleteById(id);
            return account;
        }

        return Optional.empty();
    }
}
