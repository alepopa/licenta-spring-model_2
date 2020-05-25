package org.dxworks.hogwarts.controllers;

import org.dxworks.hogwarts.service.RelationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("relation")
public class RelationController {

    @Autowired
    private RelationsService relationsService;

    @GetMapping("aspects")
    public List<String> getQualityAspects(@RequestParam("EntityType") String entityTypeString,
                                          @RequestParam("projectId") String projectId) {
        return relationsService.getRelationTypesForEntity(entityTypeString, projectId);
    }

    @GetMapping("indicators")
    public List<String> getQualityIndicators(@RequestParam("EntityType") String entityTypeString, @RequestParam("AspectId") String relationType, @RequestParam("projectId") String projectId) {
        return relationsService.getRelationsProperty(entityTypeString, relationType, projectId);
    }

    @GetMapping("allIndicators")
    public List<String> getQualityIndicatorsForProject(@RequestParam("projectId") String projectId) {
        return relationsService.getAllQualityIndicatorsForProject(projectId);
    }

//    @GetMapping("value")
//    public List<Double> getQualityIndicatorsValue(@RequestParam("EntityType") String entityTypeString, @RequestParam("AspectId") String relationType, @RequestParam("projectId") String projectId) {
//        return relationsService.getRelationsValue(entityTypeString, relationType, projectId);
//    }
}