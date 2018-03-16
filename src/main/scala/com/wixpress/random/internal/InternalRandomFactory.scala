package com.wixpress.random.internal

import com.wixpress.random._
import com.wixpress.random.internal.generators._

import scala.reflect.runtime.universe._

private[random] class InternalRandomFactory(additionalByTypeEquality: Seq[ByTypeEquality[_]],
                                            additionalByErasure: Seq[ByErasure[_]],
                                            additionalCustom: Seq[Generator[_]],
                                            val primitiveGenerators: BasicGenerators,
                                            maxSizePerCache: Int) extends Random {

  private val fail: Generator[Nothing] = {
    case (t, context: InternalContext) =>
      val basicMessage = s"Error generating an instance of ${context.root}."
      if (context.root == t) {
        throw FailedToGenerateRandomValue(basicMessage)
      } else {
        throw FailedToGenerateRandomValue(s"$basicMessage Failed to generate an instance of type $t at ${context.fragments.mkString("/")}")
      }
    case (t, _) => throw FailedToGenerateRandomValue(s"Error generating an instance of $t.")
  }

  private val failOnNothing = Generators.byExactType[Nothing](context => fail((typeOf[Nothing], context)))

  private val basicTypes = CachingGenerator[Any](
    generators = Seq(failOnNothing)
      ++ additionalByTypeEquality
      ++ Primitives.generators
      ++ BoxedJavaPrimitives.generators,
    maxCacheSize = maxSizePerCache)

  private val erasureTypes = CachingGenerator[Any](
    generators = additionalByErasure
      ++ Monads.generators
      ++ Collections.generators,
    keyFromType = AssignableErasureMatchingGenerator.cacheKeyFromType,
    maxCacheSize = maxSizePerCache
  )
  private val reflectiveGenerators = CachingGenerator.fromGeneratorsOfGenerators(
    generatorGenerators = Seq(
      Arrays,
      RandomEnums,
      CaseClasses,
      SealedTraits
    ),
    maxCacheSize = maxSizePerCache)

  private val generatorsWithCaching = {
    val basicChain = basicTypes orElseCached
      erasureTypes orElseCached
      reflectiveGenerators
    if (additionalCustom.isEmpty) {
      basicChain
    } else {
      basicChain orElseCached CachingGenerator[Any](additionalCustom, maxCacheSize = maxSizePerCache)
    }
  }

  private val generatorChain = generatorsWithCaching.onlyIfCached orElse generatorsWithCaching orElse fail


  override def random[T](implicit tag: TypeTag[T]): T = {
    val tpe = tag.tpe
    val value = InternalContext(this, tpe).random(tpe)
    value.asInstanceOf[T]
  }

  private[random] def randomWithContext(t: Type, context: Context): Any = {
    generatorChain((t.dealias, context))
  }
}

private case class InternalContext(factory: InternalRandomFactory, root: Type, fragments: Seq[String] = Seq.empty) extends Context {

  override val basic: BasicGenerators = factory.primitiveGenerators

  override def random(t: Type, addFragment: String): Any = {
    val deeper = copy(fragments = fragments :+ addFragment)
    factory.randomWithContext(t, deeper)
  }
}



