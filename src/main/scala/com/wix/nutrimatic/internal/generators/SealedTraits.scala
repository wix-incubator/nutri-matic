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

package com.wix.nutrimatic.internal.generators

import com.wix.nutrimatic.{Generator, GeneratorGenerator, TypeAndContext}

private[nutrimatic] object SealedTraits extends GeneratorGenerator[Any] {
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
        context.makeComponent(subClass.asType.toType, s"subtype of $t")
    }
  }
}
