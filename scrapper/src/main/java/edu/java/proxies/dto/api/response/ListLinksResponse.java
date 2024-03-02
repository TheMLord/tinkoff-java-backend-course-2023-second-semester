package edu.java.proxies.dto.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import lombok.Setter;
import java.util.List;

/**
 * ListLinksResponse
 */

@Setter
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

    public ListLinksResponse size(Integer size) {
        this.size = size;
        return this;
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

