package com.fan.transfer.di;

import com.fan.transfer.api.resources.AccountManagementResource;
import com.fan.transfer.domain.User;

public interface AccountManagementResourceFactory {
    AccountManagementResource create(User user);
}
