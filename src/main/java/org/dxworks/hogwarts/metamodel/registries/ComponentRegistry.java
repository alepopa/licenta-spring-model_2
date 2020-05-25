package org.dxworks.hogwarts.metamodel.registries;

import org.dxworks.hogwarts.exceptions.NoSuchEntityTypeException;
import org.dxworks.hogwarts.metamodel.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ComponentRegistry {
    private Map<String, Component> ComponentMap = new HashMap<>();

    public Component getComponentOrPutIfNotExists(String type) {
        if (ComponentMap.containsKey(type))
            return ComponentMap.get(type);

        Component newComponent = new Component(type);
        ComponentMap.put(type, newComponent);
        return newComponent;
    }

    public Component getComponent(String type) {
        return Optional.ofNullable(ComponentMap.get(type))
                .orElseThrow(() -> new NoSuchEntityTypeException(type));
    }

    public Collection<Component> getAllComponents() {
        return ComponentMap.values();
    }
}
