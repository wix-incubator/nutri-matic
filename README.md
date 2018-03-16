# Nutri-Matic

> He had found a Nutri-Matic machine which had provided him with a plastic cup filled with a liquid that was almost, but not quite, entirely unlike tea
>
> &mdash; <cite> Douglas Adams, The Hitchhiker's Guide to the Galaxy

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
* Classes that have a constructor

### Usage

You can use the default instance:
```scala
import com.wix.nutrimatic.NutriMatic

NutriMatic.makeA[Option[String]]
```

Or you can configure to your hearts content: 
```scala
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
```

### Custom types and overrides

The main concept behind making values in generators, which are just partial functions.
The most basic type are generators that match an exact type:

```scala
import com.wix.nutrimatic.{Generators, NutriMatic}

val getRandomNumber = Generators.byExactType[Int] { 
  context: Context => context.randomInt(0, 5) // context gives access to functions to make other values
}

val nutriMatic = NutriMatic.builder
  .withCustomGenerators(getRandomNumber)
  .build

nutriMatic.makeA[Int] must beBetween(0, 5)
```

You can also match by an erasure of a type:
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

Finally, you provide your own custom generators. They are fired before any other generators:
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
