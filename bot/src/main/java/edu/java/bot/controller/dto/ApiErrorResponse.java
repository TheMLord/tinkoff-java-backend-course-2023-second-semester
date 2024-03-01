package edu.java.bot.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.Value;

/**
 * ApiErrorResponse
 */

@EqualsAndHashCode(callSuper = true) @Value
@Builder
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-02-27T07:36:33.430797072Z[UTC]")
public class ApiErrorResponse extends Throwable {

    String description;

    String code;

    String exceptionName;

    String exceptionMessage;

    @Valid
    List<String> stacktrace;

    @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @Schema(name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @Schema(name = "exceptionName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("exceptionName")
    public String getExceptionName() {
        return exceptionName;
    }

    @Schema(name = "exceptionMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("exceptionMessage")
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    @Schema(name = "stacktrace", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("stacktrace")
    public List<String> getStacktrace() {
        return stacktrace;
    }

}

