# dnsupdater

[![Java CI with Gradle](https://github.com/mirkosrc/dnsupdater/actions/workflows/gradle-ci-build.yml/badge.svg)](https://github.com/mirkosrc/dnsupdater/actions/workflows/gradle-ci-build.yml)


*This application retrieves the external IP address of your router, which is updated frequently by the internet service provider and tells it to your DNS provider.*

# Instuctions
1. Build jar with `./gradlew bootJar`.
2. Deploy and run on your server: `java -jar dnsupdater.jar`. 

You may want to run it as a systemd service at system startup time with a different user and limited rights.

Create `/etc/systemd/system/ipupdater-springboot.service` with
```
[Unit]
Description=A Spring Boot application.
After=syslog.target

[Service]
User=javauser
ExecStart=/home/someone/ipSpring.sh SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

The Spring Boot application calls the Fritz!Box API via SOAP, which returns
the external IP address of your Fritz!Box Router.

### Optional:
Using an external configuration file:

`java -jar dnsupdater.jar --spring.config.additional-location=my.properties`
