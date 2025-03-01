package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BaseService {

    public UUID checkIfUuidIsNull(UUID uuid) {
        if (uuid == null) {
            throw new UserNotFoundException();
        }

        return uuid;
    }
}
