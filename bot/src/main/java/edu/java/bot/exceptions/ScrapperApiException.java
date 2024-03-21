package edu.java.bot.exceptions;

import edu.java.bot.models.dto.api.response.ApiErrorResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Exception that occurs when working with the Scrapper Api.
 */
@RequiredArgsConstructor
@Getter
@Setter
public class ScrapperApiException extends RuntimeException {
    private final ApiErrorResponse apiErrorResponse;
}
