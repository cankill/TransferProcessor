package com.fan.transfer.pereferial.db.impl;

import com.fan.transfer.domain.HasId;
import com.fan.transfer.domain.Ref;
import com.fan.transfer.pereferial.db.Repository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class InMemoryTable<T extends HasId>  implements Repository<T> {
    private final List<String> EXCLUDE_FIELDS_FOR_UPDATE = List.of("id");
    private ConcurrentHashMap<String, T> table = new ConcurrentHashMap<>();

    private ObjectMapper objectMapper;
    private Class<?> clazz;
    
    public InMemoryTable(Class<?> clazz) {
        this.objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public T get(String entityId) {
        return table.get(entityId);
    }

    @Override
    public List<T> getAllByRefs(List<Ref> entityRefs) {
        if(entityRefs == null || entityRefs.size() == 0) {
            return new LinkedList<>();
        }

        return getAll(entityRefs.stream().map(Ref::getId).collect(Collectors.toList()));
    }

    @Override
    public List<T> getAll(List<String> entityIds) {
        if(entityIds == null || entityIds.size() == 0) {
            return new LinkedList<>();
        }

        return entityIds.stream()
                        .map(this::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
    }

    @Override
    public boolean add(T entity) {
        if(entity == null || entity.getId() == null) {
            return false;
        }

        return table.putIfAbsent(entity.getId(), entity) == null;
    }

    @Override
    public boolean update(String entityId, T entity) {
        if(entityId == null || entity == null) {
            return false;
        }
        
        ObjectNode nodeNew = objectMapper.valueToTree(entity);
        nodeNew.remove(EXCLUDE_FIELDS_FOR_UPDATE);

        return table.computeIfPresent(entityId, (key, currentEntity) -> {
            ObjectReader updater = objectMapper.readerForUpdating(currentEntity);
            try {
                return updater.readValue(nodeNew);
            } catch (IOException ex) {
                log.error("Can't patch an entity '{}' for Id '{}'", entity, entityId, ex);
            }

            return currentEntity;
        }) != null;
    }

    @Override
    public boolean remove(String entityId) {
        if(entityId == null) {
            return false;
        }

        return table.remove(entityId) != null;
    }

    @Override
    public void removeAll() {
        table.clear();
    }
}
