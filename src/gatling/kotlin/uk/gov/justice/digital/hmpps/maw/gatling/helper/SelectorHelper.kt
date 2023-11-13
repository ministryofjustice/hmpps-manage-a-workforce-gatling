package uk.gov.justice.digital.hmpps.maw.gatling.helper

import io.gatling.javaapi.core.CheckBuilder.Final
import io.gatling.javaapi.core.CoreDsl
import uk.gov.justice.digital.hmpps.maw.gatling.model.CaseDetailsInSession

class SelectorHelper {
    fun checkCaseDetailsAreInPageHeader() =
        listOf(
            checkSessionValueExistsInH1(
                sessionKey = CaseDetailsInSession.NAME.sessionKey
            ),
            checkSessionValueExistsInH1(
                sessionKey = CaseDetailsInSession.TIER.sessionKey
            ),
            checkSessionValueExistsInH1(
                sessionKey = CaseDetailsInSession.CRN.sessionKey
            )
        )

    fun checkCaseViewTabsArePresent() = listOf(
        checkTabExists(tabName = "Summary"),
        checkTabExists(tabName = "Probation record"),
        checkTabExists(tabName = "Risk"),
        checkTabExists(tabName = "Documents")
    )

    private fun checkSessionValueExistsInH1(sessionKey: String): Final = checkSessionValueExistsInSearchedSelector(
        selector = "h1",
        sessionKey
    )

    private fun checkTabExists(tabName: String): Final = checkValueExistsInSearchedSelector(
        selector = ".moj-sub-navigation__link",
        value = tabName
    )

    fun checkSessionValueExistsInSearchedSelector(selector: String, sessionKey: String): Final =
        CoreDsl.css(selector)
            .findAll()
            .transformWithSession { valuesFound, session ->
                valuesFound.firstOrNull {
                    val sessionValue = session.getString(sessionKey)
                    it.contains(sessionValue!!)
                }
            }.notNull()

    private fun checkValueExistsInSearchedSelector(selector: String, value: String): Final =
        CoreDsl.css(selector)
            .findAll()
            .transformWithSession { valuesFound, session ->
                valuesFound.firstOrNull {
                    it.contains(value)
                }
            }.notNull()
}
