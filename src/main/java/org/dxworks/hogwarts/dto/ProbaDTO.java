package org.dxworks.hogwarts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProbaDTO {
    String projectId;
    String qMName;
    String[] qualityIndicators;
    String[] qIAxis;
}
