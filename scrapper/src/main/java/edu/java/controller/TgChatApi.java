/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (7.3.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package edu.java.controller;

import edu.java.models.dto.api.response.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Generated;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import static edu.java.controller.advice.ExceptionScrapperControllerAdvice.CHAT_ALREADY_REGISTER_DESCRIPTION;
import static edu.java.controller.advice.ExceptionScrapperControllerAdvice.CHAT_NOT_REGISTER_DESCRIPTION;
import static edu.java.controller.advice.ExceptionScrapperControllerAdvice.SERVER_ERROR_DESCRIPTION;
import static edu.java.controller.advice.ExceptionScrapperControllerAdvice.UNCORRECT_REQUEST_PARAM_DESCRIPTION;
import static edu.java.controller.advice.ExceptionScrapperControllerAdvice.UNSUPPORTED_REQUEST_DESCRIPTION;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-03-02T09:28:12.239297169Z[UTC]")
@Validated
@Tag(name = "tg-chat", description = "the tg-chat API")
public interface TgChatApi {

    /**
     * DELETE /tg-chat/{id} : Удалить чат
     *
     * @param id (required)
     * @return Чат успешно удалён (status code 200)
     *     or Некорректные параметры запроса (status code 400)
     *     or Чат не существует (status code 404)
     */
    @Operation(
        operationId = "tgChatIdDelete",
        summary = "Удалить чат",
        responses = {
            @ApiResponse(responseCode = "200", description = "Чат успешно удалён"),
            @ApiResponse(responseCode = "400", description = UNCORRECT_REQUEST_PARAM_DESCRIPTION, content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = CHAT_NOT_REGISTER_DESCRIPTION, content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "500", description = SERVER_ERROR_DESCRIPTION, content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "502", description = UNSUPPORTED_REQUEST_DESCRIPTION, content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/tg-chat/{id}",
        produces = {"application/json"}
    )
    ResponseEntity<Void> tgChatIdDelete(
        @Parameter(name = "id", description = "", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id
    );

    /**
     * POST /tg-chat/{id} : Зарегистрировать чат
     *
     * @param id (required)
     * @return Чат зарегистрирован (status code 200)
     *     or Некорректные параметры запроса (status code 400)
     */
    @Operation(
        operationId = "tgChatIdPost",
        summary = "Зарегистрировать чат",
        responses = {
            @ApiResponse(responseCode = "200", description = "Чат зарегистрирован"),
            @ApiResponse(responseCode = "400", description = UNCORRECT_REQUEST_PARAM_DESCRIPTION, content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "406", description = CHAT_ALREADY_REGISTER_DESCRIPTION, content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "500", description = SERVER_ERROR_DESCRIPTION, content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            }),
            @ApiResponse(responseCode = "502", description = UNSUPPORTED_REQUEST_DESCRIPTION, content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/tg-chat/{id}",
        produces = {"application/json"}
    )
    ResponseEntity<Void> tgChatIdPost(
        @Parameter(name = "id", description = "", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id
    );
}
