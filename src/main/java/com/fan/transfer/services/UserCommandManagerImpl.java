package com.fan.transfer.services;

import com.fan.transfer.domain.User;
import com.fan.transfer.pereferial.db.Repository;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class UserCommandManagerImpl implements UserCommandManager {
    @Inject
    @Named("userRepository")
    Repository<User.Id, User> userRepository;

    @Override
    public User create (User user) {
        var created = userRepository.add(user);
        return created ? user : null;
    }
}
