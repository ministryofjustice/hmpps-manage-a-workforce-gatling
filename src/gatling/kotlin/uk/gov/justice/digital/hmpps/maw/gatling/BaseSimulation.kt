package uk.gov.justice.digital.hmpps.maw.gatling

import io.gatling.javaapi.core.Simulation

abstract class BaseSimulation : Simulation() {
    val nominatedPduNameOne = "North Wales"
    val nominatedPduCodeOne = "WPTNWS"
    val nominatedTeamNameOne = "Wrexham - Team 1"
    val nominatedTeamCodeOne = "N03F01"
}
