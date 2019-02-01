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

package com.wix.nutrimaticdocs

import org.specs2.matcher.Matcher
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class ExamplesTest extends SpecificationWithJUnit {
  "Basic usage example" in validThat {
    import com.wix.nutrimatic.NutriMatic

    NutriMatic.makeA[Option[String]]
  }(beAnInstanceOf[Option[String]])


  "Configured instance example" in validThat {
    import com.wix.nutrimatic.NutriMatic
    
    val nutriMatic = NutriMatic.builder // the arguments here are the default values
      .withCollectionSizes(0, 3) // created collections and arrays will have a size between these numbers (inclusive)
      .withStringLengths(1, 20) // created strings will have a length between these numbers (inclusive)
      .withCustomGenerators() // use these custom generators
      .withSeed(scala.util.Random.nextLong()) // seed used for getting random instances
      .withMaxCacheSize(10000) // this library relies on caching quite a bit, so if you run into memory issues, try reducing this value
      .build

    nutriMatic.makeA[Option[String]]
  }(beAnInstanceOf[Option[String]])

  "Extension exact type" in valid {
    import com.wix.nutrimatic.{Generators, NutriMatic}

    val getRandomNumber = Generators.byExactType[Int] { _ => 4}

    val nutriMatic = NutriMatic.builder
      .withCustomGenerators(getRandomNumber)
      .build

    nutriMatic.makeA[Int] must_== 4
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
