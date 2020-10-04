This guide will show you how to deploy the User Interface Application.

Application code is available on [github](https://github.com/siteadmin/fhir-tools.git).

## Prerequisites

* [Node](https://nodejs.org/en/)
* Git

Follow the instructions to install the packages and build the node application.

Clone the project as follows:

```
$ git clone git clone -b bulk-api-v2 https://github.com/siteadmin/fhir-tools.git
$ cd {node-application-root-folder}
```

## NPM builds
Building the application:
```
$ npm install
```
This will download the required node modules. After successful completion of the proces run the below command.
```
$ ng serve
```
Application should now be running on http://localhost:4000. The app will automatically reload if you change any of the source files.