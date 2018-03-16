package com.wix.nutrimatic.internal.generators

import java.lang.reflect.Array.newInstance

import com.wix.nutrimatic.{Generator, GeneratorGenerator, TypeAndContext}

import scala.reflect.runtime.universe._

object Arrays extends GeneratorGenerator[Any] {
  override def isDefinedAt(tc: TypeAndContext): Boolean = {
    tc._1.typeSymbol == definitions.ArrayClass
  }

  override def apply(tc: TypeAndContext): Generator[Any] = {
    val (t, _) = tc
    val componentType = rootMirror.runtimeClass(t.typeArgs.head.typeSymbol.asClass)

    {
      case (_, context) => {
        val objectArray = context.randomCollection(context.makeComponent(t.typeArgs.head)).toArray
        val specificArray = newInstance(componentType, objectArray.length)
        Array.copy(objectArray, 0, specificArray, 0, objectArray.length)
        specificArray
      }
    }
  }
}
