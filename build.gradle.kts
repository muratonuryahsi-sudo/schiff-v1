@file:Suppress(
    "ktlint:standard:comment-spacing",
    "MaxLineLength",
    "StringLiteralDuplication",
    "MissingPackageDeclaration",
    "SpellCheckingInspection",
    "GrazieInspection",
    "LongLine",
)

/*
* Copyright (C) 2016 - present Juergen Zimmermann, Hochschule Karlsruhe
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

//  Aufrufe
//  1) Microservice uebersetzen und starten
//        .\gradlew bootRun [--args='--debug']
//        .\gradlew compileJava
//        .\gradlew compileTestJava
//
//  2) Microservice als selbstausfuehrendes JAR oder Docker-Image erstellen und ausfuehren
//        .\gradlew bootJar
//        java -jar build/libs/....jar
//        .\gradlew bootBuildImage
//        .\gradlew cyclonedxBom
//
//  3) Tests und Codeanalyse
//        .\gradlew test jacocoTestReport [-Dtest=rest-get] [--rerun-tasks]
//        .\gradlew jacocoTestCoverageVerification
//        .\gradlew checkstyleMain checkstyleTest spotbugsMain spotbugsTest pmdMain pmdTest spotlessApply modernizer
//        .\gradlew sonar
//        .\gradlew buildHealth
//        .\gradlew reason --id com.fasterxml.jackson.core:jackson-annotations:...
//
//  4) Sicherheitsueberpruefung durch OWASP Dependency Check
//        .\gradlew dependencyCheckAnalyze --info
//
//  5) "Dependencies Updates"
//        .\gradlew versions
//        .\gradlew dependencyUpdates --no-parallel
//
//  6) API-Dokumentation erstellen
//        .\gradlew javadoc
//
//  7) Projekthandbuch erstellen
//        .\gradlew asciidoctor asciidoctorPdf
//
//  8) Projektreport erstellen
//        .\gradlew projectReport
//        .\gradlew propertyReport htmlDependencyReport
//        .\gradlew dependencyInsight --dependency jakarta.persistence-api
//        .\gradlew dependencies
//        .\gradlew dependencies --configuration runtimeClasspath
//        .\gradlew buildEnvironment
//        .\gradlew htmlDependencyReport
//        .\gradlew bootBuildImage --task-graph
//
//  9) Report ueber die Lizenzen der eingesetzten Fremdsoftware
//        .\gradlew generateLicenseReport
//
//  10) Daemon stoppen
//        .\gradlew --stop
//
//  11) Verfuegbare Tasks auflisten
//        .\gradlew tasks
//
//  12) "Dependency Verification"
//        .\gradlew --write-verification-metadata pgp,sha256 --export-keys
//
//  13) Native Compilation mit Spring AOT (= Ahead Of Time) in einer Eingabeaufforderung
//        "C:\Program Files (x86)\Microsoft Visual Studio\2022\BuildTools\VC\Auxiliary\Build\vcvars64.bat"
//        .\gradlew nativeCompile
//        .\build\native\nativeCompile\schiff.exe --spring.profiles.active=dev,native --logging.file.name=C:\temp\application.log
//
//  14) Initialisierung des Gradle Wrappers in der richtigen Version
//      dazu ist ggf. eine Internetverbindung erforderlich
//      https://docs.gradle.org/current/userguide/gradle_wrapper.html#sec:adding_wrapper
//        gradle wrapper --gradle-version 9.4.0-milestone-2
//        gradle wrapper --gradle-version latest

// https://github.com/gradle/kotlin-dsl/tree/master/samples
// https://docs.gradle.org/current/userguide/kotlin_dsl.html
// https://docs.gradle.org/current/userguide/task_configuration_avoidance.html
// https://guides.gradle.org/migrating-build-logic-from-groovy-to-kotlin

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.SpotBugsTask
import net.ltgt.gradle.errorprone.errorprone
import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.asciidoctor.gradle.jvm.pdf.AsciidoctorPdfTask
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun
import java.nio.file.Path

// alternativ:   project.findProperty("...")
val javaLanguageVersion: String = project.properties["javaLanguageVersion"] as? String? ?: JavaVersion.VERSION_25.majorVersion
val javaLanguageVersionBuildpacks: String =
    project.properties["javaLanguageVersionBuildpacks"] as? String? ?: JavaVersion.VERSION_25.majorVersion
val javaLanguageVersionJavadoc: String =
    project.properties["javaLanguageVersionJavadoc"] as? String? ?: JavaVersion.VERSION_25.majorVersion
val javaLanguageVersionSonar: String =
    project.properties["javaLanguageVersionSonar"] as? String? ?: JavaVersion.VERSION_25.majorVersion
val enablePreview =
    if (project.properties["enablePreview"] == "true" || project.properties["enablePreview"] == "TRUE") {
        "--enable-preview"
    } else {
        null
    }

val usePersistence = project.properties["persistence"] != "false" && project.properties["persistence"] != "FALSE"
val useMail = project.properties["mail"] != "false" && project.properties["mail"] != "FALSE"
val useSecurity = project.properties["security"] != "false" && project.properties["security"] != "FALSE"
val useObservability = project.properties["observability"] != "false" && project.properties["observability"] != "FALSE"
val useGraphQL = project.properties["graphql"] != "false" && project.properties["graphql"] != "FALSE"
val useWebclient = project.properties["webclient"] != "false" && project.properties["webclient"] != "FALSE"

val imagePath = project.properties["imagePath"] ?: "juergenzimmermann"
val paketoBuilder = project.properties["paketoBuilder"] as? String?
val paketoRunImage = project.properties["paketoRunImage"] as? String?
val alternativeBuildpack = project.properties["buildpack"]
val nativeImage = project.properties["nativeImage"] == "true"

val mapStructVerbose =
    project.properties["mapStructVerbose"] == "true" || project.properties["mapStructVerbose"] == "TRUE"
val useDevTools = project.properties["devTools"] != "false" && project.properties["devTools"] != "FALSE"
val activeProfiles = project.properties["activeProfiles"] ?: ""

plugins {
    java
    idea
    checkstyle
    pmd
    jacoco
    `project-report`

    // https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle
    // https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#reacting-to-other-plugins.java
    // https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#packaging-executable
    // https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#build-image
    alias(libs.plugins.spring.boot)

    // Spring AOT: Kommentar entfernen
    //alias(libs.plugins.graalvm)

    // https://github.com/tbroyer/gradle-errorprone-plugin
    // https://errorprone.info/docs/installation
    // https://github.com/spring-gradle-plugins/nullability-plugin
    alias(libs.plugins.errorprone)

    // https://cyclonedx.org
    // https://github.com/CycloneDX/cyclonedx-gradle-plugin
    // https://localhost:8443/actuator/sbom
    // gradle cyclonedxBom   -> build\reports\application.cdx.json
    alias(libs.plugins.cyclonedx)

    // https://spotbugs.readthedocs.io/en/latest/gradle.html
    alias(libs.plugins.spotbugs)

    // https://github.com/diffplug/spotless
    alias(libs.plugins.spotless)

    // https://github.com/andygoossens/gradle-modernizer-plugin
    alias(libs.plugins.modernizer)

    // https://docs.sonarqube.org/latest/analyzing-source-code/scanners/sonarscanner-for-gradle
    alias(libs.plugins.sonarqube)

    // https://github.com/radarsh/gradle-test-logger-plugin
    alias(libs.plugins.test.logger)

    // https://github.com/boxheed/gradle-sweeney-plugin
    alias(libs.plugins.sweeney)

    // https://github.com/jeremylong/dependency-check-gradle
    alias(libs.plugins.dependencycheck)

    // TODO Gradle-Plugin 5.x: https://docs.asciidoctor.org/gradle-plugin/5.0
    //alias(libs.plugins.asciidoctorj)
    //alias(libs.plugins.asciidoctorDiagram)
    //alias(libs.plugins.asciidoctorPdf)
    alias(libs.plugins.asciidoctor.convert)
    alias(libs.plugins.asciidoctor.pdf)

    // https://github.com/nwillc/vplugin
    // Aufruf: gradle versions
    alias(libs.plugins.nwillc.vplugin)

    // https://github.com/ben-manes/gradle-versions-plugin
    // Aufruf: gradle dependencyUpdates
    alias(libs.plugins.ben.manes.versions)

    // https://github.com/jk1/Gradle-License-Report
    alias(libs.plugins.license.report)

    // https://github.com/gradle-dependency-analyze/gradle-dependency-analyze
    // https://github.com/jaredsburrows/gradle-license-plugin
    // https://github.com/hierynomus/license-gradle-plugin
}

defaultTasks = mutableListOf("compileTestJava")
group = "com.acme"

sweeney {
    enforce(mapOf("type" to "gradle", "expect" to "[9.4.0,10.0.0)"))
    enforce(mapOf("type" to "jdk", "expect" to "[25.0.1,26.0.0)"))
    validate()
}

repositories {
    mavenCentral()

    // https://github.com/spring-projects/spring-framework/wiki/Spring-repository-FAQ
    // https://github.com/spring-projects/spring-framework/wiki/Release-Process
    //maven("https://repo.spring.io/milestone")

    // Snapshots von Spring Framework, Spring Boot, Spring Data, Spring Security, Spring for GraphQL, ...
    //maven("https://repo.spring.io/snapshot") { mavenContent { snapshotsOnly() } }

    // Snapshots von Hibernate
    //maven("https://oss.sonatype.org/content/repositories/snapshots") { mavenContent { snapshotsOnly() } }

    // Snapshots von springdoc-openapi
    //maven("https://oss.sonatype.org/content/repositories/snapshots") { mavenContent { snapshotsOnly() } }

    // Snapshots von JaCoCo https://www.jacoco.org/jacoco/trunk/doc/repo.html
    //maven("https://central.sonatype.com/repository/maven-snapshots") {
    //    mavenContent {
    //        content {
    //            onlyForConfigurations("jacocoAgent", "jacocoAnt")
    //            excludeGroup("com.github.spotbugs")
    //            excludeGroup("com.google.guava")
    //            excludeGroup("com.uber.nullaway")
    //            excludeGroup("org.hibernate.orm")
    //            excludeGroup("org.junit.platform")
    //            excludeGroup("org.junit")
    //            excludeGroup("org.mapstruct")
    //        }
    //    }
    //}
}

// aktuelle Snapshots laden
//configurations.all {
//    resolutionStrategy { cacheChangingModulesFor(0, "seconds") }
//}

//configurations.all {
//    resolutionStrategy {
//        force(
//            "org.asciidoctor:asciidoctorj-pdf:${libs.versions.asciidoctorjPdf.get()}",
//            "org.jruby:jruby-complete:${libs.versions.jruby.get()}"
//        )
//    }
//}

// https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_separation
dependencies {
    //implementation(platform(libs.slf4j.bom))
    //implementation(platform(libs.log4j.bom))
    if (useObservability) {
        //implementation(platform(libs.zipkin.reporter.bom))
        implementation(platform(libs.opentelemetry.bom))
        //implementation(platform(libs.micrometer.bom))
        //implementation(platform(libs.micrometer.tracing.bom))
        //implementation(platform(libs.prometheus.metrics.bom))
    }
    //implementation(platform(libs.jackson.bom))
    //implementation(platform(libs.netty.bom))
    //implementation(platform(libs.reactor.bom))
    //implementation(platform(libs.spring.framework.bom))
    if (usePersistence) {
        implementation(platform(libs.hibernate.platform))
        //implementation(platform(libs.spring.data.bom))
    }
    //if (useSecurity) {
    //    implementation(platform(libs.spring.security.bom))
    //}

    //implementation(platform(libs.logback.parent))

    testImplementation(platform(libs.assertj.bom))
    //testImplementation(platform(libs.mockito.bom))
    testImplementation(platform(libs.junit.bom))

    implementation(platform(libs.spring.boot.parent))
    // spring-boot-starter-parent als "Parent POM"
    implementation(platform(libs.springdoc.openapi))

    // https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#tooling-modelgen
    // https://docs.jboss.org/hibernate/orm/current/introduction/html_single/Hibernate_Introduction.html#generator
    // build\generated\sources\annotationProcessor\java\main\com.acme.schiff\entity\chiff_.java
    annotationProcessor(libs.hibernate.processor)
    annotationProcessor(libs.mapstruct.processor)
    annotationProcessor(libs.spring.boot.configurationProcessor)

    // "Starters" enthalten sinnvolle Abhaengigkeiten, die man i.a. benoetigt
    // https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide#starters
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    if (usePersistence) {
        println("Persistence            a k t i v i e r t")
        // fuer "Jakarta Transaction"
        implementation(libs.cdi)
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        runtimeOnly("org.postgresql:postgresql")

        if (!nativeImage) {
            implementation("org.springframework.boot:spring-boot-starter-flyway")

            // https://documentation.red-gate.com/flyway/learn-more-about-flyway/system-requirements/supported-databases-for-flyway
            // https://documentation.red-gate.com/fd/postgresql-184127604.html
            // https://github.com/flyway/flyway/blob/main/flyway-core/src/main/java/org/flywaydb/core/internal/database/DatabaseTypeRegister.java#L99
            runtimeOnly("org.flywaydb:flyway-database-postgresql")
        }
    } else {
        println("Persistence            d e a k t i v i e r t")
    }

    if (useMail) {
        println("Mail                   a k t i v i e r t")
        implementation("org.springframework.boot:spring-boot-starter-mail")
    } else {
        println("Mail                   d e a k t i v i e r t")
    }

    if (useSecurity) {
        println("Security               a k t i v i e r t")
        implementation("org.springframework.boot:spring-boot-starter-security")
        implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
        implementation(libs.spring.addons.starter.oidc)
        // RestClient und RestClient.Builder fuer z.B. KeycloakRepository oder HTTP-Interface fuer anderen Microservice
        implementation("org.springframework.boot:spring-boot-starter-restclient")
        // Passwort-Verschluesselung mit bcrypt, Argon2 oder PBKDF2:
        // implementation("org.springframework.security:spring-security-crypto")
    } else {
        println("Security               d e a k t i v i e r t")
    }

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.jspecify:jspecify")

    if (useObservability) {
        println("Tracing und Metriken   a k t i v i e r t")

        // "Micrometer" ist eine Fassade fuer Metriken-Daten (vgl.: SLF4J fuer Logging)
        // "OpenTelemetry (OTel)" ist ein Projekt von CNCF (Cloud Native Computing Foundation) fuer Tracing, Metriken und Log
        // zzgl. APIs, SDKs, und Netzwerk-Protokolle fuer die (Telemetrie-) Daten
        // "OTLP (OpenTelemetry Protocol)" ist das Netzwerk-Protokoll von OpenTelemetry fuer den Export von Daten für Tracing, Metriken und Logs

        // Micrometer mit OpenTelemetry zzgl. Prometheus fuer Metriken und Zipkin fuer Tracing
        // https://docs.spring.io/spring-boot/reference/actuator/tracing.html#actuator.micrometer-tracing.tracer-implementations.otel-zipkin
        // siehe auch org.springframework.boot:spring-boot-starter-opentelemetry
        implementation("org.springframework.boot:spring-boot-micrometer-metrics")
        implementation("org.springframework.boot:spring-boot-micrometer-tracing-opentelemetry")
        implementation("org.springframework.boot:spring-boot-opentelemetry")

        // Visualisierung der Metriken durch Prometheus/Grafana
        // https://docs.spring.io/spring-boot/reference/actuator/endpoints.html
        // https://docs.micrometer.io/micrometer/reference/implementations/prometheus.html#_gradle
        implementation("io.micrometer:micrometer-registry-prometheus")

        // https://docs.spring.io/spring-boot/reference/actuator/tracing.html
        implementation("org.springframework.boot:spring-boot-starter-zipkin")
        implementation("io.opentelemetry:opentelemetry-exporter-zipkin")

        // alternativ: OpenZipkin Brave mit Zipkin
        // https://docs.spring.io/spring-boot/reference/actuator/tracing.html#actuator.micrometer-tracing.tracer-implementations.brave-zipkin
        //implementation("io.micrometer:micrometer-tracing-bridge-brave")
        //implementation("io.zipkin.reporter2:zipkin-reporter-brave")

        // "Jaeger" statt "Zipkin: https://blog.devgenius.io/implementing-distributed-tracing-with-opentelemetry-and-spring-boot-3-2ccf66f305ab
    } else {
        println("Tracing und Metriken   d e a k t i v i e r t")
    }

    if (useGraphQL) {
        println("GraphQL                a k t i v i e r t")
        implementation("org.springframework.boot:spring-boot-starter-graphql")
    } else {
        println("GraphQL                d e a k t i v i e r t")
    }

    if (useWebclient) {
        println("Webclient              a k t i v i e r t")
        implementation("org.springframework.boot:spring-boot-starter-webclient")
    } else {
        println("Webclient              d e a k t i v i e r t")
    }

    println("")

    implementation(libs.mapstruct)

    // https://springdoc.org/v2/#swagger-ui-configuration
    // https://github.com/springdoc/springdoc-openapi
    // https://github.com/springdoc/springdoc-openapi-demos/wiki/springdoc-openapi-2.x-migration-guide
    // https://www.baeldung.com/spring-rest-openapi-documentation
    implementation(libs.springdoc.openapi.ui)

    runtimeOnly(libs.jansi)

    compileOnly(libs.spotbugs.annotations)
    testCompileOnly(libs.spotbugs.annotations)

    // https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-devtools
    if (useDevTools) {
        developmentOnly(libs.spring.boot.devtools)
    }

    // einschl. JUnit und Mockito
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test") {
        exclude(group = "org.hamcrest", module = "hamcrest")
        exclude(group = "org.skyscreamer", module = "jsonassert")
        exclude(group = "org.xmlunit", module = "xmlunit-core")
        exclude(group = "org.awaitility", module = "awaitility")
    }
    testImplementation(libs.junit.platform.suite.api)
    testRuntimeOnly(libs.junit.platform.suite.engine)
    testRuntimeOnly(libs.junit.platform.reporting)
    //testImplementation("org.springframework.security:spring-security-test")
    // mock() fuer record
    testImplementation(libs.mockito.inline)
    testImplementation("org.springframework.boot:spring-boot-starter-restclient")
    testImplementation(libs.modernizer)

    // https://github.com/tbroyer/gradle-errorprone-plugin
    // https://docs.gradle.org/8.4/release-notes.html#easier-to-create-role-focused-configurations
    errorprone(libs.guava)
    errorprone(libs.nullaway)
    errorprone(libs.errorprone)

    constraints {
        //implementation(libs.commons.lang3)
        //implementation(libs.bundles.tomcat)
        //implementation(libs.json.smart)
        //implementation(libs.jspecify)
        implementation(libs.jakarta.validation)
        //implementation(libs.hibernate.validator)

        if (useMail) {
            implementation(libs.angus.mail)
        }

        if (usePersistence) {
            //runtimeOnly(libs.postgresql)
            //implementation(libs.jakarta.persistence)
            //implementation(libs.hikaricp)
            if (!nativeImage) {
                implementation(libs.flyway)
                runtimeOnly(libs.flyway.postgresql)
            }
        }

        //if (useGraphQL) {
        //    implementation(libs.graphql.java.dataloader)
        //    implementation(libs.graphql.java)
        //    implementation(libs.spring.graphql)
        //}

        //implementation(libs.snakeyaml)
    }
}

// https://docs.gradle.org/current/userguide/java_plugin.html#sec:java-extension
// https://docs.gradle.org/current/dsl/org.gradle.api.plugins.JavaPluginExtension.html
java {
    // https://docs.gradle.org/current/userguide/toolchains.html : gradle -q javaToolchains
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaLanguageVersion)
        nativeImageCapable = nativeImage
        val javaInstallationEnv = project.properties["org.gradle.java.installations.fromEnv"] as? String?
        val javaInstallation = if (javaInstallationEnv == null) {
            "siehe JAVA_HOME"
        } else {
            System.getenv(javaInstallationEnv)
        }

        println("")
        println("Toolchain                $javaLanguageVersion")
        println("Java fuer Toolchain      $javaInstallation")
        println("JAVA_HOME                ${System.getenv("JAVA_HOME")}")
        println("Native image (GraalVM)   $nativeImage")
        println("")
    }
}

tasks.withType<JavaCompile>().configureEach {
    with(options) {
        isDeprecation = true
        release = JavaLanguageVersion.of(javaLanguageVersion).asInt()
        with(compilerArgs) {
            if (enablePreview != null) {
                add(enablePreview)
            }
            // javac --help-lint
            add("-Xlint:all,-serial,-processing,-preview")
        }

        with(errorprone.errorproneArgs) {
            // https://github.com/uber/NullAway/wiki/Configuration
            add("-XepOpt:NullAway:OnlyNullMarked=true")
            add("-XepOpt:NullAway:JSpecifyMode=true")
        }
    }
}

tasks.named<JavaCompile>("compileJava") {
    // https://docs.gradle.org/current/dsl/org.gradle.api.tasks.compile.JavaCompile.html
    // https://docs.gradle.org/current/dsl/org.gradle.api.tasks.compile.CompileOptions.html
    // https://dzone.com/articles/gradle-goodness-enabling-preview-features-for-java
    with(options) {
        with(compilerArgs) {
            // https://github.com/tbroyer/gradle-errorprone-plugin#jdk-16-support
            add("--add-opens")
            add("--add-exports")

            // https://mapstruct.org/documentation/stable/reference/html/#configuration-options
            if (mapStructVerbose) {
                add("-Amapstruct.verbose=true")
            }
            //add("-Amapstruct.unmappedTargetPolicy=ERROR")
            //add("-Amapstruct.unmappedSourcePolicy=ERROR")
        }

        // https://uber.github.io/AutoDispose/error-prone
        // https://errorprone.info/docs/flags
        // https://stackoverflow.com/questions/56975581/how-to-setup-error-prone-with-gradle-getting-various-errors
        errorprone {
            errorproneArgs.add("-Xep:MissingSummary:OFF")
            // https://stackoverflow.com/questions/39561334/how-do-i-make-error-prone-ignore-my-generated-source-code#answer-76649506
            disableWarningsInGeneratedCode = true // fuer @Generated bei z.B. MapStruct
            excludedPaths = ".*/build/generated/.*" // fuer sonstigen generierten Code
            error("NullAway")
        }

        // ohne sourceCompatiblity und targetCompatibility:
        //release = javaLanguageVersion
    }

    // https://blog.gradle.org/incremental-compiler-avoidance#about-annotation-processors
}

