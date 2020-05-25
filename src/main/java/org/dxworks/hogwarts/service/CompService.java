package org.dxworks.hogwarts.service;

import org.dxworks.hogwarts.dto.DinamicViewDTO;
import org.dxworks.hogwarts.dto.TableViewDTO;
import org.dxworks.hogwarts.dto.ViewDTO;
import org.dxworks.hogwarts.metamodel.Component;
import org.dxworks.hogwarts.metamodel.registries.ComponentRegistry;
import org.dxworks.hogwarts.metamodel.transformer.ComponentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompService {

    @Autowired
    private ComponentManager componentManager;
    @Autowired
    private FileService fileService;
    @Autowired
    private ComponentsRelationsService componentsRelationsService;
    @Autowired
    private RelationsService relationsService;

    public List<String> getAllComponentNamesForComponentSchema(String componentSchemaName) {

        ComponentModel componentModel = componentManager.getComponentModel(componentSchemaName);
        ComponentRegistry componentRegistry = componentModel.getComponentRegistry();

        return componentRegistry.getAllComponents().stream()
                .map(Component::getName)
                .collect(Collectors.toList());
    }

    public DinamicViewDTO getIndicatorsView(String projectId, String file){

        List<String> qualityAspects = new ArrayList<>();
        List<String> qualityIndicators = new ArrayList<>();
        List<Double> qualityValues = new ArrayList<>();
        Map<String, Double> qAValueMap = new HashMap<>();
        List<TableViewDTO> tableViewDTOS = new ArrayList<>();

        qualityAspects.addAll(relationsService.getRelationTypesForEntity(file, projectId));

        for (String et : relationsService.getRelationTypesForEntity(file, projectId)) {
            if (qualityAspects.contains(et)) {
                qualityIndicators.addAll(relationsService.getRelationsProperty(file, et, projectId));
                qualityValues.addAll(relationsService.getRelationsValue(file, et, projectId));
            }
        }

        double sumOfQualityIndicatorsValues = 0;
        for (double qV : qualityValues) {
            sumOfQualityIndicatorsValues += qV;
        }

        List<String> distinctQualityIndicators = qualityIndicators.stream().distinct().collect(Collectors.toList());
        List<String> distinctQualityAspects = qualityAspects.stream().distinct().collect(Collectors.toList());

        tableViewDTOS.add(new TableViewDTO("Total severity score", sumOfQualityIndicatorsValues));
        tableViewDTOS.add(new TableViewDTO("Count anomalies", (double) qualityIndicators.size()));
        tableViewDTOS.add(new TableViewDTO("Anomaly types", (double) distinctQualityIndicators.size()));
        tableViewDTOS.add(new TableViewDTO("Quality Aspects", (double) distinctQualityAspects.size()));

        for(String qa: qualityAspects)
        {
            qAValueMap.put(qa, getValuePerQualityAspectDinamicView(projectId, file, qa));
        }

        for (Map.Entry<String, Double> e : qAValueMap.entrySet()) {
            System.out.println(e.getKey() + e.getValue());
            tableViewDTOS.add(new TableViewDTO(e.getKey(), e.getValue()));
        }
        return DinamicViewDTO.builder().fileName(file).fileViewDTOS(tableViewDTOS).build();
    }

    public ViewDTO getQAFromFile(String projectId, String component, String componentSchemaName) {
        List<String> filesFromProject = fileService.getAllFileNamesForProject(projectId);
        System.out.println(filesFromProject);
        List<String> filesFromComponent = componentsRelationsService.getAllFilesForSpecificComponent(component, componentSchemaName);
        System.out.println(filesFromComponent);
        List<String> qualityAspect = new ArrayList<>();
        List<String> qualityIndicators = new ArrayList<>();
        List<Double> qualityValues = new ArrayList<>();
        List<String> qualityAspectsList = new ArrayList<>();
        Map<String, Double> qAValueMap = new HashMap<>();

        List<TableViewDTO> tableViewDTOS = new ArrayList<>();

        for (String fileFromProject : filesFromProject)

            for (String fileFromComponent : filesFromComponent) {
                if (fileFromProject.equals(fileFromComponent))
                    qualityAspect.addAll(relationsService.getRelationTypesForEntity(fileFromComponent, projectId));
            }

        System.out.println(qualityAspect);

        for (String qa : qualityAspect) {
            qualityAspectsList.add(qa);
        }

        for (String f : filesFromComponent)
            for (String et : relationsService.getRelationTypesForEntity(f, projectId)) {
                if (qualityAspectsList.contains(et)) {
                    qualityIndicators.addAll(relationsService.getRelationsProperty(f, et, projectId));
                    qualityValues.addAll(relationsService.getRelationsValue(f, et, projectId));
                }
            }
        System.out.println(qualityIndicators);
        System.out.println(qualityValues);

        double sumOfQualityIndicatorsValues = 0;
        for (double qV : qualityValues) {
            sumOfQualityIndicatorsValues += qV;
        }

        List<String> distinctQualityIndicators = qualityIndicators.stream().distinct().collect(Collectors.toList());
        List<String> distinctQualityAspects = qualityAspectsList.stream().distinct().collect(Collectors.toList());

        tableViewDTOS.add(new TableViewDTO("Total severity score", sumOfQualityIndicatorsValues));
        tableViewDTOS.add(new TableViewDTO("Files with anomalies", (double) filesFromComponent.size()));
        tableViewDTOS.add(new TableViewDTO("Count anomalies", (double) qualityIndicators.size()));
        tableViewDTOS.add(new TableViewDTO("Anomaly types", (double) distinctQualityIndicators.size()));
        tableViewDTOS.add(new TableViewDTO("Quality Aspects", (double) distinctQualityAspects.size()));

        for(String qa: qualityAspectsList)
        {
            qAValueMap.put(qa, getValuePerQualityAspect(projectId, component, qa, componentSchemaName));
        }

        for (Map.Entry<String, Double> e : qAValueMap.entrySet()) {
            System.out.println(e.getKey() + e.getValue());
            tableViewDTOS.add(new TableViewDTO(e.getKey(), e.getValue()));
        }

//        tableViewDTOS.add(new TableViewDTO("Bugs", getValuePerQualityAspect(projectId, component, "bugs", componentSchemaName)));
//        tableViewDTOS.add(new TableViewDTO("Complexity", getValuePerQualityAspect(projectId, component, "complexity", componentSchemaName)));
//        tableViewDTOS.add(new TableViewDTO("Coupling", getValuePerQualityAspect(projectId, component, "coupling", componentSchemaName)));
//        tableViewDTOS.add(new TableViewDTO("Duplication", getValuePerQualityAspect(projectId, component, "duplication", componentSchemaName)));
//        tableViewDTOS.add(new TableViewDTO("Exceptions", getValuePerQualityAspect(projectId, component, "exceptions", componentSchemaName)));
//        tableViewDTOS.add(new TableViewDTO("Inheritance", getValuePerQualityAspect(projectId, component, "inheritance", componentSchemaName)));
//        tableViewDTOS.add(new TableViewDTO("Instability", getValuePerQualityAspect(projectId, component, "instability", componentSchemaName)));
//        tableViewDTOS.add(new TableViewDTO("Knowledge", getValuePerQualityAspect(projectId, component, "knowledge", componentSchemaName)));
//        tableViewDTOS.add(new TableViewDTO("Portability", getValuePerQualityAspect(projectId, component, "portability", componentSchemaName)));
//        tableViewDTOS.add(new TableViewDTO("Testability", getValuePerQualityAspect(projectId, component, "testability", componentSchemaName)));
//        tableViewDTOS.add(new TableViewDTO("Traceability", getValuePerQualityAspect(projectId, component, "traceability", componentSchemaName)));
//        tableViewDTOS.add(new TableViewDTO("UnitTesting", getValuePerQualityAspect(projectId, component, "UnitTesting", componentSchemaName)));

        return ViewDTO.builder().componentName(component).componentViewDTOS(tableViewDTOS).build();
    }

    public ViewDTO getQIForEachQA(String projectId, String component, String oneQualityAspect, String componentSchemaName) {
        List<String> filesFromProject = fileService.getAllFileNamesForProject(projectId);
        System.out.println(filesFromProject);
        List<String> filesFromComponent = componentsRelationsService.getAllFilesForSpecificComponent(component, componentSchemaName);
        System.out.println(filesFromComponent);
        System.out.println(filesFromComponent.size());

        List<String> filesWithAnomalies = new ArrayList<>();
        List<String> qualityIndicators = new ArrayList<>();
        List<Double> qualityValues = new ArrayList<>();
        List<TableViewDTO> tableViewDTOS = new ArrayList<>();
        Map<String, Double> qIValue = new HashMap<>();
        double sumOfQualityIndicatorsValues = 0;
        double valuePerQI = 0;

        for (String fileFromProject : filesFromProject)

            for (String fileFromComponent : filesFromComponent) {
                if (fileFromProject.equals(fileFromComponent))
                    if ((relationsService.getRelationTypesForEntity(fileFromComponent, projectId)).contains(oneQualityAspect)) {
                        filesWithAnomalies.add(fileFromComponent);
                        qualityIndicators.addAll(relationsService.getRelationsProperty(fileFromComponent, oneQualityAspect, projectId));
                        qualityValues.addAll(relationsService.getRelationsValue(fileFromComponent, oneQualityAspect, projectId));
                    }
            }

        System.out.println(qualityIndicators);
        System.out.println(qualityValues);

        for (double qV : qualityValues) {
            sumOfQualityIndicatorsValues += qV;
        }

        tableViewDTOS.add(new TableViewDTO("All Files", (double) filesFromComponent.size()));
        tableViewDTOS.add(new TableViewDTO("Files with anomalies", (double) filesWithAnomalies.size())); // /avg

        for(String qi: qualityIndicators)
        {
            qIValue.put(qi, getValuePerQualityIndicator(projectId, component, oneQualityAspect, qi, componentSchemaName));
        }

        for (Map.Entry<String, Double> e : qIValue.entrySet()) {
            System.out.println(e.getKey() + e.getValue());
            valuePerQI = e.getValue();
            tableViewDTOS.add(new TableViewDTO(e.getKey(), e.getValue()));
        }

        System.out.println("valuePerQI" + valuePerQI); // val asta trebuie impartita la numarul de qi la fel ---> cum??
        return ViewDTO.builder().componentName(component).componentViewDTOS(tableViewDTOS).build();
    }

    public double getValuePerQualityAspect(String projectId, String component, String qA, String componentSchemaName) {
        List<String> filesFromComponent = componentsRelationsService.getAllFilesForSpecificComponent(component, componentSchemaName);
        List<Double> valuesForQa = new ArrayList<>();
        Map<String, List<Double>> qAValue = new HashMap<>();

        for (String f : filesFromComponent) {
            valuesForQa.addAll(relationsService.getValuesForQA(f, projectId, qA));
            qAValue.put(qA, valuesForQa);
        }
        double sum = 0;
        for (Double x : valuesForQa) {
            sum += x;
        }
        return sum;
    }

    public double getValuePerQualityAspectDinamicView(String projectId, String file, String qA) {
        // List<String> filesFromComponent = componentsRelationsService.getAllFilesForSpecificComponent(component, componentSchemaName);
        List<Double> valuesForQa = new ArrayList<>();
        Map<String, List<Double>> qAValue = new HashMap<>();

       // for (String f : filesFromComponent) {
            valuesForQa.addAll(relationsService.getValuesForQA(file, projectId, qA));
            qAValue.put(qA, valuesForQa);
       // }
        double sum = 0;
        for (Double x : valuesForQa) {
            sum += x;
        }
        return sum;
    }

    public double getValuePerQualityIndicator(String projectId, String component, String qa, String qi, String componentSchemaName) {
        List<String> filesFromComponent = componentsRelationsService.getAllFilesForSpecificComponent(component, componentSchemaName);
        List<Double> valuesForQi = new ArrayList<>();
        Map<String, List<Double>> qAValue = new HashMap<>();

        for (String f : filesFromComponent) {
            for(String qA: relationsService.getRelationTypesForEntity(f, projectId)) {
                if(qA.equals(qa))
                {
                    valuesForQi.addAll(relationsService.getValuesForQI(f, qA, projectId, qi));
                    qAValue.put(qA, valuesForQi);
                }
            }
        }

        System.out.println("cate valori per qi " + valuesForQi.size());

        double sum = 0;
        for (Double x : valuesForQi) {
            sum += x;
        }

        return sum;
    }
}
