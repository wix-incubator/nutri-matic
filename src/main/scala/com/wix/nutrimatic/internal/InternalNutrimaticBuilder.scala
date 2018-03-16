package com.wix.nutrimatic.internal

import com.wix.nutrimatic._

private[nutrimatic] case class InternalNutrimaticBuilder(byTypeEquality: Seq[ByTypeEquality[_]] = Seq.empty,
                                                         byErasure: Seq[ByErasure[_]] = Seq.empty,
                                                         custom: Seq[Generator[_]] = Seq.empty,
                                                         primitiveGenerators: JavaRandomRandomValues = new JavaRandomRandomValues,
                                                         maxSizePerCache: Int = 10000)
  extends NutrimaticBuilder {
  override def withCustomGenerators(additionalGenerators: Generator[_]*): NutrimaticBuilder = {
    val additionalByErasure = additionalGenerators.collect { case h: ByErasure[_] => h }
    val additionCustom = additionalGenerators.toSet -- additionalByErasure
    copy(
      byErasure = byErasure ++ additionalByErasure,
      custom = custom ++ additionCustom
    )
  }

  override def withCollectionSizes(from: Int, to: Int): NutrimaticBuilder =
    copy(primitiveGenerators = primitiveGenerators.copy(collectionMinSize = from, collectionMaxSize = to))

  override def withStringLengths(from: Int, to: Int): NutrimaticBuilder =
    copy(primitiveGenerators = primitiveGenerators.copy(stringMinSize = from, stringMaxSize = to))

  override def withOnlyAsciiCharacters: NutrimaticBuilder =
    copy(primitiveGenerators = primitiveGenerators.copy(onlyAscii = true))

  override def withAllCharacters: NutrimaticBuilder =
    copy(primitiveGenerators = primitiveGenerators.copy(onlyAscii = false))

  override def withSeed(seed: Long): NutrimaticBuilder =
    copy(primitiveGenerators = primitiveGenerators.copy(initialSeed = seed))

  override def withMaxCacheSize(size: Int): NutrimaticBuilder =
    copy(maxSizePerCache = size)

  override def build: NutriMatic = new InternalNutriMatic(byErasure, custom, primitiveGenerators, maxSizePerCache)
}
