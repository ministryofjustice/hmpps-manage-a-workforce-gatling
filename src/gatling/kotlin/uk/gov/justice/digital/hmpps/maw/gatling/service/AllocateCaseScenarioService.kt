package uk.gov.justice.digital.hmpps.maw.gatling.service

import io.gatling.javaapi.core.ChainBuilder
import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.http.HttpDsl
import uk.gov.justice.digital.hmpps.maw.gatling.helper.HttpRequestHelper
import uk.gov.justice.digital.hmpps.maw.gatling.jdbc.UnallocatedCaseFeeder

const val pauseForBasicCaseOnAllocateCaseByTeamPage = 2L
const val pauseForBasicCaseOnUnallocatedCasesPage = 9L
const val pauseForBasicCaseOnSummaryPage = 1L
const val pauseForBasicCaseOnDocumentsPage = 6L
const val pauseForBasicCaseOnChoosePractitionerPage: Long = 12L
const val pauseForBasicCaseOnAllocateToAPractitionerPage: Long = 2L

const val pauseForNormalCaseOnAllocateCaseByTeamPage = 20L
const val pauseForNormalCaseOnUnallocatedCasesPage = 90L
const val pauseForNormalCaseOnSummaryPage = 2L
const val pauseForNormalCaseOnDocumentsPage = 60L
const val pauseForNormalCaseOnChoosePractitionerPage: Long = 120L
const val pauseForNormalCaseOnAllocateToAPractitionerPage: Long = 20L

class AllocateCaseScenarioService(
    private val unallocatedCaseFeeder: UnallocatedCaseFeeder = UnallocatedCaseFeeder(),
    private val httpRequestHelper: HttpRequestHelper = HttpRequestHelper(),
    private val pageOrchestrationService: PageOrchestrationService = PageOrchestrationService()
) {
    fun allocateCaseScenario(
        pduCode: String,
        pduName: String,
        teamCode: String,
        teamName: String,
        allocationStaffTeamCode: String,
        allocationStaffName: String,
        pauseOnAllocateCaseByTeamPage: Long = pauseForNormalCaseOnAllocateCaseByTeamPage,
        pauseOnUnallocatedCasesPage: Long = pauseForNormalCaseOnUnallocatedCasesPage,
        pauseOnSummaryPage: Long = pauseForNormalCaseOnSummaryPage,
        pauseOnDocumentsPage: Long = pauseForNormalCaseOnDocumentsPage,
        pauseOnChoosePractitionerPage: Long = pauseForNormalCaseOnChoosePractitionerPage,
        pauseOnAllocateToAPractitionerPage: Long = pauseForNormalCaseOnAllocateToAPractitionerPage,

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
        .exec(
            pageOrchestrationService.hitDocumentsPageAndDoChecks(
                pduCode = pduCode
            )
        )
        .pause(pauseOnDocumentsPage)
        .exec(
            pageOrchestrationService.hitChoosePractitionerPageAndDoChecks(
                pduCode = pduCode
            )
        )
        .pause(pauseOnChoosePractitionerPage)
        .exec(
            pageOrchestrationService.hitAllocateToAPractitionerPageAndDoChecks(
                pduCode = pduCode,
                staffTeamCode = allocationStaffTeamCode,
                staffCode = allocationStaffTeamCode,
                staffName = allocationStaffName
            )
        )
        .pause(pauseOnAllocateToAPractitionerPage)
        .exitHereIfFailed()
}
