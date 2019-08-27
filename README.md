# c-cda-validator
CCDA provides capabilities to validate your CCDAapplicable standards-specifications.
# Getting Started - Local Development

## 1. Installation
	To get started locally, follow these instructions:
	
	1. If you haven't done it already, make a fork of this repo https://github.com/onc-healthit/c-cda-validator.
	2. Clone to your local computer using git or svn
	
## 2. Dependency
	1. Java jdk/jre 1.6 or higher.
	2. Maven.
	3. MySQL 5.x or higher
	
## 3. Installation Steps

NOTE: Text in ***Italicized Bold*** are specific to your local install directory / information and needs to replaced with the corresponding values.

 1. Install Java
  
  Note: Make sure you have  **jre** directory under your JDK install 
  Eg: if your java install director is ***c:\java\jdk1.8.0_91***

  ![install_1](https://user-images.githubusercontent.com/20687947/56751121-48a17c80-6753-11e9-8735-310cf56da65b.png)
  
  	a. Set JAVA_HOME variable in the environment variable. ***JAVA_HOME= c:\java\jdk1.8.0_91***
	b. Add Jre to your PATH variable. ***C: \Java\jdk1.8.0_91\jre\bin***

2.  Install maven

	>> a. SET M2_HOME	M2_HOME = ***C: \apache-maven-3.3.9***
	>> b. Add maven to your PATH variable	***C:\apache-maven-3.3.9\bin***
	
3.	Install MySQL 5.6 or higher https://dev.mysql.com/downloads/mysql/5.6.html#downloads

	NOTE: please note down the root password 
	>> a. Add MYSQL bin to your PATH Variable ***C:\Program Files\MySQL\MySQL Server 5.7\bin***
	>> b. Run DB script

	>>> Steps
	>>> In the command problem execute the following command

>>>> c:\> mysql --user=root --password=***<provide your root password>***

>>>> mysql> create database direct;

>>>> mysql> use direct;

>>>> mysql> create user 'direct'@'localhost' identified by ‘’;

>>>> mysql> GRANT ALL PRIVILEGES ON direct.* TO 'direct'@'localhost';

>>>> mysql> source ***C:\<dtt_download\database\>\***direct_schema.sql
>>>> mysql> show tables;


4.	Creating New Packages

>> 1. Download code from Git Hub
>>> a. https://github.com/onc-healthit/c-cda-validator
>>> b. You should see the following structure
 
![install_2](https://user-images.githubusercontent.com/20687947/56751203-84d4dd00-6753-11e9-98e0-7e7a36b1a88c.png)

>> 2. Compile code
>>> Eg: if you download the code to ***C:\testing\ccda***
	
>>> Execute the following command from the command prompt twice 1st time you will see some FAILURE, execute the same command again and you should see all SUCCESS.

>>> mvn clean install -DskipTests

**First Run**

![install_3](https://user-images.githubusercontent.com/20687947/56751207-869ea080-6753-11e9-867b-4e8d87673795.png)

![install_5](https://user-images.githubusercontent.com/20687947/56751227-90280880-6753-11e9-9ca3-41f945586960.png)

**Second Run**


![install_6](https://user-images.githubusercontent.com/20687947/56751231-928a6280-6753-11e9-9984-a7e46960a4d9.png)

5.	Packages
>> Above steps will create a target folder under webapp directory and will have the jar file to run the application.
>> 1. Execute application

>>> a. Cd webapp\target

>>> b. Copy ..\application.properties.

>>> c. Copy ..\config.properties.

>>> d. Copy ..\announcements.txt.

>>> e. Copy ..\release_notes.txt.

>>> f. java -jar webapp-0.0.1-SNAPSHOT.jar.

![install_7](https://user-images.githubusercontent.com/20687947/56751235-93bb8f80-6753-11e9-9a56-3545e7eff4d2.png)

>>> Once you see the “Started Application” message

>>> Open a browser the type the following URL

>>> http://localhost:8081/ccda

>>> you should see the application running.

![install_8](https://user-images.githubusercontent.com/20687947/56751239-94ecbc80-6753-11e9-8bfa-a5b9960842cc.png)

>> 2. Edit properties
>>> a) Change version number, release date.
>>>> Please edit ***config.properties*** file to change the version number and release date.

![install_9](https://user-images.githubusercontent.com/20687947/56751241-961de980-6753-11e9-8222-1266e145f08d.png)

>>> b) Change Inquires or question email and googlegroup address.
>>>> Please edit ***applicaiton.properties*** file to change the Inquires or question email and googlegroup address.

![install_10](https://user-images.githubusercontent.com/20687947/56751253-9c13ca80-6753-11e9-9c80-2b5d75bbb77d.png)

>>> c) Change Announcements content.
>>>> Please edit ***announcements.txt*** file to change content of Announcements

![install_11](https://user-images.githubusercontent.com/20687947/56751257-9d44f780-6753-11e9-925e-6de22ba09814.png)

![install_12](https://user-images.githubusercontent.com/20687947/56751260-a0d87e80-6753-11e9-861c-f9300fe3c696.png)

>>> d) Change Release Notes content.
>>>> Please edit ***release_notes.txt*** file to change content of Release Notes

![install_13](https://user-images.githubusercontent.com/20687947/56751264-a33ad880-6753-11e9-8a41-5091a6461cd6.png)

![install_14](https://user-images.githubusercontent.com/20687947/56751270-a46c0580-6753-11e9-9907-d6bf4a3f3c6f.png)
