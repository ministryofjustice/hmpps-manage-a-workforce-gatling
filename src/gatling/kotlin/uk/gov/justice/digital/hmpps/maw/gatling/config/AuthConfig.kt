package uk.gov.justice.digital.hmpps.maw.gatling.config

data class AuthConfig(
    val connectSidCookie: String = System.getenv("connectSidCookieValue")
)
