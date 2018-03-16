package com.wixpress.random.internal.generators

import com.wixpress.random.{Generator, GeneratorGenerator, TypeAndRandom}

import scala.reflect.runtime.universe._

private[random] object CaseClasses extends GeneratorGenerator[Any] {
  override def isDefinedAt(tc: TypeAndRandom): Boolean = {
    val (t, _) = tc
    val typeSymbol = t.typeSymbol
    typeSymbol.isClass && typeSymbol.asClass.isCaseClass
  }

  override def apply(tc: TypeAndRandom): Generator[Any] = {
    val (t, _) = tc
    val mirror = rootMirror
    val clazz = t.typeSymbol.asClass
    val primaryConstructor = t.decl(termNames.CONSTRUCTOR).asTerm.alternatives.collectFirst({
      case ctor: MethodSymbol if ctor.isPrimaryConstructor => ctor
    }).head
    val ctorParams = primaryConstructor.infoIn(t).paramLists.head
    val reflected = mirror.reflectClass(clazz).reflectConstructor(primaryConstructor)

    {
      case (_, context) =>
        val args = ctorParams.map(s => {
          context.random(s.typeSignature, s.name.toString)
        })
        reflected.apply(args: _*)
    }
  }
}
