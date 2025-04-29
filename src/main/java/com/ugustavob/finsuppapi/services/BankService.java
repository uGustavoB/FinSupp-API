package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.banks.BankFilterDTO;
import com.ugustavob.finsuppapi.entities.bank.BankEntity;
import com.ugustavob.finsuppapi.repositories.BankRepository;
import com.ugustavob.finsuppapi.specifications.BankSpecification;
import com.ugustavob.finsuppapi.utils.StringFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {
    private final BankRepository bankRepository;

    public List<BankEntity> findAll(BankFilterDTO filter) {
        Specification<BankEntity> specification = BankSpecification.filter(filter);

        return bankRepository.findAll(specification);
    }

    public BankEntity createBank(String name) {
        BankEntity bank = new BankEntity();

        bank.setName(StringFormatUtil.toTitleCase(name));

        return bankRepository.save(bank);
    }
}
