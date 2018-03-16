package com.wixpress.nutrimatic.internal

import com.wixpress.nutrimatic._

private[nutrimatic] case class InternalRandomBuilder(byTypeEquality: Seq[ByTypeEquality[_]] = Seq.empty,
                                                     byErasure: Seq[ByErasure[_]] = Seq.empty,
                                                     custom: Seq[Generator[_]] = Seq.empty,
                                                     primitiveGenerators: JavaRandomBasicGenerators = new JavaRandomBasicGenerators,
                                                     maxSizePerCache: Int = 10000)
  extends RandomBuilder {
  override def withCustomGenerators(additionalGenerators: Generator[_]*): RandomBuilder = {
    val additionalByTypeEquality: Seq[ByTypeEquality[_]] = additionalGenerators.collect { case h: ByTypeEquality[_] => h }
    val additionalByErasure = additionalGenerators.collect { case h: ByErasure[_] => h }
    val additionCustom = additionalGenerators.toSet -- additionalByTypeEquality -- additionalByErasure
    copy(
      byTypeEquality = byTypeEquality ++ additionalByTypeEquality,
      byErasure = byErasure ++ additionalByErasure,
      custom = custom ++ additionCustom
    )
  }

  override def withCollectionSizes(from: Int, to: Int): RandomBuilder =
    copy(primitiveGenerators = primitiveGenerators.copy(collectionMinSize = from, collectionMaxSize = to))

  override def withStringLengths(from: Int, to: Int): RandomBuilder =
    copy(primitiveGenerators = primitiveGenerators.copy(stringMinSize = from, stringMaxSize = to))

  override def withOnlyAsciiCharacters: RandomBuilder =
    copy(primitiveGenerators = primitiveGenerators.copy(onlyAscii = true))

  override def withAllCharacters: RandomBuilder =
    copy(primitiveGenerators = primitiveGenerators.copy(onlyAscii = false))

  override def withSeed(seed: Long): RandomBuilder =
    copy(primitiveGenerators = primitiveGenerators.copy(initialSeed = seed))

  override def withMaxCacheSize(size: Int): RandomBuilder =
    copy(maxSizePerCache = size)

  override def build: Random = new InternalRandomFactory(byTypeEquality, byErasure, custom, primitiveGenerators, maxSizePerCache)
}
