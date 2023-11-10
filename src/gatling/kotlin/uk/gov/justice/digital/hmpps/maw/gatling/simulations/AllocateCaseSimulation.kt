package uk.gov.justice.digital.hmpps.maw.gatling.simulations

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import uk.gov.justice.digital.hmpps.maw.gatling.config.HttpRequestConfig
import uk.gov.justice.digital.hmpps.maw.gatling.helper.AllocateCaseScenarioHelper
import uk.gov.justice.digital.hmpps.maw.gatling.helper.HttpRequestHelper
import uk.gov.justice.digital.hmpps.maw.gatling.jdbc.UnallocatedCaseFeeder

private const val nominatedPduName = "North Wales"
private const val nominatedPduCode = "WPTNWS"
private const val nominatedTeamName = "Wrexham - Team 1"
private const val nominatedTeamCode = "N03F01"

class AllocateCaseSimulation(
    allocateCaseScenarioHelper: AllocateCaseScenarioHelper = AllocateCaseScenarioHelper(),
    unallocatedCaseFeeder: UnallocatedCaseFeeder = UnallocatedCaseFeeder(),
    httpRequestConfig: HttpRequestConfig = HttpRequestConfig(),
    httpRequestHelper: HttpRequestHelper = HttpRequestHelper()
) : Simulation() {

    private val allocateCaseScenarioChainBuilder =
        feed(unallocatedCaseFeeder.getJdbcFeeder(nominatedTeamCode)) // pulls data from unallocated_cases table
            .exec(addCookie(httpRequestHelper.connectSidAuthCookie)) // adds the connect.sid cookie to get passed security
            // runs through the pages we are testing for the scenario
            .exec(allocateCaseScenarioHelper.getAllocateCasesByTeamPage(nominatedPduCode, nominatedTeamName))
            .pause(1)
            .exec(allocateCaseScenarioHelper.getUnallocatedCasesPage(nominatedPduCode, nominatedPduName))
            .exitHereIfFailed()

    private val httpProtocol =
        http.baseUrl(httpRequestConfig.baseUrl)
            .acceptHeader(httpRequestConfig.acceptHeader)
            .acceptLanguageHeader(httpRequestConfig.acceptLanguageHeader)
            .acceptEncodingHeader(httpRequestConfig.acceptEncodingHeader)
            .userAgentHeader(httpRequestConfig.userAgentHeader)

    private val allocateCaseScenarioBuilder = scenario("Allocate Case Scenario")
            .exec(allocateCaseScenarioChainBuilder)

    init {
        setUp(
            allocateCaseScenarioBuilder.injectClosed(constantConcurrentUsers(1).during(1))
        ).protocols(httpProtocol)
    }
}
