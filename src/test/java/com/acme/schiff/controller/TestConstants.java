/*
 * Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acme.schiff.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.time.Duration;
import java.util.stream.Collectors;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.ApiVersionInserter;
import static java.nio.charset.StandardCharsets.UTF_8;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
final class TestConstants {
    static final String SCHEMA = "https";
    static final String HOST = "localhost";
    static final String VERSION_1 = "1.0.0";
    static final ApiVersionInserter API_VERSION_INSERTER = ApiVersionInserter.useHeader("X-Version");

    static final ClientHttpRequestFactory REQUEST_FACTORY;

    private static final int READ_TIMEOUT_IN_MILLIS = 10_000;
    private static final long TIMEOUT_IN_SECONDS = 10;

    static {
        final var path = Path.of("src", "main", "resources", "certificate.crt");
        // https://stackoverflow.com/questions/50025086/...
        //              ...in-java-what-is-the-simplest-way-to-create-an-sslcontext-with-just-a-pem-file#answer-61680878
        final SSLContext sslContext;
        try (var certificateStream = Files.lines(path)) {
            final var certificateBytes = certificateStream.collect(Collectors.joining("\n")).getBytes(UTF_8);
            // https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/security/cert/CertificateFactory.html
            final var certificateFactory = CertificateFactory.getInstance("X.509");
            final var certificate = certificateFactory.generateCertificate(new ByteArrayInputStream(certificateBytes));
            final var truststore = KeyStore.getInstance(KeyStore.getDefaultType());
            truststore.load(null, null);
            truststore.setCertificateEntry("microservice", certificate);
            // "X509"
            final var trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(truststore);
            sslContext = SSLContext.getInstance("TLSv1.3");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
        } catch (IOException | CertificateException | KeyStoreException | NoSuchAlgorithmException |
                 KeyManagementException e) {
            throw new IllegalStateException(e);
        }

        final var httpClient = HttpClient.newBuilder()
            .sslContext(sslContext)
            .connectTimeout(Duration.ofSeconds(TIMEOUT_IN_SECONDS))
            .build();
        final var jdkRequestFactory = new JdkClientHttpRequestFactory(httpClient);
        jdkRequestFactory.setReadTimeout(READ_TIMEOUT_IN_MILLIS);
        REQUEST_FACTORY = jdkRequestFactory;
    }

    private TestConstants() {
        // leerer Rumpf
    }
}
