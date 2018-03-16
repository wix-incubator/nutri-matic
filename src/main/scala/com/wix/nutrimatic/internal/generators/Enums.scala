package com.wix.nutrimatic.internal.generators

import com.wix.nutrimatic.{Generator, GeneratorGenerator, TypeAndContext}

import scala.reflect.runtime.universe._

private[nutrimatic] object Enums extends GeneratorGenerator[Enumeration#Value] {

  override def isDefinedAt(x: TypeAndContext): Boolean = {
    x._1 <:< typeOf[Enumeration#Value]
  }

  override def apply(x: TypeAndContext): Generator[Enumeration#Value] = {
    val (valueType, _) = x
    val TypeRef(ownerType, _, _) = valueType
    val fields = valueSymbols(ownerType, valueType).toSeq
    val mirror = rootMirror
    val companion = mirror.reflectModule(ownerType.typeSymbol.asClass.module.asModule).instance
    val enumValues = fields.filter(_.isTerm).map(_.asTerm).map(mirror.reflect(companion).reflectField(_).get)
    val lastIndex = enumValues.size

    {
      case (_, r) => enumValues(r.randomInt(0, lastIndex)).asInstanceOf[Enumeration#Value]
    }
  }

  private def valueSymbols(ownerType: Type, valueType: Type): Iterable[Symbol] =
    ownerType
      .members
      .filter(sym => !sym.isMethod && sym.typeSignature.baseType(valueType.typeSymbol) =:= valueType)

}
