package com.ugustavob.finsuppapi.repositories;

import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, Integer>, JpaSpecificationExecutor<AccountEntity> {
    Optional<AccountEntity> findById(Integer id);
    Optional<AccountEntity> findByUserId(UUID id);

    @Query("SELECT COUNT (t) > 0 FROM AccountEntity t WHERE t.description = :description AND t.user.id = :userId")
    boolean existsByDescription(@NotBlank(message = "Account description is required") String description,
                                      UUID userId);

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
