package uk.gov.justice.digital.hmpps.maw.gatling.simulations

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import uk.gov.justice.digital.hmpps.maw.gatling.BaseSimulation
import uk.gov.justice.digital.hmpps.maw.gatling.config.HttpRequestConfig
import uk.gov.justice.digital.hmpps.maw.gatling.service.*
import kotlin.time.*
import kotlin.time.Duration.Companion.seconds

class AllocateCaseSimulation(
    allocateCaseScenarioService: AllocateCaseScenarioService = AllocateCaseScenarioService(),
    httpRequestConfig: HttpRequestConfig = HttpRequestConfig(),
) : BaseSimulation() {

    private val httpProtocol =
        http.baseUrl(httpRequestConfig.baseUrl)
            .acceptHeader(httpRequestConfig.acceptHeader)
            .acceptLanguageHeader(httpRequestConfig.acceptLanguageHeader)
            .acceptEncodingHeader(httpRequestConfig.acceptEncodingHeader)
            .userAgentHeader(httpRequestConfig.userAgentHeader)

    private val basicCaseAllocationScenario = allocateCaseScenarioService.allocateCaseScenario(
        pduCode = nominatedPduCodeOne,
        pduName = nominatedPduNameOne,
        teamCode = nominatedTeamCodeOne,
        teamName = nominatedTeamNameOne,
        allocationStaffTeamCode = nominatedAllocationStaffTeamCodeOne,
        allocationStaffName = nominatedAllocationStaffNameOne,
        pauseOnAllocateCaseByTeamPage = pauseForBasicCaseOnAllocateCaseByTeamPage,
        pauseOnUnallocatedCasesPage = pauseForBasicCaseOnUnallocatedCasesPage,
        pauseOnSummaryPage = pauseForBasicCaseOnSummaryPage,
        pauseOnDocumentsPage = pauseForBasicCaseOnDocumentsPage,
        pauseOnChoosePractitionerPage = pauseForBasicCaseOnChoosePractitionerPage,
        pauseOnAllocateToAPractitionerPage = pauseForBasicCaseOnAllocateToAPractitionerPage,
    )

    private val normalCaseScenario = allocateCaseScenarioService.allocateCaseScenario(
        pduCode = nominatedPduCodeOne,
        pduName = nominatedPduNameOne,
        teamCode = nominatedTeamCodeOne,
        teamName = nominatedTeamNameOne,
        allocationStaffTeamCode = nominatedAllocationStaffTeamCodeOne,
        allocationStaffName = nominatedAllocationStaffNameOne,
        pauseOnAllocateCaseByTeamPage = pauseForNormalCaseOnAllocateCaseByTeamPage,
        pauseOnUnallocatedCasesPage = pauseForNormalCaseOnUnallocatedCasesPage,
        pauseOnSummaryPage = pauseForNormalCaseOnSummaryPage,
        pauseOnDocumentsPage = pauseForNormalCaseOnDocumentsPage,
        pauseOnChoosePractitionerPage = pauseForNormalCaseOnChoosePractitionerPage,
        pauseOnAllocateToAPractitionerPage = pauseForNormalCaseOnAllocateToAPractitionerPage,
    )

    private val simpleCaseUsers = scenario("Basic Case Allocation Scenario")
        .exec(basicCaseAllocationScenario)

    private val normalCases =
        scenario("Normal Case Allocation Scenario")
        .exec(normalCaseScenario)

    init {
        setUp(
            simpleCaseUsers.injectOpen(rampUsers(2).during(60.seconds.toJavaDuration()))
                .protocols(httpProtocol),

//            normalCases.injectOpen(rampUsers(90).during(6.hours.toJavaDuration()))
//                .protocols(httpProtocol)
//                .andThen(normalCases.injectOpen(constantUsersPerSec(90.0).during(1.hours.toJavaDuration()))
//                    .protocols(httpProtocol)
//                ),
//            simpleCaseUsers.injectOpen(rampUsers(10).during(6.hours.toJavaDuration()))
//                .protocols(httpProtocol)
//                .andThen(normalCases.injectOpen(constantUsersPerSec(10.0).during(1.hours.toJavaDuration()))
//                    .protocols(httpProtocol)
//                ),
        ).maxDuration(20.seconds.toJavaDuration())
    }
}

