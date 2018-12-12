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

package com.wix.nutrimatic

import com.wix.nutrimatic.NutriMatic.builder
import com.wix.nutrimatic.NutriMatic.default.{makeA => defaultMakeA}
import com.wix.nutrimatic.samples._
import com.wix.nutrimatic.samples.tags._
import org.specs2.control.Debug
import org.specs2.matcher.ValueCheck
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

import scala.reflect.runtime.universe

class NutriMaticTest extends SpecificationWithJUnit with Debug {

  "nutri-matic strings" should {
    "be supported" in new Scope {
      defaultMakeA[String] must beAnInstanceOf[String]
    }

    "have only ascii letters and numbers by default" in new Scope {
      private val str: String = defaultMakeA[String]
      str must beMatching("^[a-zA-Z0-9]{1,20}$")
    }

    "have only letters and numbers by default" in new Scope {
      defaultMakeA[String] must beMatching("^[\\p{L},0-9]{1,20}$")
    }

    "support tweaking of string length" in new Scope {
      val instance = builder.withStringLengths(10, 12).build
      repeatedRuns(instance.makeA[String]) must contain(allOf(haveLength[String](10), haveLength[String](11), haveLength[String](12)))
    }

    "support full utf mode" in new Scope {
      val instance = builder.withAllCharacters.build
      instance.makeA[String].replaceAll("[a-zA-Z0-9]", "") must not(beEmpty)
    }
  }

  "nutri-matic primitives" should {
    "be supported" in new Scope {
      defaultMakeA[Byte] must beBetween(Byte.MinValue, Byte.MaxValue)
      defaultMakeA[Short] must beBetween(Short.MinValue, Short.MaxValue)
      defaultMakeA[Char] must beBetween(Char.MinValue, Char.MaxValue)
      defaultMakeA[Int] must beBetween(Int.MinValue, Int.MaxValue)
      defaultMakeA[Long] must beBetween(Long.MinValue, Long.MaxValue)
      defaultMakeA[Float] must beBetween(Float.MinValue, Float.MaxValue)
      defaultMakeA[Double] must beBetween(Double.MinValue, Double.MaxValue)
      defaultMakeA[Boolean] must beOneOf(true, false)
    }

    "return both true and false for booleans" in new Scope {
      repeatedRuns(defaultMakeA[Boolean]) must contain(true, false)
    }
  }

  "nutri-matic boxed java primitives" should {
    "be supported" in new Scope {
      defaultMakeA[java.lang.Byte] must beAnInstanceOf[java.lang.Byte]
      defaultMakeA[java.lang.Short] must beAnInstanceOf[java.lang.Short]
      defaultMakeA[java.lang.Character] must beAnInstanceOf[java.lang.Character]
      defaultMakeA[java.lang.Integer] must beAnInstanceOf[java.lang.Integer]
      defaultMakeA[java.lang.Long] must beAnInstanceOf[java.lang.Long]
      defaultMakeA[java.lang.Float] must beAnInstanceOf[java.lang.Float]
      defaultMakeA[java.lang.Double] must beAnInstanceOf[java.lang.Double]
      defaultMakeA[java.lang.Boolean] must beAnInstanceOf[java.lang.Boolean]
    }

    "return both true and false for booleans" in new Scope {
      repeatedRuns(defaultMakeA[Boolean]) must contain(true, false)
    }
  }

  "nutri-matic enums" should {
    "be supported and multiple enums not be confused" in new Scope {
      defaultMakeA[Enum1.Value] must beOneOf(Enum1.values.toSeq: _*)
      defaultMakeA[Enum2.Value] must beOneOf(Enum2.values.toSeq: _*)
    }

    "return all values for enums if called enough times" in new Scope {
      repeatedRuns(defaultMakeA[Enum1.Value]) must containAllOf(Enum1.values.toSeq)
    }
  }

