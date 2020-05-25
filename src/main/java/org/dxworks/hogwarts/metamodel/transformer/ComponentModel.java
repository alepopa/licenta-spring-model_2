package org.dxworks.hogwarts.metamodel.transformer;

import lombok.extern.slf4j.Slf4j;
import org.dxworks.hogwarts.metamodel.*;
import org.dxworks.hogwarts.metamodel.registries.ComponentRegistry;
import org.dxworks.hogwarts.parser.model.ComponentConfig;


import java.util.List;

@Slf4j
public class ComponentModel {

    private ComponentRegistry componentRegistry = new ComponentRegistry();


    public ComponentModel(List<ComponentConfig> config) {
        for (ComponentConfig c: config) {
            c.getComponents().forEach(compModel -> {
            Component component = componentRegistry.getComponentOrPutIfNotExists(compModel.getComponentName());
            component.setFilesList(compModel.getFiles());
            });
        }
    }

    public ComponentRegistry getComponentRegistry() {
        return componentRegistry;
    }
}



