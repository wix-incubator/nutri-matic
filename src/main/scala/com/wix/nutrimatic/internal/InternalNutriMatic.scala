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
import com.wix.nutrimatic.internal.generators._

import scala.reflect.runtime.universe._

private[nutrimatic] class InternalNutriMatic(additionalByErasure: Seq[ByErasure[_]],
                                             additionalCustom: Seq[Generator[_]],
                                             val randomValues: RandomValues,
                                             maxSizePerCache: Int) extends NutriMatic {

  private val fail: Generator[Nothing] = {
    case (t, context: InternalContext) =>
      val basicMessage = s"Error generating an instance of ${context.root}."
      if (context.root == t) {
        throw FailedToGenerateValue(basicMessage)
      } else {
        throw FailedToGenerateValue(s"$basicMessage Failed to generate an instance of type $t at ${context.fragments.mkString("/")}")
      }
    case (t, _) => throw FailedToGenerateValue(s"Error generating an instance of $t.")
    case arg => throw new RuntimeException(s"Unexpected argument $arg")
  }

  private val failOnNothing = Generators.byExactType[Nothing](context => fail((typeOf[Nothing], context)))

  private val basicGenerators = CachingGenerator[Any](
    generators = Seq(failOnNothing)
      ++ additionalCustom
      ++ Primitives.generators
      ++ BoxedJavaPrimitives.generators,
    maxCacheSize = maxSizePerCache)

  private val erasureGenerators = CachingGenerator[Any](
    generators = additionalByErasure
      ++ Monads.generators
      ++ Collections.generators,
    keyFromType = AssignableErasureMatchingGenerator.cacheKeyFromType,
    maxCacheSize = maxSizePerCache
  )
  private val reflectiveGenerators = CachingGenerator.fromGeneratorsOfGenerators(
    generatorGenerators = Seq(
      Arrays,
      Enums,
      JavaEnums,
      Objects,
      SealedTraits,
      ClassesWithConstructors
    ),
    maxCacheSize = maxSizePerCache)

  private val generatorsWithCaching = 
    basicGenerators orElseCached
    erasureGenerators orElseCached
    reflectiveGenerators

  private val generatorChain = generatorsWithCaching.onlyIfCached orElse generatorsWithCaching orElse fail

  override def makeA[T](implicit tag: TypeTag[T]): T = synchronized { // we need a lock because of https://github.com/scala/bug/issues/10766
    val tpe = tag.tpe
    val value = InternalContext(this, tpe).makeComponent(tpe)
    value.asInstanceOf[T]
  }

  private[nutrimatic] def randomWithContext(t: Type, context: Context): Any = {
    generatorChain((t.dealias, context))
  }
}

private case class InternalContext(factory: InternalNutriMatic, root: Type, fragments: Seq[String] = Seq.empty) extends Context {

  private val p = factory.randomValues

  override def makeComponent(t: Type, addFragment: String): Any = {
    val deeper = copy(fragments = fragments :+ addFragment)
    factory.randomWithContext(t, deeper)
  }

  override def randomStr: String = p.randomStr

  override def randomInt: Int = p.randomInt

  override def randomInt(from: Int, to: Int): Int = p.randomInt(from, to)

  override def randomLong: Long = p.randomLong

  override def randomDouble: Double = p.randomDouble

  override def randomBoolean: Boolean = p.randomBoolean

  override def randomCollection[T](generator: => T): Seq[T] = p.randomCollection(generator)
}



