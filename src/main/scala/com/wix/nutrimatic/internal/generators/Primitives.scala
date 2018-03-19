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
