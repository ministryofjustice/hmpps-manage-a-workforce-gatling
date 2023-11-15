package uk.gov.justice.digital.hmpps.maw.gatling.service

import io.gatling.javaapi.core.ChainBuilder
import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.core.ScenarioBuilder
import io.gatling.javaapi.http.HttpDsl
import uk.gov.justice.digital.hmpps.maw.gatling.constants.*
import uk.gov.justice.digital.hmpps.maw.gatling.helper.HttpRequestHelper
import uk.gov.justice.digital.hmpps.maw.gatling.jdbc.UnallocatedCaseFeeder

class AllocateCaseScenarioService(
    private val unallocatedCaseFeeder: UnallocatedCaseFeeder = UnallocatedCaseFeeder(),
    private val httpRequestHelper: HttpRequestHelper = HttpRequestHelper(),
    private val pageOrchestrationService: PageOrchestrationService = PageOrchestrationService()
) {

    fun buildCaseAllocationScenarios(
        pduCode: String,
        pduName: String,
        teamCode: String,
        teamName: String
    ): Triple<ScenarioBuilder, ScenarioBuilder, ScenarioBuilder> {
        val basicCaseChainBuilder = httpChainBuilderForTeamPageAndUnallocatedCasesPageAndCaseSpecificSummaryPage(
            pduCode,
            pduName,
            teamCode,
            teamName,
            pauseOnAllocateCaseByTeamPage = pauseForBasicCaseOnAllocateCaseByTeamPage,
            pauseOnUnallocatedCasesPage = pauseForBasicCaseOnUnallocatedCasesPage,
            pauseOnSummaryPage = pauseForBasicCaseOnSummaryPage
        )
            .exitHereIfFailed()

        val normalCaseChainBuilder = httpChainBuilderForTeamPageAndUnallocatedCasesPageAndCaseSpecificSummaryPage(
            pduCode = pduCode,
            pduName,
            teamCode,
            teamName,
            pauseOnAllocateCaseByTeamPage = pauseForNormalCaseOnAllocateCaseByTeamPage,
            pauseOnUnallocatedCasesPage = pauseForNormalCaseOnUnallocatedCasesPage,
            pauseOnSummaryPage = pauseForNormalCaseOnSummaryPage
        ).exec(
            pageOrchestrationService.hitChoosePractitionerPageAndDoChecks(
                pduCode = pduCode
            )
        )
            .pause(pauseForNormalCaseOnChoosePractitionerPage)
            .exitHereIfFailed()

        val complexCaseChainBuilder = httpChainBuilderForTeamPageAndUnallocatedCasesPageAndCaseSpecificSummaryPage(
            pduCode,
            pduName,
            teamCode,
            teamName,
            pauseOnAllocateCaseByTeamPage = pauseForComplexCaseOnAllocateCaseByTeamPage,
            pauseOnUnallocatedCasesPage = pauseForComplexCaseOnUnallocatedCasesPage,
            pauseOnSummaryPage = pauseForComplexCaseOnSummaryPage
        ).exec(
            pageOrchestrationService.hitDocumentsPageAndDoChecks(
                pduCode = pduCode
            )
        )
            .pause(pauseForComplexCaseOnDocumentsPage)
            .exitHereIfFailed()

        val basicCasesScenario = CoreDsl.scenario("Basic Case Allocation Scenario")
            .exec(basicCaseChainBuilder)

        val normalCasesScenario = CoreDsl.scenario("Normal Case Allocation Scenario")
            .exec(normalCaseChainBuilder)

        val complexCasesScenario = CoreDsl.scenario("Complex Case Allocation Scenario")
            .exec(complexCaseChainBuilder)
        return Triple(basicCasesScenario, normalCasesScenario, complexCasesScenario)
    }

    private fun httpChainBuilderForTeamPageAndUnallocatedCasesPageAndCaseSpecificSummaryPage(
        pduCode: String,
        pduName: String,
        teamCode: String,
        teamName: String,
        pauseOnAllocateCaseByTeamPage: Long,
        pauseOnUnallocatedCasesPage: Long,
        pauseOnSummaryPage: Long
    ): ChainBuilder = CoreDsl.feed(unallocatedCaseFeeder.getJdbcFeeder(teamCode))
            .exec(HttpDsl.addCookie(httpRequestHelper.connectSidAuthCookie))
            .exec(
                pageOrchestrationService.hitAllocateCasesByTeamPageAndDoChecks(
                    pduCode = pduCode,
                    teamName = teamName
                )
            )
            .pause(
                pauseOnAllocateCaseByTeamPage
            )
            // have removed the below 'Unallocated Cases' page HTTP call as it intermittently fails on DEV env
//            .exec(
//                pageOrchestrationService.hitUnallocatedCasesPageAndDoChecks(
//                    pduCode = pduCode,
//                    pduName = pduName
//                )
//            )
//            .pause(pauseOnUnallocatedCasesPage)
            .exec(
                pageOrchestrationService.hitSummaryPageAndDoChecks(
                    pduCode = pduCode
                )
            )
            .pause(pauseOnSummaryPage)
}
