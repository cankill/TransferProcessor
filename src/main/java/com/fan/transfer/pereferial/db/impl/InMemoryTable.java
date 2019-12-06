package com.fan.transfer.pereferial.db.impl;

import com.fan.transfer.domain.HasId;
import com.fan.transfer.domain.IsId;
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
public class InMemoryTable<I extends IsId, T extends HasId<I>> implements Repository<I, T> {
    private static final List<String> EXCLUDE_FIELDS_FOR_UPDATE = List.of("id");
    private ConcurrentHashMap<String, T> table = new ConcurrentHashMap<>();

    private ObjectMapper objectMapper;

    public InMemoryTable () {
        this.objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public T get (I entityId) {
        return entityId == null || entityId.getValue() == null
                ? null
                : table.get(entityId.getValue());
    }

    @Override
    public List<T> getAll (List<I> entityIds) {
        if (entityIds == null || entityIds.isEmpty()) {
            return new LinkedList<>();
        }

        return entityIds.stream()
                .map(this::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getAll () {
        return new LinkedList<>(table.values());
    }

    @Override
    public boolean add (T entity) {
        if (entity == null || entity.getId() == null) {
            return false;
        }

        return table.putIfAbsent(entity.getId().getValue(), entity) == null;
    }

    @Override
    public boolean update (I entityId, T entity) {
        if (entityId == null || entityId.getValue() == null || entity == null) {
            return false;
        }

        ObjectNode nodeNew = objectMapper.valueToTree(entity);
        nodeNew.remove(EXCLUDE_FIELDS_FOR_UPDATE);

        return table.computeIfPresent(entityId.getValue(), (key, currentEntity) -> {
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
    public boolean remove (I entityId) {
        if (entityId == null || entityId.getValue() == null) {
            return false;
        }

        return table.remove(entityId.getValue()) != null;
    }

    @Override
    public void removeAll () {
        table.clear();
    }
}
