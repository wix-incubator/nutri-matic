package com.wix.nutrimaticdocs

import com.wix.nutrimatic.Context
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

    val getRandomNumber = Generators.byExactType[Int] { _ => 4 } // https://xkcd.com/221/

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

  "Extension exact type" in valid {
    import com.wix.nutrimatic.{Generators, NutriMatic}

    val getRandomNumber = Generators.byExactType[Int] {
      context: Context => context.randomInt(0, 5) // context gives access to functions to make other values
    }

    val nutriMatic = NutriMatic.builder
      .withCustomGenerators(getRandomNumber)
      .build

    nutriMatic.makeA[Int] must beBetween(0, 5)
  }

  "Extension erasure" in valid {
    import com.wix.nutrimatic.{Generators, NutriMatic}

    val makeSureWeKnow = Generators.byErasure[Option[_]] {
      case (tpe, context) => Some(context.makeComponent(tpe.typeArgs.head))
    }

    val theAnswerToLifeTheUniverseAndEverything = Generators.byExactType[Int] { _ => 42 }

    val nutriMatic = NutriMatic.builder
      .withCustomGenerators(theAnswerToLifeTheUniverseAndEverything, makeSureWeKnow)
      .build

    nutriMatic.makeA[Option[Int]] must beSome(42)
  }
  
  "Extension custom" in valid {
    import com.wix.nutrimatic.{Generator, NutriMatic}

    val typesStartingWithSAreAlwaysNull: Generator[Any] = {
      case (tpe, context) if tpe.toString.startsWith("S") => null
    }
    
    val nutriMatic = NutriMatic.builder
      .withCustomGenerators(typesStartingWithSAreAlwaysNull)
      .build

    nutriMatic.makeA[String] must beNull
    nutriMatic.makeA[Seq[Int]] must beNull
    nutriMatic.makeA[Option[Int]] must not(beNull)
  }


  def validThat[T](t: => T)(matcher: Matcher[T]): Scope = new Scope {
    t must matcher
  }

  def valid[T](t: => T): Scope = new Scope {
    t
  }
}
