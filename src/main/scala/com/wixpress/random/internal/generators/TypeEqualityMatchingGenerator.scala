package com.wixpress.random.internal.generators

import com.wixpress.random.{ByTypeEquality, Context, TypeAndRandom}

import scala.reflect.runtime.universe._

private[random] abstract class TypeEqualityMatchingGenerator[T](t: TypeTag[T]) extends ByTypeEquality[T] {
  override def isDefinedAt(x: TypeAndRandom): Boolean = {
    x._1 =:= t.tpe
  }

  override def apply(v1: TypeAndRandom): T = {
    getValue(v1._2)
  }

  protected def getValue(v1: Context): T
}
