package uk.gov.justice.digital.hmpps.maw.gatling.constants

const val noOfBasicCaseUsers = 10
const val noOfNormalCaseUsers = 70
const val noOfComplexCaseUsers = 20

const val pauseForBasicCaseOnAllocateCaseByTeamPage = 5L
const val pauseForBasicCaseOnUnallocatedCasesPage = 20L
const val pauseForBasicCaseOnSummaryPage = 2L

const val pauseForNormalCaseOnAllocateCaseByTeamPage = 20L
const val pauseForNormalCaseOnUnallocatedCasesPage = 90L
const val pauseForNormalCaseOnSummaryPage = 2L
const val pauseForNormalCaseOnChoosePractitionerPage: Long = 85L

const val pauseForComplexCaseOnAllocateCaseByTeamPage = 25L
const val pauseForComplexCaseOnUnallocatedCasesPage = 120L
const val pauseForComplexCaseOnSummaryPage = 2L
const val pauseForComplexCaseOnDocumentsPage = 30L
