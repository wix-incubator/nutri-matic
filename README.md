# Nutri-Matic

> He had found a Nutri-Matic machine which had provided him with a plastic cup filled with a liquid that was almost, but not quite, entirely unlike tea
>
> &mdash; <cite> Douglas Adams, The Hitchhiker's Guide to the Galaxy

[![Build Status](https://travis-ci.org/wix-incubator/nutri-matic.svg?branch=master)](https://travis-ci.org/wix-incubator/nutri-matic)

Creates arbitrary instances of scala objects using reflection. Currently supports:
* Primitive types
* Boxed Java primitives
* Strings
* Basic scala collections - Seq, List, Set, Map
* Arrays
* Option and Either
* Sealed traits
* Scala enums
* Case classes
* Case objects
* Classes that have a constructor

### Usage

You can use the default instance:
```scala
import com.wix.nutrimatic.NutriMatic

NutriMatic.makeA[Option[String]]
```

Or you can configure to your hearts content: 
```scala
import com.wix.nutrimatic.NutriMatic

val nutriMatic = NutriMatic.builder // the arguments here are the default values
  .withAllCharacters // created strings will include any characters
  .withOnlyAsciiCharacters // created strings will include only ascii letters and numbers (default)
  .withCollectionSizes(0, 3) // created collections and arrays will have a size between these numbers (inclusive)
  .withStringLengths(1, 20) // created strings will have a length between these numbers (inclusive)
  .withCustomGenerators() // use these custom generators
  .withSeed(scala.util.Random.nextLong()) // seed used for getting random instances
  .withMaxCacheSize(10000) // this library relies on caching quite a bit, so if you run into memory issues, try reducing this value
  .build

nutriMatic.makeA[Option[String]]
```

### Custom types and overrides

The main concept behind making values in generators, which are just partial functions.

The most basic are generators that match an exact type. Say you wanted to make sure that the ints you get are really random. You find a good [code snippet](https://xkcd.com/221/) and get to work: 
```scala
import com.wix.nutrimatic.{Generators, NutriMatic}

val getRandomNumber = Generators.byExactType[Int] { _ => 4}

val nutriMatic = NutriMatic.builder
  .withCustomGenerators(getRandomNumber)
  .build

nutriMatic.makeA[Int] must_== 4
```

For generics, you can also match by an erasure of a type. Your function gets called with context which allows you to make other values and get randoms from the same seed.
```scala
import com.wix.nutrimatic.{Generators, NutriMatic}

val makeSureWeKnow = Generators.byErasure[Option[_]] {
  case (tpe, context) => Some(context.makeComponent(tpe.typeArgs.head))
}

val theAnswerToLifeTheUniverseAndEverything = Generators.byExactType[Int] { _ => 42 }

val nutriMatic = NutriMatic.builder
  .withCustomGenerators(theAnswerToLifeTheUniverseAndEverything, makeSureWeKnow)
  .build

nutriMatic.makeA[Option[Int]] must beSome(42)
```

Finally, you can provide your own custom generators. They are attempted before any other generators:
```scala 
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
```
