package com._sptek._webFrameworkExample.aiExample.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiExampleUserDto {
    @Schema(description = "Sample user id", example = "sample-user")
    @NotBlank(message = "id is required")
    private String id;

    @Schema(description = "Sample user name", example = "Sample User")
    @NotBlank(message = "name is required")
    private String name;

    @Schema(description = "Sample user type", example = "CUSTOMER")
    private UserType type;

    @Schema(hidden = true)
    private String displayName;

    public enum UserType {
        CUSTOMER, MANAGER, ADMIN, ANONYMOUS
    }
}
