package com.fan.transfer.services;

import com.fan.transfer.domain.User;

public interface UserQueryManager {
    User get (User.Id userId);
}
