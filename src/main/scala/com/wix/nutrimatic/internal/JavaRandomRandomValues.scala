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

package com.wix.nutrimatic.internal

import com.wix.nutrimatic.RandomValues
import org.apache.commons.lang3.RandomStringUtils

import scala.util.Random

private[nutrimatic] case class JavaRandomRandomValues(collectionMinSize: Int = 0,
                                                      collectionMaxSize: Int = 3,
                                                      stringMinSize: Int = 1,
                                                      stringMaxSize: Int = 20,
                                                      onlyAscii: Boolean = true,
                                                      initialSeed: Long = Random.nextLong()) extends RandomValues {
  private val javaRandom = new java.util.Random(initialSeed)

  override def randomStr: String = {
    val length = randomInt(stringMinSize, stringMaxSize + 1)
    if (onlyAscii) {
      RandomStringUtils.random(length, 0, 0, true, true, null, javaRandom)
    } else {
      RandomStringUtils.random(length, 0, 0, false, false, null, javaRandom)
    }
  }

  override def randomInt(from: Int, to: Int): Int = if (to == from) to else javaRandom.nextInt(to - from) + from

  override def randomInt: Int = javaRandom.nextInt()

  override def randomLong: Long = javaRandom.nextLong()

  override def randomDouble: Double = javaRandom.nextDouble()

  override def randomBoolean: Boolean = javaRandom.nextBoolean()

  override def randomCollection[T](generator: => T): Seq[T] = Range(0, randomInt(collectionMinSize, collectionMaxSize + 1)).map(_ => generator)
}

