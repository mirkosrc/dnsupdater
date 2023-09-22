# dnsupdater

[![Java CI with Gradle](https://github.com/mirkosrc/dnsupdater/actions/workflows/gradle-ci-build.yml/badge.svg)](https://github.com/mirkosrc/dnsupdater/actions/workflows/gradle-ci-build.yml)


*This application retrieves the external IP address of your router, which is updated frequently by the internet service provider and tells it to your DNS provider.*


Install https://github.com/transmission/miniupnpc on your server. The Spring Boot application calls `external-ip`, which returns
the external IP address of your Fritz!Box Router.

Using an external configuration file:

`java -jar dnsupdater.jar --spring.config.additional-location=my.properties`
