package com.fan.transfer.pereferial.db;

import java.util.List;

public interface Repository<I, T> {
    T get (I entityId);

    List<T> getAll (List<I> entityIds);

    List<T> getAll ();

    boolean add (T entity);

    boolean update (I entityId, T account);

    boolean remove (I entityId);

    void removeAll ();

}
