package uk.gov.justice.digital.hmpps.maw.gatling.jdbc

import io.gatling.javaapi.core.FeederBuilder
import io.gatling.javaapi.jdbc.JdbcDsl
import uk.gov.justice.digital.hmpps.maw.gatling.config.AllocationsDbConfig
import uk.gov.justice.digital.hmpps.maw.gatling.model.CaseDetailsInSession

class UnallocatedCaseFeeder(
    private val allocationsDbConfig: AllocationsDbConfig = AllocationsDbConfig()
) {
    fun getJdbcFeeder(nominatedTeamCode: String): FeederBuilder<Any> {
        val feederQuery = """
            SELECT  ${CaseDetailsInSession.NAME.sessionKey}, 
                    ${CaseDetailsInSession.CRN.sessionKey}, 
                    ${CaseDetailsInSession.TIER.sessionKey}, 
                    ${CaseDetailsInSession.CONVICTION_NUMBER.sessionKey}
            FROM unallocated_cases
            where team_code='$nominatedTeamCode'
          """
        return JdbcDsl.jdbcFeeder(
            "jdbc:postgresql://localhost:5432/${allocationsDbConfig.dbName}",
            allocationsDbConfig.dbUsername,
            allocationsDbConfig.dbPassword,
            feederQuery
        ).random()
    }
}
