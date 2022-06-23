package io.muenchendigital.digiwf.asyncapi.docs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeploymentEvent {
    private String deploymentId;
    private String versionId;
    private String target;
    private String file;
    private String artifactType;
}
