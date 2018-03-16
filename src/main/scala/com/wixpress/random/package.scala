package com.wixpress

import com.wixpress.random.internal.InternalRandomBuilder
import com.wixpress.random.internal.generators.{TypeEqualityMatchingGenerator, AssignableErasureMatchingGenerator}

import scala.reflect.runtime.universe._

package object random {
  type TypeAndRandom = (Type, Context)
  type Generator[T] = PartialFunction[TypeAndRandom, T]
  type GeneratorGenerator[T] = PartialFunction[TypeAndRandom, Generator[T]]

  trait ByErasure[T] extends Generator[T]

  trait ByTypeEquality[T] extends Generator[T]

  trait Context {
    val basic: BasicGenerators

    def random(t: Type, addFragment: String): Any

    def random(t: Type): Any = random(t, t.typeSymbol.name.toString)
  }

  trait Random {
    def random[T](implicit tag: TypeTag[T]): T
  }

  trait BasicGenerators {
    def randomStr: String

    def randomInt: Int

    def randomInt(from: Int, to: Int): Int

    def randomLong: Long

    def randomDouble: Double

    def randomBoolean: Boolean

    def randomCollection[T](generator: => T): Seq[T]
  }

  trait RandomBuilder {
    def withCustomGenerators(generators: Generator[_]*): RandomBuilder

    def withCollectionSizes(from: Int, to: Int): RandomBuilder

    def withStringLengths(from: Int, to: Int): RandomBuilder

    def withOnlyAsciiCharacters: RandomBuilder

    def withAllCharacters: RandomBuilder

    def withSeed(seed: Long): RandomBuilder

    def withMaxCacheSize(size: Int): RandomBuilder
    
    def build: random.Random
  }

  case class FailedToGenerateRandomValue(message: String) extends Exception(message)

  object Generators {
    def byExactType[T](valueFn: Context => T)(implicit t: TypeTag[T]): ByTypeEquality[T] = new TypeEqualityMatchingGenerator(t) {
      override protected def getValue(context: Context): T = valueFn(context)
    }

    def byErasure[T](valueFn: (Type, Context) => T)(implicit t: WeakTypeTag[T]): ByErasure[T] = new AssignableErasureMatchingGenerator(t) {
      override protected def getValue(t: Type, context: Context): T = valueFn(t, context)
    }
  }

  object Random {
    val default: Random = builder.build

    def builder: RandomBuilder = InternalRandomBuilder()
  }

}


