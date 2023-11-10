package uk.gov.justice.digital.hmpps.maw.gatling.simulations

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import uk.gov.justice.digital.hmpps.maw.gatling.BaseSimulation
import uk.gov.justice.digital.hmpps.maw.gatling.config.HttpRequestConfig
import uk.gov.justice.digital.hmpps.maw.gatling.helper.HttpRequestHelper
import uk.gov.justice.digital.hmpps.maw.gatling.jdbc.UnallocatedCaseFeeder
import uk.gov.justice.digital.hmpps.maw.gatling.service.AllocateCaseScenarioService

const val noOfConcurrentUsers = 1
const val loadTestDurationInSecs = 1L

class AllocateCaseSimulation(
    allocateCaseScenarioService: AllocateCaseScenarioService = AllocateCaseScenarioService(),
    unallocatedCaseFeeder: UnallocatedCaseFeeder = UnallocatedCaseFeeder(),
    httpRequestConfig: HttpRequestConfig = HttpRequestConfig(),
    httpRequestHelper: HttpRequestHelper = HttpRequestHelper()
) : BaseSimulation() {

    private val allocateCaseScenarioChainBuilder =
        // pulls crn, conviction_number from unallocated_cases table
        feed(unallocatedCaseFeeder.getJdbcFeeder(nominatedTeamCodeOne))
            // adds the connect.sid cookie to get passed security
            .exec(addCookie(httpRequestHelper.connectSidAuthCookie))
            // runs through the pages we are testing for the scenario
            .exec(
                allocateCaseScenarioService.getAllocateCasesByTeamPage(
                    pduCode = nominatedPduCodeOne,
                    teamName = nominatedTeamNameOne
                )
            )
            .pause(1)
            .exec(
                allocateCaseScenarioService.getUnallocatedCasesPage(
                    pduCode = nominatedPduCodeOne,
                    pduName = nominatedPduNameOne
                )
            )
            .pause(1)
            .exec(
                allocateCaseScenarioService.getSummaryPage(
                    pduCode = nominatedPduCodeOne
                )
            ).exitHereIfFailed()

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
            allocateCaseScenarioBuilder.injectClosed(
                constantConcurrentUsers(noOfConcurrentUsers).during(loadTestDurationInSecs)
            )
        ).protocols(httpProtocol)
    }
}
