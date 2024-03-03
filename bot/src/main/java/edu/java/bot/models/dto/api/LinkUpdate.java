package edu.java.bot.models.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.Setter;

/**
 * LinkUpdate
 */

@Setter
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-02-27T07:34:30.177106031Z[UTC]")
public class LinkUpdate {

    private Long id;

    private URI url;

    private String description;

    @Valid
    private List<Long> tgChatIds;

    @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @Valid
    @Schema(name = "url", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("url")
    public URI getUrl() {
        return url;
    }

    @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @Schema(name = "tgChatIds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("tgChatIds")
    public List<Long> getTgChatIds() {
        return tgChatIds;
    }

}

