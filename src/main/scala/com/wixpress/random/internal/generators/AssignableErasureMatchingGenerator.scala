package com.wixpress.random.internal.generators

import com.wixpress.random.{ByErasure, Context, TypeAndRandom}

import scala.reflect.runtime.universe._

private[random] abstract class AssignableErasureMatchingGenerator[T](t: WeakTypeTag[T]) extends ByErasure[T] {
  override def isDefinedAt(x: TypeAndRandom): Boolean = {
    x._1.erasure <:< t.tpe.erasure
  }

  override def apply(v1: TypeAndRandom): T = {
    getValue(v1._1, v1._2)
  }

  protected def getValue(t: Type, context: Context): T
}

private[random] object AssignableErasureMatchingGenerator {
  def cacheKeyFromType(t: Type): Type = t.erasure
}
