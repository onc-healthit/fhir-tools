# BULKR4 project
Welcome to FHIR BULKR4 sample project

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

**Load bulkr4 schema and data**

Create the database by running the below command in command prompt

```
$ createdb -h localhost -p 5432 -U postgres bulkr4
```

bulkr4 database file `bulkr4.backup` is located under root directory. Load schema and sample data using psql command

```
$ psql -U postgres -d bulkr4 -f bulkr4.backup 
```

### Tomcat Configuration 

**Update application database properties.**

Open application.properties file under bulkr4/src/main/resources and change below properties and save the file. 

```  
jdbc.url=jdbc:postgresql://localhost:5432/bulkr4
jdbc.username=postgres
jdbc.password=postgres
```


### Built Application 
Run Maven build to build application war file. 
```
$ mvn clean install 
```
This will generate a war file under target/{application-name}.war. Copy this to your tomcat webapp directory for deployment.

**Start Tomcat service**

### Get Registered As New User
Provide the basic details and get registered followed by **Successful Registration** message
```


### Loging and Register Backend client
Login to Bulkr4 and add backend client with **Client Public Key URL/jku**:**null** and 
Publickey:

```
{
"keys": [
{
"kty": "RSA",
"e": "AQAB",
"kid": "rLbZSc78yH",
"key_ops": [
"verify"
],
"alg": "RS384",
"n": "qQIL0aYyCNdovdDsQvk3o7ZPQfkcY_HqP2JLOen9U-ntQH6IiZiQtdeSmf6NR3x9RxJD5Gl_BZg0pjL7cMyM-Q0r94qKLJv6qrQPA3KlrJL0ROo0w72b8LcbYQKxw6Vmy-oF7KnFplA1aifzBB4KeEYWGy1UnSQWIKeOColbYNkrH1SNAuE7_G_jfAdYdI41wy4JzO1rX8b3PjMcrB4F3JI5owqm8c7apnoW-RotYRWQW8DY1G6oRZqcRU2I9-FvBK4Y-wN8x5d0nXn9YLTLL3wZSGDSGB9E13E4RoGXVxE_XjBcYzSHav3hX40QuIqpv2GuScOBhTPHI5QZVQK03w"
}
]
}

```
**Note**: Public key must be in JSON format at the time of backend client registration.


select appropriate scope(s).The application responds with **clientid and token endpoint**
  
