package uk.gov.justice.digital.hmpps.maw.gatling.simulations

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import uk.gov.justice.digital.hmpps.maw.gatling.BaseSimulation
import uk.gov.justice.digital.hmpps.maw.gatling.config.HttpRequestConfig
import uk.gov.justice.digital.hmpps.maw.gatling.helper.HttpRequestHelper
import uk.gov.justice.digital.hmpps.maw.gatling.jdbc.UnallocatedCaseFeeder
import uk.gov.justice.digital.hmpps.maw.gatling.service.AllocateCaseScenarioService

const val noOfConcurrentUsers = 5
const val loadTestDurationInSecs = 5L

class AllocateCaseSimulation(
    allocateCaseScenarioService: AllocateCaseScenarioService = AllocateCaseScenarioService(),
    unallocatedCaseFeeder: UnallocatedCaseFeeder = UnallocatedCaseFeeder(),
    httpRequestConfig: HttpRequestConfig = HttpRequestConfig(),
    httpRequestHelper: HttpRequestHelper = HttpRequestHelper()
) : BaseSimulation() {

    private val allocateCaseScenarioChainBuilder =
        feed(unallocatedCaseFeeder.getJdbcFeeder(nominatedTeamCodeOne))
            .exec(addCookie(httpRequestHelper.connectSidAuthCookie))
            .exec(
                allocateCaseScenarioService.hitAllocateCasesByTeamPageAndDoChecks(
                    pduCode = nominatedPduCodeOne,
                    teamName = nominatedTeamNameOne
                )
            )
            .pause(1)
            .exec(
                allocateCaseScenarioService.hitUnallocatedCasesPageAndDoChecks(
                    pduCode = nominatedPduCodeOne,
                    pduName = nominatedPduNameOne
                )
            )
            .pause(1)
            .exec(
                allocateCaseScenarioService.hitSummaryPageAndDoChecks(
                    pduCode = nominatedPduCodeOne
                )
            )
            .pause(1)
            .exec(
                allocateCaseScenarioService.hitDocumentsPageAndDoChecks(
                    pduCode = nominatedPduCodeOne
                )
            )
            .pause(1)
            .exec(
                allocateCaseScenarioService.hitChoosePractitionerPageAndDoChecks(
                    pduCode = nominatedPduCodeOne
                )
            )
            .pause(1)
            .exec(
                allocateCaseScenarioService.hitAllocateToAPractitionerPageAndDoChecks(
                    pduCode = nominatedPduCodeOne,
                    staffTeamCode = nominatedAllocationStaffTeamCodeOne,
                    staffCode = nominatedAllocationStaffCodeOne,
                    staffName = nominatedAllocationStaffNameOne
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
