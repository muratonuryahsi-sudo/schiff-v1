# Schiff - Semesterprojekt SWA - Version 1 

**Schiffsverwaltung und -analyse**

Ein Softwarearchitektur-Projekt (SWA) zur Verwaltung und Analyse von Schiffsdaten, entwickelt im Rahmen des Moduls **Softwarearchitektur** an der Hochschule Karlsruhe.
Dies ist ein Backend-Dienst auf Basis von **Spring Boot** und **Java 25**, der als modernes Cloud-Native-Projekt konzipiert wurde.

---

## Über das Projekt

Dieses Projekt basiert auf einem offiziellen Lehr-Template (Prof. Zimmermann), wurde jedoch umfassend erweitert und angepasst, um eine vollständige, produktionsnahe Microservice-Architektur abzubilden.

**Kernfunktionen & Fokus:**
*   **Fachliche Domäne:** Verwaltung von Schiffen (CRUD), Validierung von Eingabedaten und robustes Fehlerhandling.
*   **REST-Architektur:** Sauber getrennte Layer (Controller, Service, Repository) mit DTO-Mapping.
*   **Qualitätssicherung:** Umfangreiche statische Codeanalyse und Tests.
*   **Deployment:** Containerisierung mit Docker (Buildpacks) und Orchestrierungsvorbereitung für Kubernetes.
*   **Dokumentation:** Architecture-as-Code mit PlantUML und Asciidoc.

---

## Technologie-Stack

### Backend & Core
*   **Java 25** (Preview Features aktiviert)
*   **Spring Boot 3.x**
    *   Spring Web MVC (REST)
    *   Spring Data JPA (Persistenz)
    *   Spring Validation
    *   Spring Security (OAuth2 Resource Server)
    *   Spring Boot Actuator (Observability)

### Build & Qualitätssicherung
*   **Gradle** (Kotlin DSL)
*   **Checkstyle** (Code-Formatierung)
*   **PMD** & **SpotBugs** (Statische Analyse)
*   **JUnit 5** & **Mockito** (Testing)
*   **JaCoCo** (Testabdeckung)

### Dokumentation & API
*   **SpringDoc OpenAPI** (Swagger UI)
*   **PlantUML** (UML-Diagramme in `extras/doc`)
*   **Asciidoc** (Projekthandbuch)
*   **Bruno** & **Postman** (API-Collections in `extras`)

### Infrastructure & Deployment
*   **Docker** (Paketo Buildpacks & Dockerfile)
*   **Docker Compose** (Lokale Umgebung)
*   **Kubernetes** (Konfigurationen in `extras/kubernetes`)
*   **Terraform** (Infrastructure as Code in `extras/terraform`)

---

## Erste Schritte

### Voraussetzungen
*   JDK 25
*   Docker Desktop (optional, für Container-Builds)

### Starten der Anwendung
Der Microservice kann direkt über Gradle gestartet werden:

```bash
./gradlew bootRun
```

### Wichtige Gradle-Tasks

| Task | Beschreibung |
| :--- | :--- |
| `./gradlew test` | Führt alle Unit-Tests aus |
| `./gradlew check` | Führt Tests und statische Codeanalyse (Checkstyle, SpotBugs, PMD) aus |
| `./gradlew bootBuildImage` | Erstellt ein OCI-konformes Docker-Image mittels Paketo Buildpacks |
| `./gradlew javadoc` | Generiert die JavaDoc-Dokumentation |
| `./gradlew asciidoctor` | Generiert das Projekthandbuch aus Asciidoc-Dateien |

---

## Projektstruktur

Die wichtigsten Verzeichnisse im Überblick:

*   `src/main/java`: Quellcode der Anwendung (Controller, Service, Domain-Modelle).
*   `src/test/java`: Unit- und Integrationstests.
*   `extras/doc`: Architekturdokumentation (PlantUML, Grafiken).
*   `extras/bruno` / `extras/postman`: API-Requests für Tests.
*   `extras/kubernetes`: K8s-Manifeste für das Deployment.
*   `extras/terraform`: IaC-Skripte für Cloud-Infrastruktur.

---

## Kontakt

**Entwickler:** Murat Onur Yahsi
**E-Mail:** muratonuryahsi@gmail.com
