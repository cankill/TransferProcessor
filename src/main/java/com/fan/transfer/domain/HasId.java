package com.fan.transfer.domain;

public interface HasId<T extends IsId> {
    T getId ();
}
