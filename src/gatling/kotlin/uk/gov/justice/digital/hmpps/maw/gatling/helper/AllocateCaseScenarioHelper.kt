package uk.gov.justice.digital.hmpps.maw.gatling.helper

import io.gatling.javaapi.core.CoreDsl
import io.gatling.javaapi.http.HttpDsl

class AllocateCaseScenarioHelper {
    fun getAllocateCasesByTeamPage(nominatedPduCode: String, nominatedTeamName: String) =
        HttpDsl.http("Allocate cases by team")
            .get("/pdu/$nominatedPduCode/teams")
            .check(
                CoreDsl.css(".govuk-table__caption:contains('Your teams')").exists(),
                CoreDsl.css("tbody tr .govuk-table__header:contains('$nominatedTeamName')").exists(),
                CoreDsl.css("a:contains('View unallocated cases')").exists()
            )

    fun getUnallocatedCasesPage(nominatedPduCode: String, nominatedPduName: String) = HttpDsl.http("Unallocated cases")
        .get("/pdu/$nominatedPduCode/find-unallocated")
        .check(
            CoreDsl.css("h1:contains('$nominatedPduName')").exists()
        )
        .check(
            CoreDsl.css("table tbody tr td .govuk-body-s")
                .findAll()
                .transformWithSession { allCRNs, session ->
                    allCRNs.firstOrNull { it == session.getString("crn") }
                }.notNull()
        )
        .check(
            CoreDsl.css("button:contains('Save and view selection')").exists()
        )
}
