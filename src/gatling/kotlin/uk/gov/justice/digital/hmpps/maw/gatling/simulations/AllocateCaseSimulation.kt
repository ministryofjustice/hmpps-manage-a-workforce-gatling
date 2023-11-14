package uk.gov.justice.digital.hmpps.maw.gatling.simulations

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import uk.gov.justice.digital.hmpps.maw.gatling.BaseSimulation
import uk.gov.justice.digital.hmpps.maw.gatling.config.HttpRequestConfig
import uk.gov.justice.digital.hmpps.maw.gatling.constants.noOfBasicCaseUsers
import uk.gov.justice.digital.hmpps.maw.gatling.constants.noOfComplexCaseUsers
import uk.gov.justice.digital.hmpps.maw.gatling.constants.noOfNormalCaseUsers
import uk.gov.justice.digital.hmpps.maw.gatling.service.*
import kotlin.time.*
import kotlin.time.Duration.Companion.hours

class AllocateCaseSimulation(
    allocateCaseScenarioService: AllocateCaseScenarioService = AllocateCaseScenarioService(),
    httpRequestConfig: HttpRequestConfig = HttpRequestConfig()
) : BaseSimulation() {

    private val httpProtocol =
        http.baseUrl(httpRequestConfig.baseUrl)
            .acceptHeader(httpRequestConfig.acceptHeader)
            .acceptLanguageHeader(httpRequestConfig.acceptLanguageHeader)
            .acceptEncodingHeader(httpRequestConfig.acceptEncodingHeader)
            .userAgentHeader(httpRequestConfig.userAgentHeader)

    init {
        val (
            basicCaseAllocationScenario,
            normalCaseAllocationScenario,
            complexCaseAllocationScenario
        ) = allocateCaseScenarioService.buildCaseAllocationScenarios(
            pduCode = nominatedPduCodeOne,
            pduName = nominatedPduNameOne,
            teamCode = nominatedTeamCodeOne,
            teamName = nominatedTeamNameOne
        )

        val basicCaseUsers = scenario("Basic Case Allocation Scenario")
            .exec(basicCaseAllocationScenario)

        val normalCases = scenario("Normal Case Allocation Scenario")
            .exec(normalCaseAllocationScenario)

        val complexCases = scenario("Complex Case Allocation Scenario")
            .exec(complexCaseAllocationScenario)

        setUp(
            basicCaseUsers.injectClosed(
                constantConcurrentUsers(noOfBasicCaseUsers).during(3.hours.toJavaDuration())
            ),
            normalCases.injectClosed(
                constantConcurrentUsers(noOfNormalCaseUsers).during(3.hours.toJavaDuration())
            ),
            complexCases.injectClosed(
                constantConcurrentUsers(noOfComplexCaseUsers).during(3.hours.toJavaDuration())
            )
        )
            .protocols(httpProtocol)
            .maxDuration(3.hours.toJavaDuration())
    }
}
