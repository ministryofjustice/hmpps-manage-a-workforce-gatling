package uk.gov.justice.digital.hmpps.maw.gatling.model

enum class CaseDetailsInSession(val sessionKey: String) {
    NAME("name"),
    CRN("crn"),
    TIER("tier"),
    CONVICTION_NUMBER("conviction_number")
}
