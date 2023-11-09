package allocations

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import io.gatling.javaapi.jdbc.JdbcDsl.jdbcFeeder

private const val nominatedPduName = "North Wales"
private const val nominatedPduCode = "WPTNWS"
private const val nominatedTeamName = "Wrexham - Team 1"
private const val nominatedTeamCode = "N03F01"

class AllocateCaseUsingFeSimulation : Simulation() {

  private val feederQuery = """
    SELECT crn, conviction_number
    FROM unallocated_cases
    where team_code='$nominatedTeamCode'
  """

  private val connectSidCookieValue = System.getenv("connectSidCookieValue")
  private val dbName = System.getenv("db_name")
  private val dbUsername = System.getenv("db_username")
  private val dbPassword = System.getenv("db_password")

  private val feeder = jdbcFeeder(
          "jdbc:postgresql://localhost:5432/$dbName",
          dbUsername,
          dbPassword,
          feederQuery
  ).random()

  private val connectSidCookie = Cookie("connect.sid", connectSidCookieValue)
          .withDomain("workforce-management-dev.hmpps.service.justice.gov.uk")
          .withPath("/")
          .withSecure(true)


  private val getCaseOverviewAndRisks = feed(feeder)
      .exec(addCookie(connectSidCookie))
      .exec(
          http("Allocate cases by team")
              .get("/pdu/$nominatedPduCode/teams")
              .check(
                  css(".govuk-table__caption:contains('Your teams')").exists(),
                  css("tbody tr .govuk-table__header:contains('$nominatedTeamName')").exists(),
                  css("a:contains('View unallocated cases')").exists()
              )
      ).pause(1)
      .exec(
          http("Unallocated cases")
              .get("/pdu/$nominatedPduCode/find-unallocated")
              .check(
                  css("h1:contains('$nominatedPduName')").exists()
              )
              .check(
                  css("table tbody tr td .govuk-body-s")
                  .findAll()
                  .transformWithSession { allCRNs, session ->
                      allCRNs.firstOrNull { it == session.getString("crn") }
                  }.notNull()
              )
              .check(
                  css("button:contains('Save and view selection')").exists()
              )
      )
      .exitHereIfFailed()

  private val httpProtocol =
    http.baseUrl("https://workforce-management-dev.hmpps.service.justice.gov.uk")
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0"
            )

  private val users = scenario("Users").exec(getCaseOverviewAndRisks)


  init {
    setUp(
      users.injectClosed(constantConcurrentUsers(1).during(1))
    ).protocols(httpProtocol)
  }
}
