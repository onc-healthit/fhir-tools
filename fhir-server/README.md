# DSTU2 project
Welcome to FHIR DSTU2 sample project

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites
*	Java 7
*	Tomcat7 or 8
*	Postgres SQL DB 10.x
*	Maven 3.3.X

## Installing

### Clone FHIR Server repository 
Clone FHIR Server repository using command 

```
$ git clone https://github.com/siteadmin/fhir-tools.git
```

### Postgres Configuration

**Load DSTU2 schema and data**

Create the database by running the below command in command prompt

```
$ createdb -h localhost -p 5432 -U postgres hapi
```

DSTU2 database file “hapi-server.backup” is located under root directory. Load schema and sample data using psql command

```
$ psql -U postgres -d hapi -f hapi-server.backup
```

### Tomcat Configuration 

**Update application database properties.**

Open application.properties file under fhir-server/src/main/resources and change below properties and save the file. 

```  
jdbc.url=jdbc:postgresql://localhost:5432/hapi
jdbc.username=postgres
jdbc.password=postgres
```

**Specifying the Context Path in the server.xml**

Open server.xml and add below two lines under `<host>` tag and save the file.

```
<Context path="/open" docBase="./fhirserver"/>
<Context path="/secure" docBase="./fhirserver"/>
```

### Built Application 
Run Maven build to build application war file. 
```
$ mvn clean install 
```
This will generate a war file under target/{application-name}.war. Copy this to your tomcat webapp directory for deployment.

**Start Tomcat service**

## Verification 
Verify using Postman or equivalent tool by running various FHIR APIs on the DSTU2 server. 
```
For example: http://localhost:<port>/open/fhir/Patient/1
```
  
