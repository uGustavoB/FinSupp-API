package com.ugustavob.finsuppapi.repositories;

import com.ugustavob.finsuppapi.entities.bank.BankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BankRepository extends JpaRepository<BankEntity, Integer>, JpaSpecificationExecutor<BankEntity> {

    List<BankEntity> findAllByNameContainsIgnoreCase(@Param("name") String name);
}
