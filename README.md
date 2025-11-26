# schiff-v1
Die erste Version meines SWA-Semesterprojekts

# Schiffsverwaltung – Semesterprojekt SWA

Ein Softwarearchitektur-Projekt (SWA) zur Verwaltung und Analyse von Schiffsdaten.  
Dies ist ein Backend-Dienst auf Basis von **Spring Boot**, erweitert und implementiert im Rahmen meines Studienprojekts.

---

## Über das Projekt

Dieses Projekt wurde im Rahmen des Moduls **Softwarearchitektur (SWA)** an der Hochschule Karlsruhe entwickelt.  
Es basiert auf einem offiziellen Lehr-Template (GPLv3, Prof. Zimmermann), wurde jedoch von mir um eigene Funktionen, Modelle, Validierungen und Architekturentscheidungen erweitert.

Der Fokus meiner Arbeit lag auf:

- Implementierung fachlicher Funktionen (CRUD, Validierung, Fehlerbehandlung)
- Aufbau einer klaren REST-Architektur
- Nutzung moderner Infrastruktur-, Testing- und Entwicklungstools
- Containerisierung & Deployment-Strategien
- Dokumentation mit UML, Asciidoc & API-Tools

---
## Eingesetzte Technologien & Tools

### **Backend & Architektur**
- **Java 25**
- **Spring Boot**  
  - REST Controller  
  - Service-Layer  
  - Repository-Layer  
  - Validation & Exception Handling

### **Build & Codequalität**
- **Gradle**
- **Checkstyle**
- **PMD**
- **SpotBugs**
- **JUnit / Mocking**

### **Dokumentation**
- **PlantUML** (Klassendiagramme, Sequenzdiagramme)
- **Asciidoc** (Projekthandbuch)
- **Bruno** & **Postman** (API-Testing)

### **Containerisierung & Deployment**
- **Docker**  
  - Image-Build per `bootBuildImage` (Paketo Buildpacks)  
    - Nutzt automatisch **Azul**, **Eclipse Temurin** oder andere JVM-Buildpacks  
  - Alternativer Build über eigenes `Dockerfile`
- **Docker Compose**  
  - Starten mehrerer Container (App, DB etc.)
- **Kubernetes Visualisierung:**  
  - **Lens**
- **IaC / Cloud Deployment:**  
  - **Terraform**  
  - **Pulumi** (optional, je nach Umgebung)

Diese Kombination zeigt, dass sowohl klassische Java-Backend-Entwicklung als auch moderne Cloud-/Container-Workflows beherrscht werden.

---
## Kontakt

**Name:** Murat Onur Yahsi 
**E-Mail:** muratonuryahsi@gmail.com  
