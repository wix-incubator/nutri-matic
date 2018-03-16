package com.wix.nutrimatic.internal.generators

import com.wix.nutrimatic.Generators

private[nutrimatic] object Primitives {
  val numbers = Seq(Generators.byExactType[Byte](r => r.randomInt.toByte),
    Generators.byExactType[Short](r => r.randomInt.toShort),
    Generators.byExactType[Char](r => r.randomInt.toChar),
    Generators.byExactType[Int](r => r.randomInt),
    Generators.byExactType[Long](r => r.randomLong),
    Generators.byExactType[Float](r => r.randomDouble.toFloat),
    Generators.byExactType[Double](r => r.randomDouble))

  val booleans = Seq(Generators.byExactType[Boolean](r => r.randomBoolean))

  val strings = Seq(Generators.byExactType[String](r => r.randomStr))

  val generators = numbers ++ booleans ++ strings
}
