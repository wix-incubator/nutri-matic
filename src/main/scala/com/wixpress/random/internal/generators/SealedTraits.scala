package com.wixpress.random.internal.generators

import com.wixpress.random.{Generator, GeneratorGenerator, TypeAndContext}

private[random] object SealedTraits extends GeneratorGenerator[Any] {
  override def isDefinedAt(tc: TypeAndContext): Boolean = {
    val (t, _) = tc
    val typeSymbol = t.typeSymbol
    typeSymbol.isClass && typeSymbol.asClass.isSealed
  }

  override def apply(tc: TypeAndContext): Generator[Any] = {
    val (t, _) = tc
    val classSymbol = t.typeSymbol.asClass
    val subClasses = classSymbol.knownDirectSubclasses.toVector

    {
      case (_, context) =>
        val subClass = subClasses(context.randomInt(0, subClasses.size))
        context.random(subClass.asType.toType, s"subtype of $t")
    }
  }
}
