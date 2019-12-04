package com.fan.transfer.services;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException (String s) {
        super(s);
    }
}
