package uk.gov.justice.digital.hmpps.maw.gatling.service

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.http.HttpDsl
import uk.gov.justice.digital.hmpps.maw.gatling.helper.SelectorHelper
import uk.gov.justice.digital.hmpps.maw.gatling.model.CaseDetailsInSession

class AllocateCaseScenarioService(
    private val selectorHelper: SelectorHelper = SelectorHelper()
) {
    fun getAllocateCasesByTeamPage(pduCode: String, teamName: String) =
        HttpDsl.http("Allocate cases by team Page")
            .get("/pdu/$pduCode/teams")
            .check(
                CoreDsl.css(".govuk-table__caption:contains('Your teams')").exists(),
                CoreDsl.css("tbody tr .govuk-table__header:contains('$teamName')").exists(),
                CoreDsl.css("a:contains('View unallocated cases')").exists()
            )

    fun getUnallocatedCasesPage(pduCode: String, pduName: String) =
        HttpDsl.http("Unallocated cases Page")
            .get("/pdu/$pduCode/find-unallocated")
            .check(
                CoreDsl.css("h1:contains('$pduName')").exists()
            )
            .check(
                selectorHelper.checkSessionValueExistsInSearchedSelector(
                    selector = "table tbody tr td .govuk-body-s",
                    sessionKey = CaseDetailsInSession.CRN.sessionKey
                )
            )
            .check(
                CoreDsl.css("button:contains('Save and view selection')").exists()
            )

    fun getSummaryPage(pduCode: String) =
        HttpDsl.http("Summary Page")
            .get { session ->
                val crn = session.getString(CaseDetailsInSession.CRN.sessionKey)
                val convictionNumber = session.getString(CaseDetailsInSession.CONVICTION_NUMBER.sessionKey)
                "/pdu/$pduCode/$crn/convictions/$convictionNumber/case-view"
            }
            .check(
                checkCaseDetailsAreInPageHeader()
            )
            .check(
                checkCaseViewTabsArePresent()
            )
            .check(
                CoreDsl.css("h2:contains('Summary')").exists(),
                CoreDsl.css("a:contains('Continue')").exists()
            )

    private fun checkCaseDetailsAreInPageHeader() =
        listOf(
            selectorHelper.checkSessionValueExistsInH1(
                sessionKey = CaseDetailsInSession.NAME.sessionKey
            ),
            selectorHelper.checkSessionValueExistsInH1(
                sessionKey = CaseDetailsInSession.TIER.sessionKey
            ),
            selectorHelper.checkSessionValueExistsInH1(
                sessionKey = CaseDetailsInSession.CRN.sessionKey
            )
        )

    private fun checkCaseViewTabsArePresent() = listOf(
        selectorHelper.checkTabExists(tabName = "Summary"),
        selectorHelper.checkTabExists(tabName = "Probation record"),
        selectorHelper.checkTabExists(tabName = "Risk"),
        selectorHelper.checkTabExists(tabName = "Documents")
    )
}
