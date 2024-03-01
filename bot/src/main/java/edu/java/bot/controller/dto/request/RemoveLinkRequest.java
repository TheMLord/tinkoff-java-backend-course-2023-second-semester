package edu.java.bot.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.Setter;

/**
 * RemoveLinkRequest
 */

@Setter
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-02-27T07:36:33.430797072Z[UTC]")
public class RemoveLinkRequest {

    private URI link;

    @Valid
    @Schema(name = "link", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("link")
    public URI getLink() {
        return link;
    }

}

