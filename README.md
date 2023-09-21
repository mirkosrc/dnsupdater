# dnsupdater

[![Java CI with Gradle](https://github.com/mirkosrc/dnsupdater/actions/workflows/gradle-ci-build.yml/badge.svg)](https://github.com/mirkosrc/dnsupdater/actions/workflows/gradle-ci-build.yml)

Using an external configuration file:

`java -jar dnsupdater.jar --spring.config.additional-location=my.properties`


Since you don't have an OpenWrt Router, there are limited ways to retrieve the external IP
The way is to install https://github.com/transmission/miniupnpc on your server and call `external-ip`, which returns 
the external ip of your plastic router. 