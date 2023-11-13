package uk.gov.justice.digital.hmpps.maw.gatling.service

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.http.HttpDsl
import uk.gov.justice.digital.hmpps.maw.gatling.helper.SelectorHelper
import uk.gov.justice.digital.hmpps.maw.gatling.model.CaseDetailsInSession

class AllocateCaseScenarioService(
    private val selectorHelper: SelectorHelper = SelectorHelper()
) {
    fun hitAllocateCasesByTeamPageAndDoChecks(pduCode: String, teamName: String) =
        HttpDsl.http("Allocate cases by team")
            .get("/pdu/$pduCode/teams")
            .check(
                CoreDsl.css(".govuk-table__caption:contains('Your teams')").exists(),
                CoreDsl.css("tbody tr .govuk-table__header:contains('$teamName')").exists(),
                CoreDsl.css("a:contains('View unallocated cases')").exists()
            )

    fun hitUnallocatedCasesPageAndDoChecks(pduCode: String, pduName: String) =
        HttpDsl.http("Unallocated cases")
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

    fun hitSummaryPageAndDoChecks(pduCode: String) =
        HttpDsl.http("Summary")
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

    fun hitDocumentsPageAndDoChecks(pduCode: String) =
        HttpDsl.http("Documents")
            .get { session ->
                val crn = session.getString(CaseDetailsInSession.CRN.sessionKey)
                val convictionNumber = session.getString(CaseDetailsInSession.CONVICTION_NUMBER.sessionKey)
                "/pdu/$pduCode/$crn/convictions/$convictionNumber/documents"
            }
            .check(
                checkCaseDetailsAreInPageHeader()
            )
            .check(
                checkCaseViewTabsArePresent()
            )
            .check(
                CoreDsl.css("h2:contains('Documents')").exists(),
                CoreDsl.css("a:contains('Continue')").exists()
            )

    fun hitChoosePractitionerPageAndDoChecks(pduCode: String) =
        HttpDsl.http("Choose Practitioner")
            .get { session ->
                val crn = session.getString(CaseDetailsInSession.CRN.sessionKey)
                val convictionNumber = session.getString(CaseDetailsInSession.CONVICTION_NUMBER.sessionKey)
                "/pdu/$pduCode/$crn/convictions/$convictionNumber/choose-practitioner"
            }
            .check(
                checkCaseDetailsAreInPageHeader()
            )
            .check(
                CoreDsl.css("h2:contains('Allocate to a probation practitioner')").exists(),
                CoreDsl.css("a:contains('Greg Hawkins')").exists(),
                CoreDsl.css("button:contains('Continue')").exists()
            )

    fun hitAllocateToAPractitionerPageAndDoChecks(
        pduCode: String,
        staffTeamCode: String,
        staffCode: String,
        staffName: String
    ) =
        HttpDsl.http("Allocate To A Practitioner")
            .get { session ->
                val crn = session.getString(CaseDetailsInSession.CRN.sessionKey)
                val convictionNumber = session.getString(CaseDetailsInSession.CONVICTION_NUMBER.sessionKey)
                "/pdu/$pduCode/$crn/convictions/$convictionNumber/allocate/$staffTeamCode/$staffCode/allocate-to-practitioner"
            }
            .check(
                checkCaseDetailsAreInPageHeader()
            )
            .check(
                CoreDsl.css("h2:contains('You're allocating this case to probation practitioner $staffName (PO)')").exists(),
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
