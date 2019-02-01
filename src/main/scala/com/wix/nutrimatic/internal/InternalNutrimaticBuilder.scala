/*
 * Copyright 2018 Wix.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wix.nutrimatic.internal

import com.wix.nutrimatic._

private[nutrimatic] case class InternalNutrimaticBuilder(byTypeEquality: Seq[ByTypeEquality[_]] = Seq.empty,
                                                         byErasure: Seq[ByErasure[_]] = Seq.empty,
                                                         custom: Seq[Generator[_]] = Seq.empty,
                                                         primitiveGenerators: JavaRandomRandomValues = new JavaRandomRandomValues,
                                                         maxSizePerCache: Int = 10000)
  extends NutrimaticBuilder {
  override def withCustomGenerators[T <: Any](additionalGenerators: Generator[T]*): NutrimaticBuilder = {
    val additionalByErasure: Seq[ByErasure[_]] = additionalGenerators.collect { case h: ByErasure[_] => h }
    val additionCustom: Set[Generator[_]] = additionalGenerators.toSet -- additionalByErasure
    copy(
      byErasure = byErasure ++ additionalByErasure,
      custom = custom ++ additionCustom
    )
  }

  override def withCollectionSizes(from: Int, to: Int): NutrimaticBuilder =
    copy(primitiveGenerators = primitiveGenerators.copy(collectionMinSize = from, collectionMaxSize = to))

  override def withStringLengths(from: Int, to: Int): NutrimaticBuilder =
    copy(primitiveGenerators = primitiveGenerators.copy(stringMinSize = from, stringMaxSize = to))
  
  override def withSeed(seed: Long): NutrimaticBuilder =
    copy(primitiveGenerators = primitiveGenerators.copy(initialSeed = seed))

  override def withMaxCacheSize(size: Int): NutrimaticBuilder =
    copy(maxSizePerCache = size)

  override def build: NutriMatic = new InternalNutriMatic(byErasure, custom, primitiveGenerators, maxSizePerCache)
}
