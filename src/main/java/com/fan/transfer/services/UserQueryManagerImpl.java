package com.fan.transfer.services;

import com.fan.transfer.domain.User;
import com.fan.transfer.pereferial.db.Repository;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class UserQueryManagerImpl implements UserQueryManager {
    @Inject
    @Named("userRepository")
    Repository<User.Id, User> userRepository;

    @Override
    public User get (User.Id userId) {
        var user = userRepository.get(userId);
        if(user == null) {
            throw new EntityNotFoundException(String.format("User '%s' was not found", userId.getValue()));
        }

        return user;
    }
}
