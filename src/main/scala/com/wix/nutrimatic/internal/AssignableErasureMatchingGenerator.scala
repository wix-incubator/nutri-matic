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

import com.wix.nutrimatic.{ByErasure, Context, TypeAndContext}

import scala.reflect.runtime.universe._

private[nutrimatic] abstract class AssignableErasureMatchingGenerator[T](t: WeakTypeTag[T]) extends ByErasure[T] {
  override def isDefinedAt(tc: TypeAndContext): Boolean = {
    tc._1.erasure <:< t.tpe.erasure
  }

  override def apply(tc: TypeAndContext): T = {
    getValue(tc._1, tc._2)
  }

  protected def getValue(t: Type, context: Context): T
}

private[nutrimatic] object AssignableErasureMatchingGenerator {
  def cacheKeyFromType(t: Type): Type = t.erasure
}