  "nutri-matic case classes" should {
    "beSupported" in new Scope {
      val Foo(foo, bar) = defaultMakeA[Foo]
      foo must beBetween(Int.MinValue, Int.MaxValue)
      bar must beAnInstanceOf[String]
    }

    "return different values for different invocations" in new Scope {
      defaultMakeA[Foo] must not(beEqualTo(defaultMakeA[Foo]))
    }
  }

  "nutri-matic monads" should {
    "support scala.Option and return both None and Some" in new Scope {
      val expectedSome: ValueCheck[Option[String]] = beSome(beAnInstanceOf[String])
      val expectedNone: ValueCheck[Option[String]] = beNone

      repeatedRuns(defaultMakeA[Option[String]]) must contain(allOf(expectedSome, expectedNone))
    }

    "support scala.Either and return both Left and Right" in new Scope {
      val expectedRight: ValueCheck[Either[SealedTrait, String]] = beRight(beAnInstanceOf[String])
      val expectedLeft: ValueCheck[Either[SealedTrait, String]] = beLeft(beAnInstanceOf[SealedTrait])

      repeatedRuns(defaultMakeA[Either[SealedTrait, String]]) must contain(allOf(expectedLeft, expectedRight))
    }
  }

  "nutri-matic sealed traits" should {
    "should support sealed traits" in new Scope {
      defaultMakeA[SealedTrait] must beLike {
        case Case1(e) if Enum1.values.contains(e) => ok
        case Case2() => ok
        case _ => ko
      }
    }
    "should generate each of the subclasses of sealed classes" in new Scope {
      repeatedRuns(defaultMakeA[SealedTrait]) must contain(allOf(beAnInstanceOf[Case1], beAnInstanceOf[Case2]))
    }
  }

  "nutri-matic collections" should {
    "support Seq with default size" in new Scope {
      defaultMakeA[Seq[SealedTrait]] must beAnInstanceOf[Seq[SealedTrait]] and
        contain(beAnInstanceOf[SealedTrait]).forall and
        haveSize[Seq[SealedTrait]](3)
    }

    "support List with default size" in new Scope {
      defaultMakeA[List[SealedTrait]] must beAnInstanceOf[List[SealedTrait]] and
        contain(beAnInstanceOf[SealedTrait]).forall and
        haveSize[List[SealedTrait]](3)
    }

    "support Set with default size" in new Scope {
      defaultMakeA[Set[SealedTrait]] must beAnInstanceOf[Set[SealedTrait]] and
        contain(beAnInstanceOf[SealedTrait]).forall and
        haveSize[Set[SealedTrait]](3)
    }

    "support Array with default size" in new Scope {
      val r = defaultMakeA[Array[Byte]]
      r must beAnInstanceOf[Array[Byte]] and
        haveSize[Array[Byte]](3)
    }

    "support Map with default size" in new Scope {
      defaultMakeA[Map[Int, SealedTrait]] must beAnInstanceOf[Map[Int, SealedTrait]] and
        contain(beAnInstanceOf[(Int, SealedTrait)]).forall and
        haveSize[Map[Int, SealedTrait]](3)
    }

    "support overriding size for all collections" in new Scope {
      val instance = builder.withCollectionSizes(0, 2).build
      repeatedRuns(instance.makeA[Seq[String]]) must contain(
        allOf(haveSize[Seq[String]](0), haveSize[Seq[String]](1), haveSize[Seq[String]](2)))

      repeatedRuns(instance.makeA[List[String]]) must contain(
        allOf(haveSize[List[String]](0), haveSize[List[String]](1), haveSize[List[String]](2)))

      repeatedRuns(instance.makeA[Set[String]]) must contain(
        allOf(haveSize[Set[String]](0), haveSize[Set[String]](1), haveSize[Set[String]](2)))

      repeatedRuns(instance.makeA[Map[Int, String]]) must contain(
        allOf(haveSize[Map[Int, String]](0), haveSize[Map[Int, String]](1), haveSize[Map[Int, String]](2)))
    }
  }


  "should support concrete generics with all concrete parameters" in new Scope {
    val r = defaultMakeA[GenericFoo[Enum1.Value]]
    r.bar must beOneOf(Enum1.values.toSeq: _*)
    r.baz must beAnInstanceOf[String]
  }

