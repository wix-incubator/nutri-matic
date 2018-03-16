package com.wixpress.random.internal.generators

import com.wixpress.random.{Generator, GeneratorGenerator, TypeAndRandom}

import scala.reflect.runtime.universe._

private[random] object RandomEnums extends GeneratorGenerator[Enumeration#Value] {

  override def isDefinedAt(x: TypeAndRandom): Boolean = {
    x._1 <:< typeOf[Enumeration#Value]
  }

  override def apply(x: TypeAndRandom): Generator[Enumeration#Value] = {
    val (valueType, _) = x
    val TypeRef(ownerType, _, _) = valueType
    val fields = valueSymbols(ownerType, valueType).toSeq
    val mirror = rootMirror
    val companion = mirror.reflectModule(ownerType.typeSymbol.asClass.module.asModule).instance
    val enumValues = fields.filter(_.isTerm).map(_.asTerm).map(mirror.reflect(companion).reflectField(_).get)
    val lastIndex = enumValues.size

    {
      case (_, r) => enumValues(r.basic.randomInt(0, lastIndex)).asInstanceOf[Enumeration#Value]
    }
  }

  private def valueSymbols(ownerType: Type, valueType: Type): Iterable[Symbol] =
    ownerType
      .members
      .filter(sym => !sym.isMethod && sym.typeSignature.baseType(valueType.typeSymbol) =:= valueType)

}
