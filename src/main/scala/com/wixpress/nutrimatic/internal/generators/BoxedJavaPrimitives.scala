package com.wixpress.nutrimatic.internal.generators

import com.wixpress.nutrimatic.Generators

private[nutrimatic] object BoxedJavaPrimitives {
  val numbers = Seq(Generators.byExactType[java.lang.Byte](r => r.randomInt.toByte),
    Generators.byExactType[java.lang.Short](r => r.randomInt.toShort),
    Generators.byExactType[java.lang.Character](r => r.randomInt.toChar),
    Generators.byExactType[java.lang.Integer](r => r.randomInt),
    Generators.byExactType[java.lang.Long](r => r.randomLong),
    Generators.byExactType[java.lang.Float](r => r.randomDouble.toFloat),
    Generators.byExactType[java.lang.Double](r => r.randomDouble))

  val booleans = Seq(Generators.byExactType[java.lang.Boolean](r => r.randomBoolean))

  val generators = numbers ++ booleans
}
