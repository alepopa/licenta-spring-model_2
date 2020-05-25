package org.dxworks.hogwarts.service;

import org.dxworks.hogwarts.exceptions.NoSuchRelationException;
import org.dxworks.hogwarts.metamodel.FileEntity;
import org.dxworks.hogwarts.metamodel.Relation;
import org.dxworks.hogwarts.metamodel.registries.FileRegistry;
import org.dxworks.hogwarts.metamodel.registries.RelationRegistry;
import org.dxworks.hogwarts.metamodel.transformer.ProjectModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelationsService {

    @Autowired
    private ProjectManager projectManager;
    @Autowired
    private FileService fileService;

    public List<String> getRelationTypesForEntity(String entityTypeString, String projectId) {
        ProjectModel projectModel = projectManager.getProjectModel(projectId);
        RelationRegistry relationRegistry = projectModel.getRelationRegistry();
        FileRegistry fileRegistry = projectModel.getFileRegistry();

        FileEntity fileEntity = fileRegistry.getFileEntity(entityTypeString);
        return relationRegistry.getRelationsForFileEntity(fileEntity).stream()
                .map(Relation::getRelationType)
                .collect(Collectors.toList());
    }

    public List<Relation> getRelationsForEntityTypeAndRelationType(String entityTypeString, String relationType, String projectId) {
        ProjectModel projectModel = projectManager.getProjectModel(projectId);
        RelationRegistry relationRegistry = projectModel.getRelationRegistry();
        FileRegistry fileRegistry = projectModel.getFileRegistry();

        FileEntity fileEntity = fileRegistry.getFileEntity(entityTypeString);
        Relation relation = relationRegistry.getRelationForFileEntityAndCategoryType(fileEntity, relationType)
                .orElseThrow(() -> new NoSuchRelationException(entityTypeString, relationType, projectId));

        return relation.getRelations();
    }

    public List<String> getRelationsProperty(String entityTypeString, String relationType, String projectId) {
        List<String> aux = new ArrayList<>();

        for (Relation relation : getRelationsForEntityTypeAndRelationType(entityTypeString, relationType, projectId)) {
            aux.add(relation.getCategory());
        }
        return aux;
    }

    public List<Double> getRelationsValue(String entityTypeString, String relationType, String projectId) {
        List<Double> aux = new ArrayList<>();

        for (Relation relation : getRelationsForEntityTypeAndRelationType(entityTypeString, relationType, projectId)) {
            aux.add(relation.getValue());
        }
        return aux;
    }

    public List<Double> getValuesForQA(String entityTypeString, String projectId, String qA) {
        List<Double> aux = new ArrayList<>();
        for (String qa : getRelationTypesForEntity(entityTypeString, projectId)) {
            if (qa.equals(qA)) {
                aux.addAll(getRelationsValue(entityTypeString, qA, projectId));
            }
        }
        return aux;
    }

    public List<Double> getValuesForQI(String entityTypeString, String relationType, String projectId, String qualityIndicator) {

        List<Double> aux = new ArrayList<>();
        for (Relation rel : getRelationsForEntityTypeAndRelationType(entityTypeString, relationType, projectId)) {
            if (rel.getCategory().equals(qualityIndicator))
                aux.add(rel.getValue());
        }
        return aux;
    }

    public List<String> getAllQualityIndicatorsForProject(String projectId){

        List<String> qIList = new ArrayList<>();
        for(String file: fileService.getAllFileNamesForProject(projectId)){
            for(String qa: getRelationTypesForEntity(file, projectId))
                    qIList.addAll(getRelationsProperty(file, qa, projectId));
        }
        return qIList;
    }
}
