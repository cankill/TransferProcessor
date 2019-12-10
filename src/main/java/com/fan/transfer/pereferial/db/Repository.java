package com.fan.transfer.pereferial.db;

import java.util.List;
import java.util.function.Predicate;

/**
 * Interface for InMemory DB for tests purpose, emulates key-value DB
 * @param <I> Type parameter for the keys inside (attempt to support a type safety)
 * @param <T> Type parameter for the values inside DB
 */
public interface Repository<I, T> {
    /**
     * Gets single Entity by it's type safe ID
     * @param entityId Entity identity to be loaded
     * @return Null if entityId is not valid and if DB doesn't contains a value
     * In other cases it returns a found Entity.
     */
    T get (I entityId);

    /**
     * Gets a list of Entities by the list of it's IDs
     * @param entityIds The list of IDs to lookup in DB
     * @return emptyList if entityIds is not valid and if DB doesn't contains any values.
     * In other cases it returns a list of Entities.
     */
    List<T> getAll (List<I> entityIds);

    /**
     * Get a list of Entities by the predicate
     * @param predicateBy The predicate, it is applied on every value in DB,
     *                    should return false to skip an Entity from result set.
     * @return emptyList if predicate is not valid and if DB doesn't contains any values.
     * In other cases it returns a list of Entities.
     */
    List<T> getAllBy (Predicate<T> predicateBy);

    /**
     * Gets a list of all Entities
     * @return emptyList if DB doesn't contains any values.
     * In other cases it returns a list of Entities.
     */
    List<T> getAll ();

    /**
     * Adds new entity to DB
     * @param entity new Entity to add. Entity filed Id should contains unique ID
     * @return True - if entity successfully added.
     * False - if entity or it's ID is invalid, or element with such ID already exists
     */
    boolean add (T entity);

    /**
     * Updates existing entity.
     * @param entityId The identity of the entity to update
     * @param entity The entity patch. Only fields which are not null will be updated on target entity.
     *               Also field ID could not be updated, as it is using as Key for DB.
     * @return True - if entity successfully updated.
     * False - if entityId is not valid, or Entity with such Key is not exists in DB, or patching process failed.
     */
    boolean update (I entityId, T entity);

    /**
     * Updates existing entity.
     * @param entityId The identity of the entity to update
     * @param entity The entity patch. Only fields which are not null will be updated on target entity.
     *               Also field ID could not be updated, as it is using as Key for DB.
     * @param ignoreFields The list of fields to ignore during patch.
     * @return True - if entity successfully updated.
     * False - if entityId is not valid, or Entity with such Key is not exists in DB, or patching process failed.
     */
    boolean update (I entityId, T entity, List<String> ignoreFields);

    /**
     * Removes an entity from DB by it's ID
     * @param entityId The identity of an Entity to delete.
     * @return True - if entityId is valid and DB contains such a key.
     * False - otherwise.
     */
    boolean remove (I entityId);

    /**
     * Removes all Entities from DB
     */
    void removeAll ();

}
