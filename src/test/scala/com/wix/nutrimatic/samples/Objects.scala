package com.wix.nutrimatic.samples

sealed trait SealedTraitWithCaseObject

case object CaseObject extends SealedTraitWithCaseObject

case class CaseClass() extends SealedTraitWithCaseObject

