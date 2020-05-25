package org.dxworks.hogwarts.service;

import org.dxworks.hogwarts.metamodel.Component;
import org.dxworks.hogwarts.metamodel.registries.ComponentRegistry;
import org.dxworks.hogwarts.metamodel.transformer.ComponentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ComponentsRelationsService {

    @Autowired
    private ComponentManager componentManager;
    @Autowired
    private RelationsService relationsService;
    @Autowired
    private CompService compService;
    @Autowired
    private FileService fileService;


    public ArrayList<String> getAllFilesForSpecificComponent(String componentName, String componentSchema)
    {
        ComponentModel componentModel = componentManager.getComponentModel(componentSchema);
        ComponentRegistry componentRegistry = componentModel.getComponentRegistry();
        return componentRegistry.getComponentOrPutIfNotExists(componentName).getFilesList();
    }

    public List<String> getQualityAspectsForComponent(String componentSchemaName, String projectId) {

        List<String> componentsFromComponentSchema = compService.getAllComponentNamesForComponentSchema(componentSchemaName);

        List<String> qAForComponent = new ArrayList<>();

        for(String c: componentsFromComponentSchema){
            for (String fileFromComponent : getAllFilesForSpecificComponent(c, componentSchemaName)){
                qAForComponent.addAll(relationsService.getRelationTypesForEntity(fileFromComponent, projectId));
            }
        }
        List<String> distinctQualityAspects = qAForComponent.stream().distinct().collect(Collectors.toList());

        return distinctQualityAspects;
    }

    public void getExtraFilesNotContainedByComponents(String projectId, String componentSchemaName){

        List<String> allFilesFromComponents = new ArrayList<>();
        List<String> filesFromProject = fileService.getAllFileNamesForProject(projectId);
        List<String> componentsFromComponentSchema = compService.getAllComponentNamesForComponentSchema(componentSchemaName);

        for(String cs: componentsFromComponentSchema)
        {
            for (String fileFromComponent : getAllFilesForSpecificComponent(cs, componentSchemaName)) {
                allFilesFromComponents.add(fileFromComponent);
            }
        }

        ArrayList<String> union = new ArrayList<>(allFilesFromComponents);
        union.addAll(filesFromProject);
        ArrayList<String> intersection = new ArrayList<>(allFilesFromComponents);
        intersection.retainAll(filesFromProject);
        union.removeAll(intersection);

        if (union.size() != 0) {
            ComponentModel componentModel = componentManager.getComponentModel(componentSchemaName);
            Component newComponent = componentModel.getComponentRegistry().getComponentOrPutIfNotExists("@");
            newComponent.setFilesList(union);
        }
    }
}
