package com.fan.transfer.services;

import com.fan.transfer.domain.IsId;
import com.fan.transfer.domain.Ref;

public interface ValidationService {
    boolean validateId (IsId entityId, String error);
    boolean validateRef (Ref<? extends IsId> entityId, String error);
}