tasks.named<JavaCompile>("compileTestJava") {
    // sourceCompatibility = javaLanguageVersion
    with(options.errorprone.errorproneArgs) {
        add("-Xep:VariableNameSameAsType:OFF")
        add("-XepOpt:NullAway:HandleTestAssertionLibraries=true")
    }
}

tasks.named("bootJar", BootJar::class.java) {
    // in src/main/resources/
    exclude("private-key.pem", "certificate.crt", ".reloadtrigger")

    doLast {
        // CDS = Class Data Sharing seit Spring Boot 3.3.0
        // https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.3.0-M3-Release-Notes#cds-support
        // https://docs.spring.io/spring-framework/reference/integration/cds.html
        println(
            """
                |
                |Aufruf der ausfuehrbaren JAR-Datei ohne CDS:
                |java --enable-preview -jar build/libs/${project.name}-${project.version}.jar `
                |     --spring.profiles.active=dev `
                |     --spring.ssl.bundle.pem.microservice.keystore.private-key=./src/main/resources/private-key.pem `
                |     --spring.ssl.bundle.pem.microservice.keystore.certificate=./src/main/resources/certificate.crt `
                |     --spring.ssl.bundle.pem.microservice.truststore.certificate=./src/main/resources/certificate.crt [--debug]
                |
                |Aufruf der ausfuehrbaren JAR-Datei mit CDS (= Class Data Sharing):
                |java -Djarmode=tools -jar build/libs/${project.name}-${project.version}.jar extract
                |java -Djarmode=tools --enable-preview -jar ${project.name}-${project.version}/${project.name}-${project.version}.jar `
                |     --spring.profiles.active=dev `
                |     --spring.ssl.bundle.pem.microservice.keystore.private-key=./src/main/resources/private-key.pem `
                |     --spring.ssl.bundle.pem.microservice.keystore.certificate=./src/main/resources/certificate.crt `
                |     --spring.ssl.bundle.pem.microservice.truststore.certificate=./src/main/resources/certificate.crt [--debug]
            """.trimMargin("|"),
        )
    }
}

