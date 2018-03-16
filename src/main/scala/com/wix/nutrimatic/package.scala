package com.wix

import com.wix.nutrimatic.internal.{AssignableErasureMatchingGenerator, InternalNutrimaticBuilder, TypeEqualityMatchingGenerator}

import scala.reflect.runtime.universe._

package object nutrimatic {
  type TypeAndContext = (Type, Context)
  type Generator[T] = PartialFunction[TypeAndContext, T]
  type GeneratorGenerator[T] = PartialFunction[TypeAndContext, Generator[T]]

  trait ByErasure[T] extends Generator[T]

  trait ByTypeEquality[T] extends Generator[T]

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
    def withCustomGenerators(generators: Generator[_]*): NutrimaticBuilder

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
    def byExactType[T](valueFn: Context => T)(implicit t: TypeTag[T]): ByTypeEquality[T] = new TypeEqualityMatchingGenerator(t) {
      override protected def getValue(context: Context): T = valueFn(context)
    }

    def byErasure[T](valueFn: (Type, Context) => T)(implicit t: WeakTypeTag[T]): ByErasure[T] = new AssignableErasureMatchingGenerator(t) {
      override protected def getValue(t: Type, context: Context): T = valueFn(t, context)
    }
  }

  object NutriMatic {
    val default: NutriMatic = builder.build

    def builder: NutrimaticBuilder = InternalNutrimaticBuilder()
  }

}


