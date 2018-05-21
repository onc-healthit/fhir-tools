This guide will show you how to deploy the application.

Application code is available on [github](https://github.com/siteadmin/fhir-tools.git) along with this document.

## Prerequisites

* Java, Maven and Git
* An installed version of [Postgres](http://www.postgresql.org/) 
* An installed version of [Tomcat]	(http://tomcat.apache.org/)

Follow the instructions to install the packages and make sure that they are on your path.
There are all sorts of dependencies will get downloaded automatically. This may take a few minute....

Clone the project as follows:

```
$ git clone git clone -b bulk-api-v2 https://github.com/siteadmin/fhir-tools.git
$ cd {application-root-folder}
```

##Database configuration settings
To change the database connection settings open the application.properties file from \src\main\resources. And modify the below properties
```
jdbc.url=jdbc:postgresql://localhost:5432/fhirdb
jdbc.username=postgres
jdbc.password=postgres
```

## Maven builds
Building the application:
```
$ mvn clean install
```
This will produce a file `target/{application-name}.war`. Copy this to your tomcat webapp directory for deployment.