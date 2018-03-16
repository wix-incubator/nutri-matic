package com.wixpress.nutrimatic.internal

import com.wixpress.nutrimatic._
import com.wixpress.nutrimatic.internal.generators._

import scala.reflect.runtime.universe._

private[nutrimatic] class InternalNutriMatic(additionalByTypeEquality: Seq[ByTypeEquality[_]],
                                             additionalByErasure: Seq[ByErasure[_]],
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
      ++ additionalByTypeEquality
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
      SealedTraits,
      ClassesWithConstructors
    ),
    maxCacheSize = maxSizePerCache)

  private val generatorsWithCaching = {
    val basicChain = basicGenerators orElseCached
      erasureGenerators orElseCached
      reflectiveGenerators
    if (additionalCustom.isEmpty) {
      basicChain
    } else {
      basicChain orElseCached CachingGenerator[Any](additionalCustom, maxCacheSize = maxSizePerCache)
    }
  }

  private val generatorChain = generatorsWithCaching.onlyIfCached orElse generatorsWithCaching orElse fail

  override def makeA[T](implicit tag: TypeTag[T]): T = {
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



