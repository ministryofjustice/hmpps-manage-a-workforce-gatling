# hmpps-manage-a-workforce-gatling

## Pre-requisites:
* Install Java: https://formulae.brew.sh/formula/openjdk
* Install Maven: https://formulae.brew.sh/formula/maven
* Install Intellij: https://www.jetbrains.com/idea/download/?section=mac

## Prep for running load test - Connect to DB and get auth cookie:
To kick off the load tests you will need to do the following:

1. Port forward to [Access the DEV RDS Database](https://user-guide.cloud-platform.service.justice.gov.uk/documentation/other-topics/rds-external-access.html#accessing-your-rds-database)
2. Go to `Manage a workforce Dev` web application and grab the `connection.sid` cookie's value: 
  * Go here in Google Chrome: https://workforce-management-dev.hmpps.service.justice.gov.uk
  * Log in
  * Right-click browser > Inspect
  * Go to `Application` tab > `Storage` in left nav > Expand `Cookies`
  * Find the `connect.sid` cookie in the list and copy its' value from the `Value` column
  * Copy the value for later step


## Run load tests in Intellij:
1. Open repo in IntelliJ
2. Go to `AllocateCaseSimulation.kt` and set the concurrent user and during functions to ensure you are running the correct load against the APIs.
  * For example 1 user for 1 second would be ```users.injectClosed(constantConcurrentUsers(1).during(1))```
3. Right-click on `Engine.kt` class > `Run Engine`
4. This will fail as missing env vars
5. Add missing env vars to `Engine` configuration:
  * Click on the `Engine` configuration that now exists next to the `Run` button on the top panel in Intellij
  * Click on `Edit Configurations`
  * Paste this into the `VM options` box (and swap out variables below for real values - see `Prep for running load test` for how to get the value for `auth_cookie_value` and the rest our k8s secrets):
```-DconnectSidCookieValue=<auth_cookie_value> -Ddb_name=<db_name_value> -Ddb_password=<db_password_value>```



## Run load tests in Terminal:
1. Change directory in a terminal and navigate to this repo's root folder
2. Run this command (and swap out variables below for real values - see `Prep for running load test` for how to get the value for `auth_cookie_value` and the rest our k8s secrets):

```./gradlew gatlingRun-uk.gov.justice.digital.hmpps.maw.gatling.simulations.AllocateCaseSimulation -DconnectSidCookieValue=<auth_cookie_value> -Ddb_name=<db_name_value> -Ddb_password=<db_password_value>```
