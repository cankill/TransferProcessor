package com.fan.transfer.pereferial.db;

import com.fan.transfer.domain.Ref;

import java.util.List;

public interface Repository<T> {
    T get (String entityId);

    List<T> getAllByRefs(List<Ref> entityRefs);
    List<T> getAll (List<String> entityIds);
    boolean add (T entity);
    boolean update (String entityId, T account);
    boolean remove (String entityId);
    void removeAll();

}
