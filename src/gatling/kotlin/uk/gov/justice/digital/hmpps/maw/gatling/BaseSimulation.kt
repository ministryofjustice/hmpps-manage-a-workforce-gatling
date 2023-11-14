package uk.gov.justice.digital.hmpps.maw.gatling

import io.gatling.javaapi.core.Simulation

abstract class BaseSimulation : Simulation() {
    val nominatedPduNameOne: String = "North Wales"
    val nominatedPduCodeOne: String = "WPTNWS"
    val nominatedTeamNameOne: String = "Wrexham - Team 1"
    val nominatedTeamCodeOne: String = "N03F01"
}
