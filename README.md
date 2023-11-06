# hmpps-manage-a-workforce-gatling

## Pre-requisites:
* Install Java: https://formulae.brew.sh/formula/openjdk
* Install Maven: https://formulae.brew.sh/formula/maven
* Install Intellij: https://www.jetbrains.com/idea/download/?section=mac

## Run load tests:
To kick off the load tests you will need to do the following:

1. Port forward to [Access the DEV RDS Database](https://user-guide.cloud-platform.service.justice.gov.uk/documentation/other-topics/rds-external-access.html#accessing-your-rds-database)
2. Go to `Manage a workforce Dev` web application and grab the `connection.sid` cookie's value: 
  * Go here in Google Chrome: https://workforce-management-dev.hmpps.service.justice.gov.uk
  * Log in
  * Right-click browser > Inspect
  * Go to `Application` tab > `Storage` in left nav > Expand `Cookies`
  * Find the `connect.sid` cookie in the list and copy its' value from the `Value` column
  * Copy the value for later step
3. Open repo in IntelliJ
4. Go to `AllocateCaseSimulation.kt` and set the concurrent user and during functions to ensure you are running the correct load against the APIs.
  * For example 1 user for 1 second would be ```users.injectClosed(constantConcurrentUsers(1).during(1))```
5. Right-click on `Engine.kt` class > `Run Engine`
6.  This will fail as missing env vars
7. Add missing env vars to `Engine` configuration:
  * Click on the `Engine` configuration that now exists next to the `Run` button on the top panel in Intellij
  * Click on `Edit Configurations`
  * Paste this into the `Environmental box (and swap out variables below for real secrets):

```db_name=<available_in_k8s_secrets>;db_password=<available_in_k8s_secrets>;db_username=<available_in_k8s_secrets>;connectSidCookieValue=<grab_this_from_browser_cookies_see_step_2>```
