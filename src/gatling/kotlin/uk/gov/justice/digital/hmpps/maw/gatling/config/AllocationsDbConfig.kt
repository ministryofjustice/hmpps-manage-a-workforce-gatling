package uk.gov.justice.digital.hmpps.maw.gatling.config

data class AllocationsDbConfig(
    val dbName: String = System.getProperty("db_name"),
    val dbUsername: String = System.getProperty("db_username"),
    val dbPassword: String = System.getProperty("db_password")
)