// https://github.com/paketo-buildpacks/spring-boot
tasks.named("bootBuildImage", BootBuildImage::class.java) {
    // https://github.com/spring-projects/spring-boot/issues/41199#issuecomment-2183338408
    //docker {
    //    host = "//./pipe/dockerDesktopLinuxEngine"
    //}

    // statt "created xx years ago": https://medium.com/buildpacks/time-travel-with-pack-e0efd8bf05db
    createdDate = "now"

    // default:   imageName = "docker.io/${project.name}:${project.version}"
    imageName = "$imagePath/${project.name}:${project.version}-buildpacks"

    // default: "paketobuildpacks/builder-noble-java-tiny:latest" OHNE Shell
    // https://github.com/paketo-buildpacks/builder-noble-java-tiny
    // https://docs.spring.io/spring-boot/gradle-plugin/packaging-oci-image.html#build-image.customization
    // https://stackoverflow.com/questions/68023763/spring-boot-buildpack-always-downloads-latest-packeto-images-from-git#answer-68039094
    if (paketoBuilder != null) {
        builder = paketoBuilder
    }

    // paketobuildpacks/builder-noble-java-tiny:latest enthaelt keine Shell
    // https://docs.spring.io/spring-boot/gradle-plugin/packaging-oci-image.html#build-image.examples.custom-image-builder
    if (paketoRunImage != null) {
        runImage = paketoRunImage
    }

    // verboseLogging = true
    // cleanCache = true

    @Suppress("UNCHECKED_CAST")
    val environmentVars = mapOf(
        // https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#build-image.examples.builder-configuration
        // https://github.com/paketo-buildpacks/bellsoft-liberica#configuration
        // https://github.com/paketo-buildpacks/bellsoft-liberica/blob/main/buildpack.toml: Umgebungsvariable und Ubuntu Jammy
        // https://releases.ubuntu.com: Noble Numbat = 24.04, Jammy Jellyfish = 22.04
        // https://github.com/paketo-buildpacks/bellsoft-liberica/releases
        "BP_JVM_VERSION" to javaLanguageVersionBuildpacks, // default: 21
        // BPL = Build Packs Launch
        "BPL_JVM_THREAD_COUNT" to "20", // default: 250
        // https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#build-image.examples.runtime-jvm-configuration
        "BPE_DELIM_JAVA_TOOL_OPTIONS" to " ",
        "BPE_APPEND_JAVA_TOOL_OPTIONS" to enablePreview,

        // https://github.com/paketo-buildpacks/spring-boot#configuration
        // https://github.com/paketo-buildpacks/spring-boot/blob/main/buildpack.toml
        "BP_SPRING_CLOUD_BINDINGS_DISABLED" to "true",
        "BPL_SPRING_CLOUD_BINDINGS_DISABLED" to "true",
        "BPL_SPRING_CLOUD_BINDINGS_ENABLED" to "false", // deprecated
        // https://paketo.io/docs/howto/configuration/#enabling-debug-logging
        //"BP_LOG_LEVEL" to "DEBUG",

        // https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#build-image.examples.publish
    ) as Map<String, String>

    @Suppress("ktlint:standard:no-blank-line-in-list")
    environment.set(environmentVars.toMutableMap())

    // https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#reacting-to-other-plugins.nbt
    // https://docs.spring.io/spring-boot/docs/current/reference/html/native-image.html
    // https://github.com/spring-projects/spring-framework/blob/main/framework-docs/src/docs/asciidoc/core/core-aot.adoc
    if (nativeImage) {
        environment
            .get()
            .putAll(
                mapOf(
                    "BP_NATIVE_IMAGE" to "true",
                    "BP_BOOT_NATIVE_IMAGE_BUILD_ARGUMENTS" to "-H:+ReportExceptionStackTraces",
                ),
            )
    }

    // https://paketo.io/docs/howto/java/#use-an-alternative-jvm
    when (alternativeBuildpack) {
        "azul-zulu" -> {
            // Azul Zulu: JRE 8, 11, 17, 21 (default, siehe buildpack.toml: BP_JVM_VERSION), 25
            // https://github.com/paketo-buildpacks/azul-zulu/releases
            buildpacks =
                listOf(
                    //"paketo-buildpacks/ca-certificates",
                    "paketobuildpacks/azul-zulu",
                    "paketobuildpacks/java",
                )

            imageName = "${imageName.get()}-azul"
            println("")
            println("Buildpacks: JRE durch   A z u l   Z u l u")
            println("")
        }
        "adoptium" -> {
            // Eclipse Temurin: JRE 8, 11, 17, 21 (default, siehe buildpack.toml: BP_JVM_VERSION), 25
            // https://github.com/paketo-buildpacks/adoptium/releases
            buildpacks =
                listOf(
                    "paketobuildpacks/adoptium",
                    "paketobuildpacks/java",
                )
            imageName = "${imageName.get()}-eclipse"
            println("")
            println("Buildpacks: JRE durch   E c l i p s e   T e m u r i n")
            println("")
        }
        "sap-machine" -> {
            // SapMachine: JRE 11, 17, 21 (default, siehe buildpack.toml: BP_JVM_VERSION), 25
            // https://github.com/paketo-buildpacks/sap-machine/releases
            buildpacks =
                listOf(
                    "paketobuildpacks/sap-machine",
                    "paketobuildpacks/java",
                )
            imageName = "${imageName.get()}-sapmachine"
            println("")
            println("Buildpacks: JRE durch   S a p M a c h i n e")
            println("")
        }
        else -> {
            // Bellsoft Liberica: JRE 8, 11, 17, 21 (default, siehe buildpack.toml: BP_JVM_VERSION), 25
            // https://github.com/paketo-buildpacks/bellsoft-liberica/releases
            // https://github.com/paketo-buildpacks/java/releases
            // https://docs.spring.io/spring-boot/gradle-plugin/packaging-oci-image.html#build-image.customization
            // https://stackoverflow.com/questions/68023763/spring-boot-buildpack-always-downloads-latest-packeto-images-from-git#answer-68039094

            //buildpacks =
            //    listOf(
            //        "paketobuildpacks/bellsoft-liberica",
            //        "paketobuildpacks/java",
            //    )
            imageName = "${imageName.get()}-bellsoft"

            println("")
            println("Buildpacks: JRE durch   B e l l s o f t   L i b e r i c a   (default)")
            println("")

            // *kein* JRE, nur JDK:
            // Amazon Coretto     https://github.com/paketo-buildpacks/amazon-corretto/releases
            // Oracle             https://github.com/paketo-buildpacks/oracle/releases
            // Microsoft OpenJDK  https://github.com/paketo-buildpacks/microsoft-openjdk/releases
            // Alibaba Dragonwell https://github.com/paketo-buildpacks/alibaba-dragonwell/releases
            // *nur* LTS:
            // IBM Semeru         https://github.com/paketo-buildpacks/eclipse-openj9/releases
        }
    }

    // Podman statt Docker
    // docker {
    //    host = "unix:///run/user/1000/podman/podman.sock"
    //    isBindHostToBuilder = true
    // }
}

