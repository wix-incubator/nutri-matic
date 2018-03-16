package com.wixpress.random.internal.generators

import com.wixpress.random.{Generator, GeneratorGenerator, TypeAndContext}

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
        val objectArray = context.randomCollection(context.random(t.typeArgs.head)).toArray
        val specificArray = java.lang.reflect.Array.newInstance(componentType, objectArray.length)
        Array.copy(objectArray, 0, specificArray, 0, objectArray.length)
        specificArray
      }
    }
  }
}
