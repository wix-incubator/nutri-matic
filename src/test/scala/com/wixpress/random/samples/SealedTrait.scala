package com.wixpress.random.samples

sealed trait SealedTrait

case class Case1(e: Enum1.Value) extends SealedTrait

case class Case2() extends SealedTrait
