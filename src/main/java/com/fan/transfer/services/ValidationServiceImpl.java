package com.fan.transfer.services;

import com.fan.transfer.domain.IsId;
import org.apache.commons.lang3.StringUtils;

public class ValidationServiceImpl implements ValidationService {
    @Override
    public boolean validateId (IsId entityId, String error) {
        if(entityId == null || StringUtils.isEmpty(entityId.getValue())) {
            throw new IllegalArgumentException(error);
        }

        return true;
    }
}