  "should support regular classes" in new Scope {
    val r: RegularClass = defaultMakeA[RegularClass]
    r must beAnInstanceOf[RegularClass]
    r.bazbaz must beAnInstanceOf[String]
  }

  "should not generate abstract classes" in new Scope {
    defaultMakeA[AbstractClass] must throwA[FailedToGenerateValue].like {
      case e: Throwable => e.getMessage must contain("AbstractClass")
    }
  }

  "should fail to build regular traits and report path" in new Scope {
    NutriMatic.builder.withCollectionSizes(3, 3).build.makeA[Seq[GenericFoo[RegularTrait]]] must throwA[FailedToGenerateValue].like {
      case e: Throwable => e.getMessage must
        contain("Seq[com.wix.nutrimatic.samples.GenericFoo[com.wix.nutrimatic.samples.RegularTrait]]") and
        contain("Seq/GenericFoo/bar")
    }
  }

  "should fail on Nothing" in new Scope {
    repeatedRuns(defaultMakeA) must throwA[FailedToGenerateValue].like {
      case e: Throwable => e.getMessage must contain("Error generating an instance of Nothing")
    }
  }

  "should be repeatable if new instance with the same seed is used" in new Scope {
    val seed = 1234
    val instance1 = NutriMatic.builder.withSeed(seed).build
    val instance2 = NutriMatic.builder.withSeed(seed).build

    repeatedRuns(instance1.makeA[Int]) must_== repeatedRuns(instance2.makeA[Int])
  }

  "should support multiple constructor argument lists" in new Scope {
    defaultMakeA[MultipleArgListsConstructor] must beAnInstanceOf[MultipleArgListsConstructor]
  }
  
  "should support case objects" in new Scope {
    defaultMakeA[CaseObject.type] must_=== defaultMakeA[CaseObject.type]

    private def beEitherObjectOrInstanceOfCaseClass =
      (_: SealedTraitWithCaseObject) must (be_==(CaseObject) or be_==(CaseClass()))
    
    repeatedRuns(defaultMakeA[SealedTraitWithCaseObject]) must beEitherObjectOrInstanceOfCaseClass.forall
  }

  "should support tagged types" >> {
    "with custom generators for each" in new Scope {
      val Douglas = "Douglas"
      val Adams = "Adams"
      
      val instance = NutriMatic.builder.withCustomGenerators(
        Generators.byExactType[FirstName](_ => tags.firstName(Douglas)),
        Generators.byExactType[LastName](_ => tags.lastName(Adams)),
        Generators.byExactType[Age](_ => tags.age(67).right.get)
      ).build

      val person = instance.makeA[Person]

      person.firstName must_== Douglas
      person.lastName must_== Adams
      person.age must_== 67
      person.motto must !==(Adams) and !==(Douglas)
    }

    "with fallback for unknown tagged types" in new Scope {
      import scala.reflect.runtime.universe._
      private val taggedGenerator: Generator[@@[_, _]] = {
        case (t: universe.Type, r: Context) if t <:< weakTypeOf[Tagged[_]] =>
          // this won't work with tagged types that have generic arguments (eg List[X] with Tag[...]), as those get 
          // erased as well. But since tagging is usually used for primitives, this is a reasonable implementation
          r.makeComponent(t.erasure).asInstanceOf[@@[_, _]]
      }
      
      val instance = NutriMatic.builder.withCustomGenerators(
        Generators.byExactType[FirstName](_ => tags.firstName("Douglas")),
        taggedGenerator
      ).build

      val person = instance.makeA[Person]

      person.firstName must_== "Douglas"
      person.lastName must beAnInstanceOf[String] and !==("Douglas")
      person.age must be_>=(Short.MinValue) and be_<=(Short.MaxValue)
      person.motto must !==("Adams") and !==("Douglas")
    }
  }
  
  def repeatedRuns[T](t: => T): Seq[T] = Range(0, 50).map((_) => t)
}
