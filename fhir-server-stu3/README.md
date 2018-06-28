This guide provides instructions on how to build and deploy the application.

Prerequisites
* Java 8
* Tomcat7 or 8
* Maven 3.3.X
* Postgres SQL DB 10.x

Installation of Pre-requisites:
Install Java
Install Maven.
Install Postgres.
Install Tomcat

Population of the Postgres Database with STU3 schema and data.:
* Use the fhir-stu3-db.backup script to populate the Postgres DB with tables and data required for the FHIR Server.
* The script can be run using pgAdmin console or using command line connection to the postgres DB. For more instructions see Postgres documentation.

Download the project:
* Clone the git project project.
* Update database properties

##Database configuration settings To change the database connection settings open the application.properties file from \src\main\resources. And modify the below properties

jdbc.url=jdbc:postgresql://localhost:5432/fhirdb
jdbc.username=postgres
jdbc.password=postgres


Build Application

$ mvn clean install
This will produce a file target/{application-name}.war. Copy this to your tomcat webapp directory for deployment.

Tomcat Configuration for Open and Secure Servers:

The following needs to be added to the tomcat server.xml file to configure open and secure servers using the same code base.

 <Context path="/open/stu3" docBase="./stu3"/>
 <Context path="/secure/stu3" docBase="./stu3"/>

Verification:

Verify using Postman or equivalent tool by running various FHIR APIs on the STU3 server.
For example: http://localhost:/open/stu3/fhir/Patient/1
