package com.wixpress.nutrimatic.internal.generators

import com.wixpress.nutrimatic.{Generator, GeneratorGenerator, TypeAndContext}

import scala.reflect.runtime.universe._

private[nutrimatic] object ClassesWithConstructors extends GeneratorGenerator[Any] {

  override def isDefinedAt(tc: TypeAndContext): Boolean = {
    val (t, _) = tc
    val typeSymbol = t.typeSymbol
    typeSymbol.isClass && !typeSymbol.isAbstract && getPrimaryConstructor(t).isDefined
  }

  override def apply(tc: TypeAndContext): Generator[Any] = {
    val (t, _) = tc
    val mirror = rootMirror
    val clazz = t.typeSymbol.asClass
    val primaryConstructor = getPrimaryConstructor(t).get
    val ctorParams = primaryConstructor.infoIn(t).paramLists.head
    val reflected = mirror.reflectClass(clazz).reflectConstructor(primaryConstructor)

    {
      case (_, context) =>
        val args = ctorParams.map(s => {
          context.makeComponent(s.typeSignature, s.name.toString)
        })
        reflected.apply(args: _*)
    }
  }

  private def getPrimaryConstructor(t: Type): Option[MethodSymbol] = {
    val constructorDeclaration = t.decl(termNames.CONSTRUCTOR)
    if (constructorDeclaration.isTerm) {
      val alternatives = constructorDeclaration.asTerm.alternatives
      alternatives
        .collectFirst { case ctor: MethodSymbol if ctor.isPrimaryConstructor => ctor }
        .orElse(Some(alternatives.head.asMethod))
    } else {
      None
    }
  }
}