//tasks.named("processAot", org.springframework.boot.gradle.tasks.aot.ProcessAot::class.java) {
//    jvmArgs = listOf(enablePreview ?: throw IllegalStateException("'nativeImage' nicht gesetzt in 'gradle.properties'"))
//}

tasks.named("bootRun", BootRun::class.java) {
    if (enablePreview != null) {
        jvmArgs(enablePreview)
    }

    // "System Properties", z.B. fuer Spring Properties oder fuer logback
    // https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties
    systemProperty("spring.profiles.active", activeProfiles)
    systemProperty("logging.file.name", "./build/log/application.log")
    // $env:TEMP\tomcat-docbase.* -> src\main\webapp (urspruengl. fuer WAR)
    // Document Base = Context Root, siehe https://tomcat.apache.org/tomcat-10.1-doc/config/context.html
    // $env:TEMP\hsperfdata_<USERNAME>\<PID> Java HotSpot Performance data log: bei jedem Start der JVM neu angelegt.
    // https://support.oracle.com/knowledge/Middleware/2325910_1.html
    // https://blog.mygraphql.com/zh/notes/java/diagnostic/hsperfdata/hsperfdata
    systemProperty("server.tomcat.basedir", "build/tomcat")

    if (usePersistence) {
        systemProperty(
            "spring.datasource.username",
            project.properties["spring.datasource.username"]
                ?: error("spring.datasource.username nicht gesetzt in gradle.properties"),
        )
        systemProperty(
            "spring.datasource.password",
            project.properties["spring.datasource.password"]
                ?: error("spring.datasource.password nicht gesetzt in gradle.properties"),
        )
        val dbUrl = project.properties["spring.datasource.url"]
        systemProperty(
            "spring.datasource.url",
            dbUrl
                ?: error("spring.datasource.url nicht gesetzt in gradle.properties"),
        )
        val namingStrategy = project.properties["spring.jpa.hibernate.naming.physical-strategy"]
        if (namingStrategy != null) {
            systemProperty("spring.jpa.hibernate.naming.physical-strategy", namingStrategy)
        }

        systemProperty(
            "spring.flyway.clean-disabled",
            project.properties["spring.flyway.clean-disabled"]
                ?: error("spring.flyway.clean-disabled nicht gesetzt in gradle.properties"),
        )
        dbUrl as String
        if (dbUrl.contains("postgres")) {
            systemProperty(
                "spring.flyway.tablespace",
                project.properties["spring.flyway.tablespace"]
                    ?: error("spring.flyway.tablespace nicht gesetzt in gradle.properties"),
            )
        }

        if (activeProfiles.toString().contains("dev")) {
            println("Persistence Properties:")
            println("-----------------------")
            println("spring.datasource.username=${systemProperties["spring.datasource.username"]}")
            println("spring.datasource.password=${systemProperties["spring.datasource.password"]}")
            println("spring.datasource.url=${systemProperties["spring.datasource.url"]}")
            println("spring.flyway.clean-disabled=${systemProperties["spring.flyway.clean-disabled"]}")
            println("spring.flyway.tablespace=${systemProperties["spring.flyway.tablespace"]}")
            println("")
        }
    }

    if (useSecurity) {
        val keycloakClientSecret =
            project.properties["app.keycloak.client-secret"]
                ?: error("'app.keycloak.client-secret' nicht gesetzt in gradle.properties")
        systemProperty("app.keycloak.client-secret", keycloakClientSecret)
        val keycloakSchema =
            project.properties["app.keycloak.schema"]
                ?: error("'app.keycloak.schema' nicht gesetzt in gradle.properties")
        systemProperty("app.keycloak.schema", keycloakSchema)
        val keycloakHost =
            project.properties["app.keycloak.host"]
                ?: error("'app.keycloak.host' nicht gesetzt in gradle.properties")
        systemProperty("app.keycloak.host", keycloakHost)
        val keycloakPort =
            project.properties["app.keycloak.port"]
                ?: error("'app.keycloak.port' nicht gesetzt in gradle.properties")
        systemProperty("app.keycloak.port", keycloakPort)
        val keycloakIssuerUri =
            project.properties["app.keycloak.issuer-uri"]
                ?: error("'app.keycloak.issuer-uri' nicht gesetzt in gradle.properties")
        systemProperty("app.keycloak.issuer-uri", keycloakIssuerUri)

        systemProperty("javax.net.ssl.trustStore", "extras/compose/keycloak/keycloak-truststore.p12")
        systemProperty("javax.net.ssl.trustStorePassword", "p")

        if (activeProfiles.toString().contains("dev")) {
            println("Keycloak Properties:")
            println("--------------------")
            println("app.keycloak.client-secret=${systemProperties["app.keycloak.client-secret"]}")
            println("app.keycloak.schema=${systemProperties["app.keycloak.schema"]}")
            println("app.keycloak.host=${systemProperties["app.keycloak.host"]}")
            println("app.keycloak.port=${systemProperties["app.keycloak.port"]}")
            println("app.keycloak.issuer-uri=${systemProperties["app.keycloak.issuer-uri"]}")
        }
    }
}

