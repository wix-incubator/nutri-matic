package com.wixpress.random.internal.generators

import com.wixpress.random.Generators

private[random] object Primitives {
  val numbers = Seq(Generators.byExactType[Byte](r => r.basic.randomInt.toByte),
    Generators.byExactType[Short](r => r.basic.randomInt.toShort),
    Generators.byExactType[Char](r => r.basic.randomInt.toChar),
    Generators.byExactType[Int](r => r.basic.randomInt),
    Generators.byExactType[Long](r => r.basic.randomLong),
    Generators.byExactType[Float](r => r.basic.randomDouble.toFloat),
    Generators.byExactType[Double](r => r.basic.randomDouble))
  
  val booleans = Seq(Generators.byExactType[Boolean](r => r.basic.randomBoolean))

  val strings = Seq(Generators.byExactType[String](r => r.basic.randomStr))

  val generators = numbers ++ booleans ++ strings
}
