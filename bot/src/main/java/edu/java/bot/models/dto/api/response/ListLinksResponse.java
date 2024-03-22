package edu.java.bot.models.dto.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * ListLinksResponse
 */

@Setter
@AllArgsConstructor
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-02-27T07:36:33.430797072Z[UTC]")
public class ListLinksResponse {

    @Valid
    private List<@Valid LinkResponse> links;

    private Integer size;

    @Schema(name = "links", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("links")
    public List<@Valid LinkResponse> getLinks() {
        return links;
    }

    /**
     * Get size
     *
     * @return size
     */

    @Schema(name = "size", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("size")
    public Integer getSize() {
        return size;
    }

}

