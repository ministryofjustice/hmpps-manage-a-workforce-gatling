package uk.gov.justice.digital.hmpps.maw.gatling.helper

import io.gatling.javaapi.http.HttpDsl
import uk.gov.justice.digital.hmpps.maw.gatling.config.AuthConfig

class HttpRequestHelper(
    authConfig: AuthConfig = AuthConfig()
) {
    val connectSidAuthCookie = HttpDsl.Cookie("connect.sid", authConfig.connectSidCookie)
        .withDomain("workforce-management-dev.hmpps.service.justice.gov.uk")
        .withPath("/")
        .withSecure(true)
}
