package org.dxworks.hogwarts.metamodel.transformer;

import lombok.extern.slf4j.Slf4j;
import org.dxworks.hogwarts.metamodel.*;
import org.dxworks.hogwarts.metamodel.registries.FileRegistry;
import org.dxworks.hogwarts.metamodel.registries.RelationRegistry;
import org.dxworks.hogwarts.parser.model.Config;
import org.dxworks.hogwarts.parser.model.FileModel;

import java.util.List;

@Slf4j
public class ProjectModel {

    private FileRegistry fileRegistry = new FileRegistry();
    private RelationRegistry relationRegistry = new RelationRegistry();

    public ProjectModel(List<Config> config) {
        for (Config c : config) {
            c.getFiles().forEach(fileModel -> {
                try {
                    addRelation(fileModel);
                } catch (RuntimeException e) {
                    log.error("Error creating relation " + fileModel, e);
                }
            });
        }
    }

    private void addRelation(FileModel fileModel) {

        FileEntity fileEntity = fileRegistry.getFileEntityOrPutIfNotExists(fileModel.getFile());
        fileEntity.addFile(new FileEntity(fileModel.getFile()));

        String categoryType = fileModel.getCategory();
        String propertyType = fileModel.getName();
        Double valueType = fileModel.getValue();

        Relation relation = relationRegistry.getRelationForFileEntityAndCategoryType(fileEntity, categoryType)
                .orElseGet(() -> createNewRelation(fileEntity, categoryType, propertyType, valueType));

        relation.addRelation(new Relation(fileEntity, propertyType, categoryType, valueType));

    }

    public FileRegistry getFileRegistry() {
        return fileRegistry;
    }

    public RelationRegistry getRelationRegistry() {
        return relationRegistry;
    }

    private Relation createNewRelation(FileEntity fileEntity, String categoryType, String propertyType, Double valueType) {
        Relation r = new Relation(fileEntity, categoryType, propertyType, valueType);
        relationRegistry.addRelation(r, fileEntity);
        return r;
    }
}