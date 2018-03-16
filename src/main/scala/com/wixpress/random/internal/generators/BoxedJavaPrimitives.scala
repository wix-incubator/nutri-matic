package com.wixpress.random.internal.generators

import com.wixpress.random.Generators

private[random] object BoxedJavaPrimitives {
  val numbers = Seq(Generators.byExactType[java.lang.Byte](r => r.basic.randomInt.toByte),
    Generators.byExactType[java.lang.Short](r => r.basic.randomInt.toShort),
    Generators.byExactType[java.lang.Character](r => r.basic.randomInt.toChar),
    Generators.byExactType[java.lang.Integer](r => r.basic.randomInt),
    Generators.byExactType[java.lang.Long](r => r.basic.randomLong),
    Generators.byExactType[java.lang.Float](r => r.basic.randomDouble.toFloat),
    Generators.byExactType[java.lang.Double](r => r.basic.randomDouble))
  
  val booleans = Seq(Generators.byExactType[java.lang.Boolean](r => r.basic.randomBoolean))

  val generators = numbers ++ booleans 
}
