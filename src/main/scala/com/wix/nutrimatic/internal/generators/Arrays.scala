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

import java.lang.reflect.Array.newInstance

import com.wix.nutrimatic.{Generator, GeneratorGenerator, TypeAndContext}

import scala.reflect.runtime.universe._

object Arrays extends GeneratorGenerator[Any] {
  override def isDefinedAt(tc: TypeAndContext): Boolean = {
    tc._1.typeSymbol == definitions.ArrayClass
  }

  override def apply(tc: TypeAndContext): Generator[Any] = {
    val (t, _) = tc
    val componentType = rootMirror.runtimeClass(t.typeArgs.head.typeSymbol.asClass)

    {
      case (_, context) => {
        val objectArray = context.randomCollection(context.makeComponent(t.typeArgs.head)).toArray
        val specificArray = newInstance(componentType, objectArray.length)
        Array.copy(objectArray, 0, specificArray, 0, objectArray.length)
        specificArray
      }
    }
  }
}
