package com.acme.schiff.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

/**
 * Hilfsklasse zur Ermittlung der Basis-URI.
 * Wird z.B. für Location-Header nach POST benötigt.
 * Übernimmt evtl. vorhandenes Forwarding.
 *
 * @author Murat Yahsi
 */
@Component
class UriHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(UriHelper.class);
    private static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    private static final String X_FORWARDED_HOST = "x-forwarded-host";
    private static final String X_FORWARDED_PREFIX = "x-forwarded-prefix";
    private static final String SCHIFFE_PREFIX = "/schiffe";

    /// Standardkonstruktor für den UriHelper.
    UriHelper() {
        // Default constructor for PMD compliance
    }

    /**
     * Ermittelt die Basis-URI unter Berücksichtigung evtl. Forwarding-Header.
     *
     * @param request HTTP-Request
     * @return URI-Objekt
     */
    URI getBaseUri(final HttpServletRequest request) {
        final var forwardedHost = request.getHeader(X_FORWARDED_HOST);
        if (forwardedHost != null) {
            return getBaseUriForwarded(request, forwardedHost);
        }

        final UriComponents uriComponents = ServletUriComponentsBuilder.fromRequestUri(request).build();
        final var baseUri = uriComponents.getScheme() + "://" +
            uriComponents.getHost() + ":" + uriComponents.getPort() + "/api";

        LOGGER.debug("getBaseUri (ohne Forwarding): baseUri={}", baseUri);
        return URI.create(baseUri);
    }

    private URI getBaseUriForwarded(final HttpServletRequest request, final String forwardedHost) {
        final var forwardedProto = request.getHeader(X_FORWARDED_PROTO);
        if (forwardedProto == null) {
            throw new IllegalStateException("Kein 'X-Forwarded-Proto' im Header");
        }

        var forwardedPrefix = request.getHeader(X_FORWARDED_PREFIX);
        if (forwardedPrefix == null) {
            LOGGER.trace("getBaseUriForwarded: Kein '{}' im Header", X_FORWARDED_PREFIX);
            forwardedPrefix = SCHIFFE_PREFIX;
        }

        final var baseUri = forwardedProto + "://" + forwardedHost + forwardedPrefix + "/api";
        LOGGER.debug("getBaseUriForwarded: baseUri={}", baseUri);
        return URI.create(baseUri);
    }
}