tasks.named<Test>("test") {
    val outputDir = reports.junitXml.outputLocation
    jvmArgumentProviders += CommandLineArgumentProvider {
        listOf(
            // build\test-results\test\open-test-report.xml
            "-Djunit.platform.reporting.open.xml.enabled=true",
            "-Djunit.platform.reporting.output.dir=${outputDir.get().asFile.absolutePath}"
        )
    }

    useJUnitPlatform {
        includeTags =
            when (project.properties["test"]) {
                "all" -> setOf("integration", "unit")
                "integration" -> setOf("integration")
                "rest" -> setOf("rest")
                "rest-get" -> setOf("rest-get")
                "rest-write" -> setOf("rest-write")
                "graphql" -> setOf("graphql")
                "query" -> setOf("query")
                "mutation" -> setOf("mutation")
                "unit" -> setOf("unit")
                "service-read" -> setOf("service-read")
                "service-write" -> setOf("service-write")
                else -> setOf("integration", "unit")
            }
    }

    systemProperty("spring.profiles.active", activeProfiles)
    systemProperty("junit.platform.output.capture.stdout", true)
    systemProperty("junit.platform.output.capture.stderr", true)

    systemProperty("logging.file.name", "./build/log/application.log")
    val logLevelTest = project.properties["logLevelTest"] ?: "INFO"
    // systemProperty("logging.level.com.acme", logLevelTest)
    systemProperty("logging.level.org.hibernate.SQL", logLevelTest)
    systemProperty("logging.level.org.hibernate.orm.jdbc.bind", logLevelTest)
    systemProperty("logging.level.org.flywaydb.core.internal.sqlscript.DefaultSqlScriptExecutor", logLevelTest)
    systemProperty("logging.level.org.springframework.web.reactive.function.client.ExchangeFunctions", logLevelTest)
    systemProperty("logging.level.org.springframework.web.service.invoker.PathVariableArgumentResolver", logLevelTest)
    systemProperty(
        "logging.level.org.springframework.web.service.invoker.RequestHeaderArgumentResolver",
        logLevelTest,
    )
    systemProperty(
        "logging.level.org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor",
        logLevelTest,
    )
    systemProperty(
        "logging.level.org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping",
        logLevelTest,
    )

    // $env:TEMP\tomcat-docbase.* -> src\main\webapp (urspruengl. fuer WAR)
    // Document Base = Context Root, siehe https://tomcat.apache.org/tomcat-10.1-doc/config/context.html
    // $env:TEMP\hsperfdata_<USERNAME>\<PID> Java HotSpot Performance data log: bei jedem Start der JVM neu angelegt.
    // https://support.oracle.com/knowledge/Middleware/2325910_1.html
    // https://blog.mygraphql.com/zh/notes/java/diagnostic/hsperfdata/hsperfdata
    systemProperty("server.tomcat.basedir", "build/tomcat")

    if (usePersistence) {
        systemProperty(
            "spring.datasource.username",
            project.properties["spring.datasource.username"]
                ?: error("spring.datasource.username nicht gesetzt in gradle.properties"),
        )
        systemProperty(
            "spring.datasource.password",
            project.properties["spring.datasource.password"]
                ?: error("spring.datasource.password nicht gesetzt in gradle.properties"),
        )
        val dbUrl = project.properties["spring.datasource.url"]
        systemProperty(
            "spring.datasource.url",
            dbUrl
                ?: error("spring.datasource.url nicht gesetzt in gradle.properties"),
        )

        systemProperty(
            "spring.flyway.clean-disabled",
            project.properties["spring.flyway.clean-disabled"]
                ?: error("spring.flyway.clean-disabled nicht gesetzt in gradle.properties"),
        )
        dbUrl as String
        if (dbUrl.contains("postgres")) {
            systemProperty(
                "spring.flyway.tablespace",
                project.properties["spring.flyway.tablespace"]
                    ?: error("spring.flyway.tablespace nicht gesetzt in gradle.properties"),
            )
        }
    }

    if (useSecurity) {
        val keycloakClientSecret =
            project.properties["app.keycloak.client-secret"]
                ?: error("'app.keycloak.client-secret' nicht gesetzt in gradle.properties")
        systemProperty("app.keycloak.client-secret", keycloakClientSecret)
        val keycloakSchema =
            project.properties["app.keycloak.schema"]
                ?: error("'app.keycloak.schema' nicht gesetzt in gradle.properties")
        systemProperty("app.keycloak.schema", keycloakSchema)
        val keycloakHost =
            project.properties["app.keycloak.host"]
                ?: error("'app.keycloak.host' nicht gesetzt in gradle.properties")
        systemProperty("app.keycloak.host", keycloakHost)
        val keycloakPort =
            project.properties["app.keycloak.port"]
                ?: error("'app.keycloak.port' nicht gesetzt in gradle.properties")
        systemProperty("app.keycloak.port", keycloakPort)
        val keycloakIssuerUri =
            project.properties["app.keycloak.issuer-uri"]
                ?: error("'app.keycloak.issuer-uri' nicht gesetzt in gradle.properties")
        systemProperty("app.keycloak.issuer-uri", keycloakIssuerUri)
    }

    if (enablePreview != null) {
        jvmArgs(enablePreview)
    }

    if (project.properties["showTestStandardStreams"] == "true" ||
        project.properties["showTestStandardStreams"] == "TRUE"
    ) {
        testLogging.showStandardStreams = true
    }

    extensions.configure(JacocoTaskExtension::class) {
        excludes = listOf("**/entity/*_.class", "**/config/*.class")
    }

    // https://docs.gradle.org/current/userguide/java_testing.html#sec:debugging_java_tests
    // https://www.jetbrains.com/help/idea/run-debug-configuration-junit.html
    // https://docs.gradle.org/current/userguide/java_testing.html#sec:debugging_java_tests
    // debug = true

    // finalizedBy("jacocoTestReport")
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

// https://docs.gradle.org/current/userguide/task_configuration_avoidance.html
// https://guides.gradle.org/migrating-build-logic-from-groovy-to-kotlin/#configuring-tasks
tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        xml.required = true
        html.required = true
    }

    classDirectories.setFrom(
        classDirectories.files.map {
            fileTree(it).matching { exclude(listOf("**/entity/*_.class", "**/dev/*.class")) }
        },
    )

    // https://docs.gradle.org/current/userguide/jacoco_plugin.html
    // https://github.com/gradle/gradle/pull/12626
    dependsOn(tasks.test)
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    violationRules {
        rule {
            limit { minimum = BigDecimal("0.7") }
        }
    }
}

