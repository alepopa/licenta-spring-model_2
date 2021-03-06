package org.dxworks.hogwarts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewDTO {
    private String componentName;
    private List<TableViewDTO> componentViewDTOS;
}
