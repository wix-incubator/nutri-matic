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

### Extending
