package com.wix.nutrimatic.internal.generators

import com.wix.nutrimatic.{Generator, GeneratorGenerator, TypeAndContext}

import scala.reflect.runtime.universe._

private[nutrimatic] object Objects extends GeneratorGenerator[Any] {
  override def isDefinedAt(tc: TypeAndContext): Boolean = {
    val (t, _) = tc
    val typeSymbol = t.typeSymbol
    typeSymbol.isModuleClass
  }

  override def apply(tc: TypeAndContext): Generator[Any] = {
    val (t, _) = tc
    val instance = rootMirror.reflectModule(t.typeSymbol.asClass.module.asModule).instance
    
    {
      case (_, context) => instance
    }
  }
}
