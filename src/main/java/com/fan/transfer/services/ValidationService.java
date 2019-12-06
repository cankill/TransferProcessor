package com.fan.transfer.services;

import com.fan.transfer.domain.IsId;

public interface ValidationService {
    boolean validateId (IsId entityId, String error);
}
