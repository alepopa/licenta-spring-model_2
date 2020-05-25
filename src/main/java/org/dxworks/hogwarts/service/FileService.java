package org.dxworks.hogwarts.service;

import org.dxworks.hogwarts.metamodel.FileEntity;
import org.dxworks.hogwarts.metamodel.Relation;
import org.dxworks.hogwarts.metamodel.transformer.ComponentModel;
import org.dxworks.hogwarts.metamodel.transformer.ProjectModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.RelationService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    @Autowired
    private ProjectManager projectManager;

    public List<String> getAllFileNamesForProject(String projectId) {
        ProjectModel projectModel = projectManager.getProjectModel(projectId);
        return projectModel.getFileRegistry().getAllFileEntities().stream()
                .map(FileEntity::getName)
                .collect(Collectors.toList());
    }
}
