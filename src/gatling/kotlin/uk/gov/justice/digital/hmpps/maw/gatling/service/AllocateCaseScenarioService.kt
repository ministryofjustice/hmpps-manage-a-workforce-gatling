package uk.gov.justice.digital.hmpps.maw.gatling.service

import io.gatling.javaapi.core.ChainBuilder
import io.gatling.javaapi.core.CoreDsl
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
    ): Triple<ChainBuilder, ChainBuilder, ChainBuilder> {
        val basicCaseAllocationScenario = allocateCaseScenario(
            pduCode,
            pduName,
            teamCode,
            teamName,
            pauseOnAllocateCaseByTeamPage = pauseForBasicCaseOnAllocateCaseByTeamPage,
            pauseOnUnallocatedCasesPage = pauseForBasicCaseOnUnallocatedCasesPage,
            pauseOnSummaryPage = pauseForBasicCaseOnSummaryPage
        )

        val normalCaseAllocationScenario = allocateCaseScenario(
            pduCode = pduCode,
            pduName,
            teamCode,
            teamName,
            pauseOnAllocateCaseByTeamPage = pauseForNormalCaseOnAllocateCaseByTeamPage,
            pauseOnUnallocatedCasesPage = pauseForNormalCaseOnUnallocatedCasesPage,
            pauseOnSummaryPage = pauseForNormalCaseOnSummaryPage,
            pauseOnChoosePractitionerPage = pauseForNormalCaseOnChoosePractitionerPage
        )

        val complexCaseAllocationScenario = allocateCaseScenario(
            pduCode,
            pduName,
            teamCode,
            teamName,
            pauseOnAllocateCaseByTeamPage = pauseForComplexCaseOnAllocateCaseByTeamPage,
            pauseOnUnallocatedCasesPage = pauseForComplexCaseOnUnallocatedCasesPage,
            pauseOnSummaryPage = pauseForComplexCaseOnSummaryPage,
            pauseOnDocumentsPage = pauseForComplexCaseOnDocumentsPage
        )
        return Triple(basicCaseAllocationScenario, normalCaseAllocationScenario, complexCaseAllocationScenario)
    }

    private fun allocateCaseScenario(
        pduCode: String,
        pduName: String,
        teamCode: String,
        teamName: String,
        pauseOnAllocateCaseByTeamPage: Long,
        pauseOnUnallocatedCasesPage: Long,
        pauseOnSummaryPage: Long,
        pauseOnDocumentsPage: Long? = null,
        pauseOnChoosePractitionerPage: Long? = null
    ): ChainBuilder {
        val allocatedCaseScenarioChainBuilder = CoreDsl.feed(unallocatedCaseFeeder.getJdbcFeeder(teamCode))
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
            .exec(
                pageOrchestrationService.hitUnallocatedCasesPageAndDoChecks(
                    pduCode = pduCode,
                    pduName = pduName
                )
            )
            .pause(pauseOnUnallocatedCasesPage)
            .exec(
                pageOrchestrationService.hitSummaryPageAndDoChecks(
                    pduCode = pduCode
                )
            )
            .pause(pauseOnSummaryPage)

        if (pauseOnDocumentsPage != null) {
            allocatedCaseScenarioChainBuilder.exec(
                pageOrchestrationService.hitDocumentsPageAndDoChecks(
                    pduCode = pduCode
                )
            )
                .pause(pauseOnDocumentsPage)
        }
        if (pauseOnChoosePractitionerPage != null) {
            allocatedCaseScenarioChainBuilder.exec(
                pageOrchestrationService.hitChoosePractitionerPageAndDoChecks(
                    pduCode = pduCode
                )
            )
                .pause(pauseOnChoosePractitionerPage)
        }
        allocatedCaseScenarioChainBuilder.exitHereIfFailed()
        return allocatedCaseScenarioChainBuilder
    }
}
