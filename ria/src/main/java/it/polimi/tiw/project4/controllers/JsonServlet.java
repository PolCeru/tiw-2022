package it.polimi.tiw.project4.controllers;

import it.polimi.tiw.project4.utils.JsonHelper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This interfaces provides methods to send a JSON responses
 */
public interface JsonServlet {
    /**
     * Sends back a JSON response.
     *
     * @param response The servlet response object
     * @param json     The encoded JSON response
     */
    default void sendJson(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    /**
     * Sends back a JSON error response.
     *
     * @param response The servlet response object
     * @param status   The HTTP error status code
     * @param error    The error message
     */
    default void sendJsonError(HttpServletResponse response, int status, String error) throws IOException {
        response.setStatus(status);
        sendJson(response, JsonHelper.errorToJson(error));
    }
}
