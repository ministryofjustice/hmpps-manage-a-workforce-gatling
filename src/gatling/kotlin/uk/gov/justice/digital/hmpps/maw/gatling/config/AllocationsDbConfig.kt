package uk.gov.justice.digital.hmpps.maw.gatling.config

data class AllocationsDbConfig(
    val dbName: String = System.getenv("db_name"),
    val dbUsername: String = System.getenv("db_username"),
    val dbPassword: String = System.getenv("db_password")
)
