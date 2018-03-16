package com.wixpress.nutrimatic.internal

import com.wixpress.nutrimatic.{ByTypeEquality, Context, TypeAndContext}

import scala.reflect.runtime.universe._

private[nutrimatic] abstract class TypeEqualityMatchingGenerator[T](t: TypeTag[T]) extends ByTypeEquality[T] {
  override def isDefinedAt(x: TypeAndContext): Boolean = {
    x._1 =:= t.tpe
  }

  override def apply(tc: TypeAndContext): T = {
    getValue(tc._2)
  }

  protected def getValue(tc: Context): T
}
