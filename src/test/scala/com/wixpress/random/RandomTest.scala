package com.wixpress.random

import com.wixpress.random.Random.builder
import com.wixpress.random.Random.default.{random => defaultRandom}
import com.wixpress.random.samples._
import org.specs2.control.Debug
import org.specs2.matcher.ValueCheck
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class RandomTest extends SpecificationWithJUnit with Debug {

  "random strings" should {
    "be supported" in new Scope {
      defaultRandom[String] must beAnInstanceOf[String]
    }

    "have default length of 20" in new Scope {
      defaultRandom[String] must haveLength[String](20)
    }

    "have only ascii letters and numbers by default" in new Scope {
      private val str: String = defaultRandom[String]
      str must beMatching("^[a-zA-Z0-9]{20}$")
    }

    "have only letters and numbers by default" in new Scope {
      defaultRandom[String] must beMatching("^[\\p{L},0-9]{20}$")
    }

    "support tweaking of string length" in new Scope {
      val instance = builder.withStringLengths(10, 12).build
      repeatedRuns(instance.random[String]) must contain(allOf(haveLength[String](10), haveLength[String](11), haveLength[String](12)))
    }

    "support full utf mode" in new Scope {
      val instance = builder.withAllCharacters.build
      instance.random[String].replaceAll("[a-zA-Z0-9]", "") must not(beEmpty)
    }
  }

  "random primitives" should {
    "be supported" in new Scope {
      defaultRandom[Byte] must beBetween(Byte.MinValue, Byte.MaxValue)
      defaultRandom[Short] must beBetween(Short.MinValue, Short.MaxValue)
      defaultRandom[Char] must beBetween(Char.MinValue, Char.MaxValue)
      defaultRandom[Int] must beBetween(Int.MinValue, Int.MaxValue)
      defaultRandom[Long] must beBetween(Long.MinValue, Long.MaxValue)
      defaultRandom[Float] must beBetween(Float.MinValue, Float.MaxValue)
      defaultRandom[Double] must beBetween(Double.MinValue, Double.MaxValue)
      defaultRandom[Boolean] must beOneOf(true, false)
    }

    "return both true and false for booleans" in new Scope {
      repeatedRuns(defaultRandom[Boolean]) must contain(true, false)
    }
  }

  "random boxed java primitives" should {
    "be supported" in new Scope {
      defaultRandom[java.lang.Byte] must beAnInstanceOf[java.lang.Byte]
      defaultRandom[java.lang.Short] must beAnInstanceOf[java.lang.Short]
      defaultRandom[java.lang.Character] must beAnInstanceOf[java.lang.Character]
      defaultRandom[java.lang.Integer] must beAnInstanceOf[java.lang.Integer]
      defaultRandom[java.lang.Long] must beAnInstanceOf[java.lang.Long]
      defaultRandom[java.lang.Float] must beAnInstanceOf[java.lang.Float]
      defaultRandom[java.lang.Double] must beAnInstanceOf[java.lang.Double]
      defaultRandom[java.lang.Boolean] must beAnInstanceOf[java.lang.Boolean]
    }

    "return both true and false for booleans" in new Scope {
      repeatedRuns(defaultRandom[Boolean]) must contain(true, false)
    }
  }

  "random enums" should {
    "be supported and multiple enums not be confused" in new Scope {
      defaultRandom[Enum1.Value] must beOneOf(Enum1.values.toSeq: _*)
      defaultRandom[Enum2.Value] must beOneOf(Enum2.values.toSeq: _*)
    }

    "return all values for enums if called enough times" in new Scope {
      repeatedRuns(defaultRandom[Enum1.Value]) must containAllOf(Enum1.values.toSeq)
    }
  }

  "random case classes" should {
    "beSupported" in new Scope {
      val Foo(foo, bar) = defaultRandom[Foo]
      foo must beBetween(Int.MinValue, Int.MaxValue)
      bar must beAnInstanceOf[String]
    }

    "return different values for different invocations" in new Scope {
      defaultRandom[Foo] must not(beEqualTo(defaultRandom[Foo]))
    }
  }

  "random monads" should {
    "support scala.Option and return both None and Some" in new Scope {
      val expectedSome: ValueCheck[Option[String]] = beSome(beAnInstanceOf[String])
      val expectedNone: ValueCheck[Option[String]] = beNone

      repeatedRuns(defaultRandom[Option[String]]) must contain(allOf(expectedSome, expectedNone))
    }

    "support scala.Either and return both Left and Right" in new Scope {
      val expectedRight: ValueCheck[Either[SealedTrait, String]] = beRight(beAnInstanceOf[String])
      val expectedLeft: ValueCheck[Either[SealedTrait, String]] = beLeft(beAnInstanceOf[SealedTrait])

      repeatedRuns(defaultRandom[Either[SealedTrait, String]]) must contain(allOf(expectedLeft, expectedRight))
    }
  }

  "random sealed traits" should {
    "should support sealed traits" in new Scope {
      defaultRandom[SealedTrait] must beLike {
        case Case1(e) if Enum1.values.contains(e) => ok
        case Case2() => ok
        case _ => ko
      }
    }
    "should generate each of the subclasses of sealed classes" in new Scope {
      repeatedRuns(defaultRandom[SealedTrait]) must contain(allOf(beAnInstanceOf[Case1], beAnInstanceOf[Case2]))
    }
  }

  "random collections" should {
    "support Seq with default size" in new Scope {
      defaultRandom[Seq[SealedTrait]] must beAnInstanceOf[Seq[SealedTrait]] and
        contain(beAnInstanceOf[SealedTrait]).forall and
        haveSize[Seq[SealedTrait]](3)
    }

    "support List with default size" in new Scope {
      defaultRandom[List[SealedTrait]] must beAnInstanceOf[List[SealedTrait]] and
        contain(beAnInstanceOf[SealedTrait]).forall and
        haveSize[List[SealedTrait]](3)
    }

    "support Set with default size" in new Scope {
      defaultRandom[Set[SealedTrait]] must beAnInstanceOf[Set[SealedTrait]] and
        contain(beAnInstanceOf[SealedTrait]).forall and
        haveSize[Set[SealedTrait]](3)
    }

    "support Array with default size" in new Scope {
      val r = defaultRandom[Array[Byte]]
      r must beAnInstanceOf[Array[Byte]] and
        haveSize[Array[Byte]](3)
    }

    "support Map with default size" in new Scope {
      defaultRandom[Map[Int, SealedTrait]] must beAnInstanceOf[Map[Int, SealedTrait]] and
        contain(beAnInstanceOf[(Int, SealedTrait)]).forall and
        haveSize[Map[Int, SealedTrait]](3)
    }

    "support overriding size for all collections" in new Scope {
      val instance = builder.withCollectionSizes(0, 2).build
      repeatedRuns(instance.random[Seq[String]]) must contain(
        allOf(haveSize[Seq[String]](0), haveSize[Seq[String]](1), haveSize[Seq[String]](2)))

      repeatedRuns(instance.random[List[String]]) must contain(
        allOf(haveSize[List[String]](0), haveSize[List[String]](1), haveSize[List[String]](2)))

      repeatedRuns(instance.random[Set[String]]) must contain(
        allOf(haveSize[Set[String]](0), haveSize[Set[String]](1), haveSize[Set[String]](2)))

      repeatedRuns(instance.random[Map[Int, String]]) must contain(
        allOf(haveSize[Map[Int, String]](0), haveSize[Map[Int, String]](1), haveSize[Map[Int, String]](2)))
    }
  }


  "should support concrete generics with all concrete parameters" in new Scope {
    val r = defaultRandom[GenericFoo[Enum1.Value]]
    r.bar must beOneOf(Enum1.values.toSeq: _*)
    r.baz must beAnInstanceOf[String]
  }

  "should support regular classes" in new Scope {
    val r: RegularClass = defaultRandom[RegularClass]
    r must beAnInstanceOf[RegularClass]
    r.bazbaz must beAnInstanceOf[String]
  }

  "should not generate abstract classes" in new Scope {
    defaultRandom[AbstractClass] must throwA[FailedToGenerateRandomValue].like {
      case e: Throwable => e.getMessage must contain("AbstractClass")
    }
  }

  "should fail to build regular traits and report path" in new Scope {
    defaultRandom[Seq[GenericFoo[RegularTrait]]] must throwA[FailedToGenerateRandomValue].like {
      case e: Throwable => e.getMessage must
        contain("Seq[com.wixpress.random.samples.GenericFoo[com.wixpress.random.samples.RegularTrait]]") and
        contain("Seq/GenericFoo/bar")
    }
  }

  "should fail on Nothing" in new Scope {
    repeatedRuns(defaultRandom) must throwA[FailedToGenerateRandomValue].like {
      case e: Throwable => e.getMessage must contain("Error generating an instance of Nothing")
    }
  }

  "should be repeatable if new instance with the same seed is used" in new Scope {
    val seed = 1234
    val instance1 = Random.builder.withSeed(seed).build
    val instance2 = Random.builder.withSeed(seed).build

    repeatedRuns(instance1.random[Int]) must_== repeatedRuns(instance2.random[Int])
  }

  def repeatedRuns[T](t: => T): Seq[T] = Range(0, 50).map((_) => t)
}