// https://github.com/gradle/gradle/blob/master/platforms/jvm/code-quality/src/main/java/org/gradle/api/plugins/quality/CheckstylePlugin.java
// https://github.com/gradle/gradle/blob/master/platforms/jvm/code-quality/src/main/java/org/gradle/api/plugins/quality/CheckstyleExtension.java
checkstyle {
    toolVersion = libs.versions.checkstyle.get()
    isIgnoreFailures = false
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required = true
        html.required = true
    }
}

// https://github.com/gradle/gradle/blob/master/platforms/jvm/code-quality/src/main/java/org/gradle/api/plugins/quality/PmdPlugin.java
// https://github.com/gradle/gradle/blob/master/platforms/jvm/code-quality/src/main/java/org/gradle/api/plugins/quality/PmdExtension.java
pmd {
    toolVersion = libs.versions.pmd.get()
    ruleSetConfig = resources.text.fromFile(Path.of("config", "pmd", "ruleset.xml"))
}

spotbugs {
    // https://github.com/spotbugs/spotbugs/releases
    toolVersion = libs.versions.spotbugs.get()
}
tasks.named("spotbugsMain", SpotBugsTask::class.java) {
    reportLevel = Confidence.LOW
    // https://youtrack.jetbrains.com/projects/KTIJ/issues/KTIJ-34210/Flaky-Cannot-infer-type-for-this-parameter.-Specify-it-explicitly.-in
    reports.create("html", Action { required = true })
    // val excludePath = File("config/spotbugs/exclude.xml")
    val excludePath = Path.of("config", "spotbugs", "exclude.xml")
    excludeFilter = file(excludePath)
}

