compose-idm
===========

This project includes the code for the COMPOSE Identity Management. See http://www.compose-project.eu/
This component is a Spring Java project. It requires the CloudFoundry User Account and Authentication (UAA) Server in order to work out-of-the-box. 

# Installation

## Requirements

	Java sun SDK 1.7
	gradle version 11.1 or higher
	maven 3.0.4 of higher
	gradle 1.11 or higher 
	MySQL
	git

## Quick Start


### Installing the UAA

Once you have installed the requirements listed above. 	You can start by Installing Cloud Foundry's UAA in the following way:


    $ git clone git://github.com/cloudfoundry/uaa.git
    $ cd uaa
    $ mvn install
    $ mvn tomcat7:run -Dmaven.tomcat.port=8081
    
However, please note that this method can be used only to run the UAA with an in-memory database. In case that you want to install it with another database (such as MySQL, PostgreSQL, etc) you would need to refer to the sysadmin guide locaed here:

https://github.com/cloudfoundry/uaa/blob/master/docs/Sysadmin-Guide.rst

For additional info, see https://github.com/cloudfoundry/uaa/.

### Installing COMPOSE Identity Management

By default Identity Management can be run  as a stand-alone jar file embedding all the functionalities as if they were provided by a regular application server, such as Tomcat.
 
#### Configurations

Modify the file located in  src/main/resources/application.properties to set port to run the IDM if required.
Modify src/main/resources/datasource.properties to set up the database connection to point to an existing (but empty) database.
Modify src/test/resources/datasource.properties to set up the database connection to point to an existing (but empty) database.
Modify src/test/resources/authentication.properties to set your own username and password for HTTP digest authentication.
Modify src/test/resources/uaa.properties to set up the client credentials, and the url to point to an installation of Cloud Foundry's UAA.

Other than creating a database and putting the name of the database in the previosly mentioned configuration files, there is no need to execute SQL scripts.
The database tables will be created afterwards during the building process (gradle build).


#### Run the compose-idm 

To get the code run: 

	$ git clone https://github.com/nopobyte/compose-idm
	$ cd compose-idm
	
You can build compose-idm as a jar file to execute with a simple "java -jar " command, or you can also build it as a war file, to deploy it in a Tomcat7 server, for example.

#### Run as a jar file

There is a section to build a jar file in the build.gradle file. It is surrounded by comments stating //jar changes... and //end of jar changes...
After uncommenting the lines between those to separators (i.e. the lines containing the 'jar' element) run the following.

	$ gradle build
	$ java -jar build/libs/COMPOSEIdentityManagement-0.5.0.jar 
	

### Run as a war file

Comment all the lines in the build.gradle for the jar element (pointed between the //jar changes comments). And uncomment the lines between //these are the war changes and //end of war changes... This would enable the proper configurations in gradle to build the war file. Afterwards execute the shell script called compile_war.sh or execute the following commands:

	$ gradle clean
	$ gradle build -x test
	$ gradle wrapper
	$ ./gradlew build -x test


### Testing IDM from the command line

To test this component there is a set of curl command lines available in the curl/tests-digest-authentication folder.

## Documentation 

The links to the documentation for specific sections of the API is available here:

* API for Groups: http://docs.composeidentitymanagement.apiary.io/
* API for Applications: http://docs.composeidmapplications.apiary.io/
* API for Service instances: http://docs.composeidmservices.apiary.io/
* API for Service Compositions: http://docs.composeidmservicecompositions.apiary.io/
* API for Service Source Code: http://docs.composeidmservicesourcecode.apiary.io/
* API for Service Objects: http://docs.composeidmserviceobjects.apiary.io/
* API for Users: http://docs.composeidmusers.apiary.io/

Please keep in mind, we try to keep the documentation as up-to-date as possible. However, we recommend to use the curl lines in the 'curl' folder for testing and experimentation with compose-idm.


## Importing the project as an eclipse java project

To import the project execute the following commands from a shell:

	$ git clone https://github.com/nopobyte/compose-idm
	$ cd compose-idm
	$ gradle eclipse

This will generate the proper eclipse files. Afterwards, just execute the 'import existing project into workspace' feature from eclipse.


