This document outlines the steps required to create a local installation for the Edge Testing Tool.
This involves setting up at least three servers at this point due to the fact that the components in the architecture
use the standard port 25 (in addition to a few other ports). The main server is the Edge server that runs the main application packaged as a springboot jar. This also hosts the XDS toolkit and a Name server which require tomcat containers. The second server is for running the SMTP service - Apache James is being used. The ccda-validator is also hosted in this server. The third server runs the Direct RI (java). A different reorganization is possible, and will need adjustments to the configuration. 

## Edge server installation steps
####Prerequisites:
1. Java 8 openjdk (installation steps Appendix A)
2. Tomcat 7 (installation steps Appendix B).
3. Mysql database (installation steps Appendix C).
4. Name server (installation steps Appendix D).

####Installation.

Step 1. Navigate to the ETT’s downloadable and executable .jar file (ett.jar) located in the directory located [here](https://github.com/siteadmin/ett/releases). The needed configuration information (contained within the application properties file) is [here](https://github.com/siteadmin/ett/blob/resources/installation-resources/application.properties).

```
     # create /opt/ttp/logs (-p for parents create, if not present)
     # also, opt is the parent directory; but if you choose a different dir, configure ttpservice accordingly
     sudo mkdir -p /opt/ttp/logs
     sudo cp downloaded-ett.jar /sites/ttp
     sudo cp downloaded-application.properties /opt/ttp
     sudo cp downloaded-ttpservice /etc/init.d    
     sudo chmod u+x /etc/init.d/ttpservice
```

Step 2. Installing Local XDSTOOLS Instance.

The ETT depends on a version of XDSTOOLS for its XD* related components.  The user can either point the configuration file to the public copy available online (see the default instructions) or can point to a copy running on their local system.

Download the most recent version of xdstools?.war from the following link:
[xdstools] (https://github.com/usnistgov/iheos-toolkit2/releases). This is a web archive that will need to be deployed from a local Tomcat instance.  
 * mkdir -p [tomcat-dir]/toolkit/external_cache/environment/NA2015
 * Unzip [keyAndCert.zip](https://github.com/siteadmin/ett/blob/resources/certificates/common/xdr-tls/keyAndCert.zip) into [tomcat-dir]/toolkit/external_cache/environment/NA2015.
 * Deploy the xdstools?.war in tomcat 
 * Open the ui for the xdstools: http://[servername]:11080/xdstools 
 * Click "Toolkit configuration"
 * Password: "easy"
 * Configure the tomcat server.xml to use these keys for mutual TLS support; see tomcat installation steps for details.
 * Set external cache:[tomcat-dir]/toolkit/external_cache (create the directories as well)
 * Set toolkit port to 11080.
 * Toolkit Host: ttpedgedev.sitenv.org
 * Default Env "NA2015"
 * Save; set the application.properties for ett to point to this and restart.
 


## James server installation steps
## Direct RI server installation steps
The original document for the installation steps is [here]
However it is outlined again here (in a shorter version).
1. Install Java 8 (same steps as above): Note that the direct RI installation page recommends Java 7, we are moving to Java 8 for the whole installation. set JAVA_HOME to this path for the next steps.

2. Download Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 1.4.2
    `unzip jce_policy-1_4_2.zip`
For the below case the java home is : /usr/lib/jvm/java-8-openjdk-amd64

     ```
     sudo cp jce/US_export_policy.jar $JAVA_HOME/jre/lib/security
     sudo cp jce/local_policy.jar $JAVA_HOME/jre/lib/security
     ```

3. There is an issue with the Direct RI derby column size when importing private key in the above step. The following workaround can be used:


1. Download  [db-derby-10.12.1.1-bin.tar.gz](http://apache.cs.utah.edu//db/derby/db-derby-10.12.1.1/db-derby-10.12.1.1-bin.tar.gz)
2. Unzip using tar xzvf 
3. Run ij (derby manager) after shutting down tomcat

     ```
     cd $DIRECT_HOME/apache-tomcat-7.0.59/bin
     sudo ./shutdown.sh
     Cd /opt
     $ sudo /opt/db-derby-10.12.1.1-bin/bin/ij
     connect ‘jdbc:derby:$directhometomcat/bin/nhindconfig’;
     alter table nhind.certificate alter column certificatedata set data type blob(16k);
     disconnect;
     exit;
     Restart tomcat and now you should be able to import.
     $ cd $DIRECT_HOME/apache-tomcat-7.0.59/bin
     $ sudo ./startup.sh
     ```



## Appendix A
1. Install Java 8 openjdk.

    ```
    sudo add-apt-repository ppa:openjdk-r/ppa
    sudo apt-get update
    sudo apt-get install openjdk-8-jdk
    sudo update-alternatives --config java
    sudo update-alternatives --config javac
    Make sure that java -version points to the installed Java 8 openjdk.
    ```
    In case of package errors, you can try
    ```
    sudo rm /var/lib/apt/lists/* -vf
    sudo apt-get update
    ```

## Appendix B - Tomcat 7 installation

    sudo apt-get install tomcat7


Recommendation: Certain versions of tomcat seem to have issues, so please use tomcat-7.0.53 available [here](https://github.com/siteadmin/ett/blob/resources/installation-resources/tomcat7/apache-tomcat-7.0.53.tar.gz).

    sudo tar zxvf /opt/installs/apache-tomcat-7.0.53.tar.gz

Also note that since the Name Server packaged with Direct comes with its own tomcat, it can be used for the toolkit 
and validator; however for load balancing purposes, the validator is installed in the James server.

1. XDS Toolkit requires additional configuration in the server.xml:
       ```
       <Connector port="11084"
                SSLEnabled="true"
                maxThreads="150"
                secure="true"
                protocol="HTTP/1.1"
                scheme="https"
                clientAuth="false"
                SSlProtocol="TLS" sslEnabledProtocols="TLSv1.2,TLSv1.1,TLSv1"
                SSLCipherSuite="-ALL:HIGH:!ADH:!SSLv3:!MD5:!RC4"
                SSLCertificateFile="/opt/tomcat7/toolkit/external_cache/environment/NA2015/cert.pem"
                SSLCertificateKeyFile="/opt/tomcat7/toolkit/external_cache/environment/NA2015/key.pem"
                keystoreFile="/opt/tomcat7/toolkit/external_cache/environment/NA2015/keystore"
                keystorePass="changeit"
                keyAlias="1"
                truststoreFile="/opt/tomcat7/toolkit/external_cache/environment/NA2015/keystore"
                truststorePass="changeit"
                />
       ```

2. MDHT Validator requires additional configuration.


## Appendic C - MySQL installation

1. Install mysql

     ```
     sudo apt-get install mysql-server [root password set to: <amimysqlroot>]]
     sudo mysql_secure_installation
     sudo mysql_install_db
     sudo mysql -u root -p 
     create database direct
     create user 'direct'@'localhost' identified by ''
     grant all on direct.*  to direct
     ```
  
     Database schema can be found [here](https://github.com/edge-tool/ett/wiki/Database-Schema). Saving this in a file createdb.sql, you can run this myqsql command:

    `mysql direct -u direct < createdb.sql`


##Appendix D - Name server

Follow the "Direct RI server steps" - since it comes with name services (the steps corresponding to James server
need to be skipped, since ETT has a listener in the port 25).

Also, Apache installation is recommended for URL rewrite to make the ETT available
through 443 using https. For proxy and ssl support additional steps maybe required.

```
   sudo apt-get install apache2
   sudo a2enmod proxy_http
   sudo a2enmod ssl
```

   For ssl configuration the following steps maybe required:
   - copy the p12, key, cert files to /opt/ttp/certificates/private
   - create a pp.out in the same directory to echo the passphrase for bypassing sslpassphrasedialog on restart
   - add these two lines to ssl config     
```
        SSLCertificateFile    /opt/ttp/certificates/private/star.crt
        SSLCertificateKeyFile /opt/ttp/certificates/private/star.key
```


   If you have a passphrase you may want to add this to ports.conf under <IfModule mod_ssl.c>

```
   SSLPassPhraseDialog exec:/opt/ttp/certificates/private/pp.out
```

   Add these lines in the site conf files:
```
    <VirtualHost *:80>
        DocumentRoot /var/www
        ServerName ttpedgedev.sitenv.org
        ProxyPreserveHost On
        SSLProxyEngine on
        TimeOut 10000
        ProxyTimeout 10000
        Redirect    301    /     https://ttpedgedev.sitenv.org/ttp/
```

   and correspondingly in the ssl config:
```
       SSLProxyEngine on
        Redirect 301 / https://ttpedgedev.sitenv.org/ttp
        TimeOut 10000
        ProxyTimeout 10000
        ProxyPass       /ttp    https://ttpedgedev.sitenv.org:12080/ttp
        ProxyPassReverse        /ttp    https://ttpedgedev.sitenv.org:12080/ttp
```
