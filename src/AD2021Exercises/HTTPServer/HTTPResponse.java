package AD2021Exercises.HTTPServer;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * A simple implementation of an object representing an HTTP response message, following RFC 2616.
 * Contains all data and values from the request, accessible through getter methods.
 *
 * @author Olav Valle
 * @version 280221
 */
public class HTTPResponse {

    private final String version;
    private final String statusCode;
    private final String reasonPhrase;
    private final LinkedHashMap<String, String> headFields;
    private final String body;

    /**
     * Returns string representing the HTTP-version used by this response message.
     * @return HTTP-version used by this response.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns string representing the Status-Code for this response message.
     * @return Status-Code for this response.
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * Returns string representing the Reason-Phrase used by this response message.
     * @return Reason-Phrase for this response.
     */
    public String getReasonPhrase() {
        return reasonPhrase;
    }

    /**
     * Returns a map holding the header fields for this response message.
     * Elements are stored in the order they were added to the original map.
     * The uses header-name as keys, and header-value as value.
     * @return map of message header fields.
     */
    public LinkedHashMap<String, String> getHeadFields() {
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, String> headCopy = (LinkedHashMap<String, String>) headFields.clone();
        return headCopy;
    }

    /**
     * Returns string representing the message body of this response message.
     * @return Message body.
     */
    public String getBody() {
        return body;
    }

    private HTTPResponse(Builder builder) {
        this.version = builder.version;
        this.statusCode = builder.statusCode;
        this.reasonPhrase = builder.reasonPhrase;
        this.headFields = builder.headFields;
        this.body = builder.body;
    }

    /**
     * Builder class for HTTPResponse objects.
     */
    public static class Builder {

        // Mandatory fields
        private final String version;
        private final String statusCode;
        private final String reasonPhrase;
        // Optional fields
        private final LinkedHashMap<String, String> headFields = new LinkedHashMap<>();

        private String body = "";

        /**
         * Build an HTTPResponse object.
         * Required parameters are a minimal HTTP response status-line,
         * according to RFC 2616:
         * HTTP-Version SP Status-Code SP Reason-Phrase CRLF
         *
         * @param version      HTTP-Version of response.
         * @param statusCode   Response status code, according to RFC 2616
         * @param reasonPhrase Response Reason-Phrase according to RFC 2616
         */
        public Builder(String version, String statusCode, String reasonPhrase) {
            this.version = version;
            this.statusCode = statusCode;
            this.reasonPhrase = reasonPhrase;
        }

        /**
         * Add a field and value pair to response header.
         *
         * @param field Name of field to add
         * @param value Value of field to add
         * @return This builder object
         */
        public Builder headField(String field, String value) {
            this.headFields.put(field, value);
            return this;
        }

        /**
         * Add a string to the response body.
         * String can be a fully preformatted response body in a single string, or single lines from the body.
         * If single lines are used, correct line endings (CRLF) must be ensured by caller.
         *
         * @param bodyString HTTP response body as String.
         * @return This builder object.
         */
        public Builder body(String bodyString) {
            this.body += bodyString;
            return this;
        }
        /**
         * Builds and returns this HTTP response message object.
         *
         * @return The HTTPResponse object.
         */
        public HTTPResponse build() {
            return new HTTPResponse(this);
        }

    }


}