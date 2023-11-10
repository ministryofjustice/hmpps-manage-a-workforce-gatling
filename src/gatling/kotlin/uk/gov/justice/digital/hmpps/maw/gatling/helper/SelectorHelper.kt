package uk.gov.justice.digital.hmpps.maw.gatling.helper

import io.gatling.javaapi.core.CheckBuilder.Final
import io.gatling.javaapi.core.CoreDsl

class SelectorHelper {
    fun checkSessionValueExistsInH1(sessionKey: String): Final = checkSessionValueExistsInSearchedSelector(
        selector = "h1",
        sessionKey
    )

    fun checkTabExists(tabName: String): Final = checkValueExistsInSearchedSelector(
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
