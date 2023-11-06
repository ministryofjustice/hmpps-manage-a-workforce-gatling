package allocations

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import io.gatling.javaapi.jdbc.JdbcDsl.jdbcFeeder

class AllocateCaseUsingBeApisSimulation : Simulation() {

  private val feederQuery = """
    SELECT crn, conviction_number
    FROM unallocated_cases
  """

  private val dbName = System.getenv("db_name")
  private val dbUsername = System.getenv("db_username")
  private val dbPassword = System.getenv("db_password")
  private val accessToken = System.getenv("access_token")

  private val feeder = jdbcFeeder(
          "jdbc:postgresql://localhost:5432/$dbName",
          dbUsername,
          dbPassword,
          feederQuery
  ).queue()

  private val getCaseOverviewAndRisks = feed(feeder).exec(
          http("GetUnallocatedCaseOverview")
                  .get("/cases/unallocated/#{crn}/convictions/#{conviction_number}/overview")
                  .header("Authorization", "Bearer $accessToken")
                  .check(
                          status().shouldBe(200),
                          //jsonPath("$.tier").ofString().saveAs("caseTier")
                  )
  ).pause(1).exec(
          http("GetUnallocatedCaseRisks")
                  .get("/cases/unallocated/#{crn}/convictions/#{conviction_number}/risks")
                  .header("Authorization", "Bearer $accessToken")
                  .check(status().shouldBe(200))
  ).exitHereIfFailed()

  private val httpProtocol =
    http.baseUrl("https://hmpps-allocations-dev.hmpps.service.justice.gov.uk")
      .acceptHeader("application/json")
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
