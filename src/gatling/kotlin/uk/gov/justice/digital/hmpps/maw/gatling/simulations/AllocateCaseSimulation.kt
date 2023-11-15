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
import kotlin.time.Duration.Companion.minutes

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
            basicCasesAllocationScenario,
            normalCasesAllocationScenario,
            complexCasesAllocationScenario
        ) = allocateCaseScenarioService.buildCaseAllocationScenarios(
            pduCode = nominatedPduCodeOne,
            pduName = nominatedPduNameOne,
            teamCode = nominatedTeamCodeOne,
            teamName = nominatedTeamNameOne
        )

        setUp(
            basicCasesAllocationScenario.injectClosed(
                constantConcurrentUsers(noOfBasicCaseUsers).during(10.minutes.toJavaDuration())
            ),
            normalCasesAllocationScenario.injectClosed(
                constantConcurrentUsers(noOfNormalCaseUsers).during(10.minutes.toJavaDuration())
            ),
            complexCasesAllocationScenario.injectClosed(
                constantConcurrentUsers(noOfComplexCaseUsers).during(10.minutes.toJavaDuration())
            )
        )
            .protocols(httpProtocol)
            .maxDuration(10.minutes.toJavaDuration())
    }
}
