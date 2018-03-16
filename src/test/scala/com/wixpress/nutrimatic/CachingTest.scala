package com.wixpress.nutrimatic

import com.wixpress.common.specs2.JMock
import com.wixpress.nutrimatic.internal.CachingGenerator
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

import scala.reflect.runtime.universe._

class CachingTest extends SpecificationWithJUnit with JMock {

  "is defined at should delegate to inner generator's isDefinedAt" in new CachingScope {
    checking {
      oneOf(firstGenerator).isDefinedAt(ts) willReturn true
    }
    cachingGenerator.isDefinedAt(ts) must beTrue
  }

  "is defined at should fall through" in new CachingScope {
    checking {
      oneOf(firstGenerator).isDefinedAt(ts) willReturn false
      oneOf(secondGenerator).isDefinedAt(ts) willReturn true
    }
    cachingGenerator.isDefinedAt(ts) must beTrue
  }

  "applying the function should make the generator selection cached and lookup should be skipped" in new CachingScope {
    checking {
      oneOf(firstGenerator).isDefinedAt(ts) willReturn false
      oneOf(secondGenerator).isDefinedAt(ts) willReturn true
      oneOf(secondGenerator).apply(ts) willReturn 123
      oneOf(secondGenerator).apply(ts) willReturn 234
    }
    cachingGenerator.apply(ts) must_== 123
    cachingGenerator.apply(ts) must_== 234
  }

  "function returned by calling onlyIfCached should only be defined if cached" in new CachingScope {
    checking {
      oneOf(firstGenerator).isDefinedAt(ts) willReturn true
      oneOf(firstGenerator).apply(ts) willReturn 123
    }
    val transformed = cachingGenerator.onlyIfCached
    transformed.isDefinedAt(ts) must beFalse
    cachingGenerator.apply(ts) must_== 123
    transformed.isDefinedAt(ts) must beTrue
  }

  "function returned by calling orElseCached should combine functions and cache lookups" in new CachingScope {
    checking {
      oneOf(firstGenerator).isDefinedAt(ts) willReturn false
      oneOf(secondGenerator).isDefinedAt(ts) willReturn false
      oneOf(thirdGenerator).isDefinedAt(ts) willReturn true
      oneOf(thirdGenerator).apply(ts) willReturn 123
      oneOf(thirdGenerator).apply(ts) willReturn 234
    }
    val transformed = cachingGenerator orElseCached otherCachingGenerator
    transformed.onlyIfCached.isDefinedAt(ts) must beFalse
    transformed.apply(ts) must_== 123
    transformed.onlyIfCached.isDefinedAt(ts) must beTrue
    transformed.apply(ts) must_== 234
  }

  "fromGeneratorGenerators should call the results of generator generator and call cached generator on apply" in new GeneratorCachingScope {
    checking {
      oneOf(firstGenerator).isDefinedAt(ts) willReturn false
      oneOf(secondGenerator).isDefinedAt(ts) willReturn true
      oneOf(secondGenerator).apply(ts) willReturn returnedGenerator
      oneOf(returnedGenerator).apply(ts) willReturn 123
      oneOf(returnedGenerator).apply(ts) willReturn 234
    }
    cachingGeneratorGenerator.onlyIfCached.isDefinedAt(ts) must beFalse
    cachingGeneratorGenerator.apply(ts) must_== 123
    cachingGeneratorGenerator.onlyIfCached.isDefinedAt(ts) must beTrue
    cachingGeneratorGenerator.apply(ts) must_== 234
  }


  trait CachingScope extends Scope {
    val context = mock[Context]
    val ts = (typeOf[Int], context)
    val firstGenerator = mock[Generator[Int]]("first")
    val secondGenerator = mock[Generator[Int]]("second")
    val thirdGenerator = mock[Generator[Int]]("third")

    val cachingGenerator = CachingGenerator(Seq(firstGenerator, secondGenerator), maxCacheSize = 10)
    val otherCachingGenerator = CachingGenerator(Seq(thirdGenerator), maxCacheSize = 10)

  }

  trait GeneratorCachingScope extends Scope {
    val context = mock[Context]
    val ts = (typeOf[Int], context)
    val firstGenerator = mock[Generator[Generator[Int]]]("first")
    val secondGenerator = mock[Generator[Generator[Int]]]("second")
    val returnedGenerator = mock[Generator[Int]]("generated")

    val cachingGeneratorGenerator = CachingGenerator.fromGeneratorsOfGenerators(Seq(firstGenerator, secondGenerator), maxCacheSize = 10)

  }

}