modernizer {
    toolVersion = libs.versions.modernizer.get()
    includeTestClasses = true
}

// https://docs.sonarqube.org/latest/analyzing-source-code/scanners/sonarscanner-for-gradle/#analyzing
// https://docs.sonarsource.com/sonarqube/latest/analyzing-source-code/languages/java
// https://docs.sonarsource.com/sonarqube/latest/analyzing-source-code/test-coverage/java-test-coverage/#gradle-project
// https://community.sonarsource.com/t/enable-preview-features-for-versions-other-than-the-maximum-supported/83291
sonarqube {
    properties {
        property("sonar.organization", "Softwarearchitektur und Microservices")
        property("sonar.projectDescription", "Beispiel fuer Softwarearchitektur")
        property("sonar.projectVersion", project.version)
        property("sonar.host.url", "http://localhost:9000")
        val sonarToken = project.properties["sonarToken"] ?: error("'sonarToken' nicht gesetzt in gradle.properties")
        property("sonar.token", sonarToken)
        property("sonar.scm.disabled", "true")
        property(
            "sonar.exclusions",
            ".gradle/**/*,.idea/**/*,build/**/*,config/**/*,extras/**/*,gradle/**/*,src/test/java/**/*,target/*,tmp/**/*",
        )
        property("sonar.java.source", javaLanguageVersionSonar)
        property("sonar.java.enablePreview", "true")
        property("sonar.gradle.skipCompile", "true")

        println("")
        println("sonar.junit.reportPaths=${properties["sonar.junit.reportPaths"]}")
        println("sonar.coverage.jacoco.xmlReportPaths=${properties["sonar.coverage.jacoco.xmlReportPaths"]}")
        println("sonar.jacoco.reportPath=${properties["sonar.jacoco.reportPath"]}")
        println("")
    }
}

