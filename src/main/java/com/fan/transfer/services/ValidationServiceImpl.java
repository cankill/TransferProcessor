package com.fan.transfer.services;

import com.fan.transfer.domain.IsId;
import com.fan.transfer.domain.Ref;
import org.apache.commons.lang3.StringUtils;

public class ValidationServiceImpl implements ValidationService {
    @Override
    public boolean validateId (IsId entityId, String error) {
        if(entityId == null || StringUtils.isEmpty(entityId.getValue())) {
            throw new IllegalArgumentException(error);
        }

        return true;
    }

    @Override
    public boolean validateRef (Ref<? extends IsId> ref, String error) {
        if(ref == null || !validateId(ref.getId(), error)) {
            throw new IllegalArgumentException(error);
        }

        return true;
    }
}
