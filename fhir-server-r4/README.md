# R4 project
Welcome to FHIR R4 sample project

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites
*	Java 8
*	Tomcat 8 or 9
*	Postgres SQL DB 10.x
*	Maven 3.5.X

## Installing

### Clone FHIR Server repository 
Clone FHIR Server repository using command 

```
$ git clone https://github.com/siteadmin/fhir-tools.git
```

### Postgres Configuration

**Load R4 schema and data**

Create the database by running the below command in command prompt

```
$ createdb -h localhost -p 5432 -U postgres r4
```

R4 database file `fhir-r4-db.backup` is located under root directory. Load schema and sample data using psql command

```
$ psql -U postgres -d r4 -f fhir-r4-db.backup 
```

### Tomcat Configuration 

**Update application database properties.**

Open application.properties file under fhir-server/src/main/resources and change below properties and save the file. 

```  
jdbc.url=jdbc:postgresql://localhost:5432/r4
jdbc.username=postgres
jdbc.password=postgres
```

**Specifying the Context Path in the server.xml**

Open server.xml and add below two lines under `<host>` tag and save the file.

```
<Context path="/open/r4" docBase="./r4"/>
<Context path="/secure/r4" docBase="./r4"/>
```

### Built Application 
Run Maven build to build application war file. 
```
$ mvn clean install 
```
This will generate a war file under target/{application-name}.war. Copy this to your tomcat webapp directory for deployment.

**Start Tomcat service**

## Verification 
Verify using Postman or equivalent tool by running various FHIR APIs on the R4 server. 
```
For example: http://localhost:<port>/open/r4/fhir/Patient/1
```
  
