Quickbooks-Integration
======================

This application is an extension of Mozu.  It integrates orders and customers with Quickbooks via the Quickbooks webconnector.  Here is a good technical overview of the architeture, http://wiki.consolibyte.com/wiki/doku.php/quickbooks_web_connector.  This application is a java solution that is deployed as a war file into your favorite container or application server.

## Requirements
* Windows to host:
  * Quickbooks
  * Quickbooks webconnector
* At least Java 1.7
* Web application server (e.g. tomcat, jboss, etc)

## Building and installing the application
Clone the git repository.
The build is accomplished with gradle targets.

$ ./gradlew clean build

This creates a war artifact at build/libs/Quickbooks.war that can be deployed to the application server.

## Quickbooks and webconnector setup
Follow the Quickbooks guides on the correct quickbooks and webconnector installation in a windows server or vm.
The webconnector can be found at http://marketplace.intuit.com/webconnector/.

## Create an application definition in Mozu
1. Go to Mozu Dev Center
2. Click "Develop | Applications"
3. Click "Create Application" button in right corner.
4. Enter the name and application id you'd like to use for your application.
5. There are resources that apply necessary configuration into the application.
6. Further sandbox configuration can be found at https://mozups.atlassian.net/wiki/pages/viewpage.action?spaceKey=MPS&title=Quickbooks+Setup.
7. Update the Mozu properties. This is the application ID and SharedSecret found in the Mozu application definition in the Mozu Dev Center ApplicationId= SharedSecret=

## Install the application into a Mozu tenant
To test and see the application, you must install the application to a Mozu sandbox tenant using the Dev Center.

The project can be run with embedded jetty using a gradle target.

$ ./gradlew jettyRun

