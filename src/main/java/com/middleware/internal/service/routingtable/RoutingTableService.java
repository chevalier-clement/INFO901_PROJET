package com.middleware.internal.service.routingtable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.UUID;

public class RoutingTableService {
    // Attributes
    private static RoutingTableService instance;
    private final BiMap<UUID, Integer> routingTable;

    // Constructors
    private RoutingTableService() {
        this.routingTable = Maps.synchronizedBiMap(HashBiMap.create());
    }

    // Getters & Setters
    public static RoutingTableService getInstance() {
        if (instance == null) {
            instance = new RoutingTableService();
        }
        return instance;
    }

    // Methods
    public void register(UUID uuid, int id) {
        this.routingTable.forcePut(uuid, id);
    }

    public void unregister(UUID uuid) {
        this.routingTable.remove(uuid);
    }

    public void update(UUID uuid, int id) {
        this.register(uuid, id);
    }

    public Integer getId(UUID uuid) {
        return this.routingTable.get(uuid);
    }

    public UUID getUUID(int id) {
        return this.routingTable.inverse().get(id);
    }

    public boolean isUUIDRegistered(UUID uuid) {
        return this.routingTable.containsKey(uuid);
    }

    public boolean isIdRegistered(int id) {
        return this.routingTable.containsValue(id);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("RoutingTableService {\n");
        for (UUID uuid : routingTable.keySet()) {
            sb.append("  ")
                    .append(uuid.toString())
                    .append(" -> ")
                    .append(routingTable.get(uuid))
                    .append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
