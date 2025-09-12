
# DCC Backend Rest API
- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [License](#license)
## Overview
The DCC Backend  is a storage service that retrieves DCCs (Digital calibration certificates) from an available pidListUrl and responds with a XML file converted into Base64-encoded strings.
All data is attributed a unique and persistent identifier (PID). 

## Prerequisites

- Java JDK 17+
- Spring Boot 3.x
- Maven
- MySQL version 8.x
- Spring Data JPA
- Spring Web
- Spring Boot DevTools

## Getting Started

1. **Clone the repository:**

```bash
https://github.com/PTBresearch/DCC-Backend.git
```
2. **local:**
   Add a local ./m2/settings.xml to allow maven to use the proxy server:

```xml

<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <proxies>
    <proxy>
      <id>.....webproxy</id>
      <active>true</active>
      <protocol>http</protocol>
      <host>webproxy.example.com</host>
      <port>8080</port>
      <nonProxyHosts>localhost|*.example.com</nonProxyHosts>
    </proxy>
  </proxies>
</settings>
```
3. **Build the project local using Maven:**

```bash
mvn clean install
```

4. **Run the application:**

```bash
mvn spring-boot:run
```
## Running the Application

Once the application is running, you can access the API endpoints via `http://localhost:8085/api/d-dcc/dccPidList`.
## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.