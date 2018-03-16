package com.wix.nutrimaticdocs

import org.specs2.matcher.Matcher
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class ExamplesTest extends SpecificationWithJUnit {
  "Basic usage example" in validThat {
    import com.wix.nutrimatic.NutriMatic

    NutriMatic.makeA[Option[String]]
  }(beAnInstanceOf[Option[String]])


  "Configured instance example" in valid {
    import com.wix.nutrimatic.{Generators, NutriMatic}

    val getRandomNumber = Generators.byExactType[Int]((_) => 4) // https://xkcd.com/221/

    val nutriMatic = NutriMatic.builder // the arguments here are the default values
      .withAllCharacters // created strings will include any characters
      .withOnlyAsciiCharacters // created strings will include only ascii letters and numbers (default)
      .withCollectionSizes(0, 3) // created collections and arrays will have a size between these numbers (inclusive)
      .withStringLengths(1, 20) // created strings will have a length between these numbers (inclusive)
      .withCustomGenerators(getRandomNumber) // use these custom generators
      .withSeed(scala.util.Random.nextLong()) // seed used for getting random instances
      .withMaxCacheSize(10000) // this library relies on caching quite a bit, so if you run into memory issues, try reducing this value
      .build

    nutriMatic.makeA[Int] must_== 4
  }


  def validThat[T](t: => T)(matcher: Matcher[T]): Scope = new Scope {
    t must matcher
  }

  def valid[T](t: => T): Scope = new Scope {
    t
  }
}
