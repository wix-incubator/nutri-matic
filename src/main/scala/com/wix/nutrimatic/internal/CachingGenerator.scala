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

import com.wix.nutrimatic.internal.util.SimpleCache
import com.wix.nutrimatic.{Generator, TypeAndContext}

import scala.reflect.runtime.universe._

private[nutrimatic] trait CachingGenerator[Key <: AnyRef, Value] extends Generator[Value] {
  def orElseCached(other: CachingGenerator[Key, Value]): CachingGenerator[Key, Value] = new CachingGenerator[Key, Value] {
    override def hasCached(tc: TypeAndContext): Boolean = CachingGenerator.this.hasCached(tc) || other.hasCached(tc)

    override def isDefinedAt(x: TypeAndContext): Boolean = hasCached(x) || CachingGenerator.this.isDefinedAt(x) || other.isDefinedAt(x)

    override def apply(tc: TypeAndContext): Value = {
      if (CachingGenerator.this.hasCached(tc)) {
        CachingGenerator.this.apply(tc)
      } else if (other.hasCached(tc)) {
        other.apply(tc)
      } else {
        CachingGenerator.this.applyOrElse(tc, other.apply)
      }
    }
  }

  def onlyIfCached: Generator[Value] = new Generator[Value] {

    override def isDefinedAt(x: TypeAndContext): Boolean = CachingGenerator.this.hasCached(x)

    override def apply(tc: TypeAndContext): Value = CachingGenerator.this.apply(tc)
  }

  protected def hasCached(tc: TypeAndContext): Boolean
}

private[nutrimatic] object CachingGenerator {
  def apply[T](generators: Seq[Generator[T]], keyFromType: Type => Type = identity, maxCacheSize: Int): CachingGenerator[Type, T] =
    new ByKeyLookupCachingGenerator[Type, T](
      generators = generators,
      keyFromType = keyFromType,
      maxCacheSize = maxCacheSize
    )

  def fromGeneratorsOfGenerators[T](generatorGenerators: Seq[Generator[Generator[T]]], maxCacheSize: Int): CachingGenerator[Type, T] =
    new GeneratorCachingGenerator[T](
      wrapppedGeneratorGenerators = generatorGenerators,
      maxCacheSize = maxCacheSize)
}

private class GeneratorCachingGenerator[Value](wrapppedGeneratorGenerators: Seq[Generator[Generator[Value]]], maxCacheSize: Int)
  extends AbstractCachingGenerator[Type, Value](identity, maxCacheSize) {
  
  override protected def canHandle(tc: TypeAndContext): Boolean = findGeneratorGenerator(tc).isDefined

  override protected def findGenerator(tc: TypeAndContext): Option[Generator[Value]] = findGeneratorGenerator(tc).map(_.apply(tc))

  private def findGeneratorGenerator(tc: TypeAndContext): Option[Generator[Generator[Value]]] = {
    wrapppedGeneratorGenerators.find(_.isDefinedAt(tc))
  }

}

private class ByKeyLookupCachingGenerator[Key <: AnyRef, Value](generators: Seq[Generator[Value]], keyFromType: Type => Key, maxCacheSize: Int)
  extends AbstractCachingGenerator[Key, Value](keyFromType, maxCacheSize) {

  override protected def canHandle(tc: TypeAndContext): Boolean = findGenerator(tc).isDefined

  override protected def findGenerator(tc: TypeAndContext): Option[Generator[Value]] = generators.find(_.isDefinedAt(tc))

}

private abstract class AbstractCachingGenerator[Key <: AnyRef, Value](keyFromType: Type => Key, maxCacheSize: Int) extends CachingGenerator[Key, Value] {

  val cache: SimpleCache[Key, Generator[Value]] = new SimpleCache[Key, Generator[Value]](maxCacheSize)
  
  override def isDefinedAt(tc: TypeAndContext): Boolean = {
    hasCached(tc) || canHandle(tc)
  }

  override def hasCached(tc: TypeAndContext): Boolean = {
    getCached(tc).isDefined
  }

  override def apply(tc: TypeAndContext): Value = {
    val cached = getCached(tc)
    if (cached.isDefined) {
      cached.get(tc)
    } else {
      val fn: Generator[Value] = findGenerator(tc)
        .getOrElse(throw new RuntimeException("Apply called on unsupported type, this should not happen."))
      cache.put(keyFromType(tc._1), fn)
      fn(tc)
    }
  }

  private def getCached(tc: TypeAndContext) = {
    cache.getIfPresent(keyFromType(tc._1))
  }

  protected def canHandle(tc: TypeAndContext): Boolean

  protected def findGenerator(tc: TypeAndContext): Option[Generator[Value]]
}