// https://github.com/jeremylong/DependencyCheck/blob/master/src/site/markdown/dependency-check-gradle/configuration.md
// https://github.com/jeremylong/DependencyCheck/blob/main/pom.xml#L144
// https://repo.maven.apache.org/maven2/com/h2database/h2/2.1.214/
// java -jar h2-2.1.214.jar
//  Generic H2 (Embedded)
//  JDBC URL:       jdbc:h2:tcp://localhost/C:/Zimmermann/dependency-check-data/odc
//  Benutzername:   dcuser
//  Passwort:       DC-Pass1337!
//  Tabelle:        VULNERABILITY
dependencyCheck {
    // https://github.com/dependency-check/dependency-check-gradle/blob/main/src/main/groovy/org/owasp/dependencycheck/gradle/extension/NvdExtension.groovy
    // NVD = National Vulnerability Database
    // NIST = National Institute of Standards and Technology
    // https://nvd.nist.gov/developers/request-an-api-key
    nvd {
        val nvdApiKey = project.properties["nvdApiKey"] ?: error("'nvdApiKey' nicht gesetzt in gradle.properties")
        apiKey = nvdApiKey as String
        // default: 3500 Millisekunden Wartezeit zwischen den Aufrufen an das NVD API bei einem API-Key, sonst 8000
        //delay = 5000
        // default: max. 10 wiederholte Requests fuer einen Aufruf an das NVD API
        //nvdMaxRetryCount = 20
        // https://services.nvd.nist.gov/rest/json/cves/2.0
    }

    data {
        directory = "C:/Zimmermann/dependency-check-data"
        // https://github.com/jeremylong/DependencyCheck/blob/main/core/src/main/java/org/owasp/dependencycheck/data/nvdcve/DatabaseManager.java#L158
        // username = "dcuser"
        // password = "DC-Pass1337!"
    }

    suppressionFile = "$projectDir/config/dependencycheck/suppression.xml"
    scanConfigurations = listOf("productionRuntimeClasspath")
    analyzedTypes = listOf("jar")

    analyzers  {
        // nicht benutzte Analyzer
        archiveEnabled = false
        assemblyEnabled = false
        autoconfEnabled = false
        bundleAuditEnabled = false
        cmakeEnabled = false
        cocoapodsEnabled = false
        composerEnabled = false
        cpanEnabled = false
        dartEnabled = false
        golangDepEnabled = false
        golangModEnabled = false
        msbuildEnabled = false
        nugetconfEnabled = false
        nuspecEnabled = false
        pyDistributionEnabled = false
        pyPackageEnabled = false
        rubygemsEnabled = false
        swiftEnabled = false
        swiftPackageResolvedEnabled = false
        nodePackage { enabled = false }
        nodeAudit { enabled = false }
        retirejs { enabled = false }
    }

    // format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.HTML.toString()
    // format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL.toString()
}

tasks.named<Javadoc>("javadoc") {
    options {
        showFromPackage()
        // outputLevel = org.gradle.external.javadoc.JavadocOutputLevel.VERBOSE

        if (this is CoreJavadocOptions) {
            // https://stackoverflow.com/questions/59485464/javadoc-and-enable-preview
            addBooleanOption("-enable-preview", true)
            addStringOption("-release", javaLanguageVersionJavadoc)
            // Keine warnings
            //addBooleanOption("Xdoclint:none", true)
        }

        if (this is StandardJavadocDocletOptions) {
            author(true)
            bottom("Copyright &#169; 2016 - present J&uuml;rgen Zimmermann, Hochschule Karlsruhe. All rights reserved.")
        }
    }
}

tasks.named("asciidoctor", AsciidoctorTask::class) {
    asciidoctorj {
        setVersion(libs.versions.asciidoctorj.get())
        setJrubyVersion(libs.versions.jruby.get())
        modules {
            diagram.use()
            diagram.setVersion(libs.versions.asciidoctorjDiagram.get())
        }
    }

    val docPath = Path.of("extras", "doc")
    setBaseDir(file(docPath))
    setSourceDir(file(docPath))
    logDocuments = true

    doLast {
        @Suppress("ktlint:standard:chain-method-continuation")
        val outputPath = Path.of(layout.buildDirectory.asFile.get().absolutePath, "docs", "asciidoc")
        val outputFile = Path.of(outputPath.toFile().absolutePath, "projekthandbuch.html")
        println("Das Projekthandbuch ist in $outputFile")
    }
}

tasks.named("asciidoctorPdf", AsciidoctorPdfTask::class) {
    asciidoctorj {
        setVersion(libs.versions.asciidoctorj.get())
        setJrubyVersion(libs.versions.jrubyAsciidoctorpdf.get())
        modules {
            diagram.setVersion(libs.versions.asciidoctorjDiagram.get())
            diagram.use()
            pdf.setVersion(libs.versions.asciidoctorjPdf.get())
        }
    }

    val docPath = Path.of("extras", "doc")
    setBaseDir(file(docPath))
    setSourceDir(file(docPath))
    attributes(mapOf("pdf-page-size" to "A4"))
    logDocuments = true

    doLast {
        val outputPath =
            Path.of(
                @Suppress("ktlint:standard:chain-method-continuation")
                layout.buildDirectory.asFile.get().absolutePath,
                "docs",
                "asciidocPdf",
            )
        val outputFile = Path.of(outputPath.toString(), "projekthandbuch.pdf")
        println("Das Projekthandbuch ist in $outputFile")
    }
}

// TODO Gradle-Plugin 5.x: https://docs.asciidoctor.org/gradle-plugin/5.0
//asciidoc {
//    publications {
//        named("main") {
//            // org.asciidoctor.gradle.model5.core.publications.AsciidoctorPublication
//            sourceSet {
//                val docPath = Path.of("extras", "doc")
//                setSourceDir(file(docPath))
//                sources("*.adoc")
//            }
//        }
//    }
//    toolchains.named("asciidoctorj") {
//        registeredOutputFormatters.named("html", org.asciidoctor.gradle.model5.jvm.formatters.AsciidoctorjHtml5::class) {
//            //embedded.set(true)
//            print("backend=${backend}")
//        }
//    }
//}

licenseReport {
    configurations = arrayOf("runtimeClasspath")
}

tasks.named("dependencyUpdates", DependencyUpdatesTask::class) {
    checkConstraints = true
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
        // https://stackoverflow.com/questions/59950657/querydsl-annotation-processor-and-gradle-plugin
        sourceDirs.add(file("generated/"))
        generatedSourceDirs.add(file("generated/"))
    }
}
