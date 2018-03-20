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

package com.wix

import com.wix.nutrimatic.internal.{AssignableErasureMatchingGenerator, InternalNutrimaticBuilder, TypeEqualityMatchingGenerator}

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

package object nutrimatic {
  type TypeAndContext = (Type, Context)
  type Generator[+T] = PartialFunction[TypeAndContext, T]
  type GeneratorGenerator[+T] = PartialFunction[TypeAndContext, Generator[T]]

  trait ByErasure[+T] extends Generator[T]

  trait ByTypeEquality[+T] extends Generator[T]

  trait NutriMatic {
    def makeA[T](implicit tag: TypeTag[T]): T
    
    def makeAn[T](implicit tag: TypeTag[T]) = makeA
  }

  trait Context extends RandomValues {

    def makeComponent(t: Type, addFragment: String): Any

    def makeComponent(t: Type): Any = makeComponent(t, t.typeSymbol.name.toString)
  }

  trait RandomValues {

    def randomStr: String

    def randomInt: Int

    def randomInt(from: Int, to: Int): Int

    def randomLong: Long

    def randomDouble: Double

    def randomBoolean: Boolean

    def randomCollection[T](generator: => T): Seq[T]
  }

  trait NutrimaticBuilder {
    def withCustomGenerators[T <: Any](generators: Generator[T]*): NutrimaticBuilder

    def withCollectionSizes(from: Int, to: Int): NutrimaticBuilder

    def withStringLengths(from: Int, to: Int): NutrimaticBuilder

    def withOnlyAsciiCharacters: NutrimaticBuilder

    def withAllCharacters: NutrimaticBuilder

    def withSeed(seed: Long): NutrimaticBuilder

    def withMaxCacheSize(size: Int): NutrimaticBuilder

    def build: nutrimatic.NutriMatic
  }

  case class FailedToGenerateValue(message: String) extends Exception(message)

  object Generators {
    def byExactType[T](valueFn: Context => T)(implicit t: TypeTag[T]): Generator[T] = new TypeEqualityMatchingGenerator(t) {
      override protected def getValue(context: Context): T = valueFn(context)
    }

    def byErasure[T](valueFn: (Type, Context) => T)(implicit t: WeakTypeTag[T]): ByErasure[T] = new AssignableErasureMatchingGenerator(t) {
      override protected def getValue(t: Type, context: Context): T = valueFn(t, context)
    }
  }

  object NutriMatic extends NutriMatic {
    val default: NutriMatic = builder.build

    def builder: NutrimaticBuilder = InternalNutrimaticBuilder()

    override def makeA[T](implicit tag: universe.TypeTag[T]): T = default.makeA
  }

}


