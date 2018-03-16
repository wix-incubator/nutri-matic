package com.wixpress

import com.wixpress.nutrimatic.internal.{AssignableErasureMatchingGenerator, InternalRandomBuilder, TypeEqualityMatchingGenerator}

import scala.reflect.runtime.universe._

package object nutrimatic {
  type TypeAndContext = (Type, Context)
  type Generator[T] = PartialFunction[TypeAndContext, T]
  type GeneratorGenerator[T] = PartialFunction[TypeAndContext, Generator[T]]

  trait ByErasure[T] extends Generator[T]

  trait ByTypeEquality[T] extends Generator[T]

  trait Random {
    def random[T](implicit tag: TypeTag[T]): T
  }

  trait Context extends BasicGenerators {

    def random(t: Type, addFragment: String): Any

    def random(t: Type): Any = random(t, t.typeSymbol.name.toString)
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

    def build: nutrimatic.Random
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


