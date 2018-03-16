package com.wix.nutrimatic.internal

import com.wix.nutrimatic.RandomValues
import org.apache.commons.lang3.RandomStringUtils

import scala.util.Random

private[nutrimatic] case class JavaRandomRandomValues(collectionMinSize: Int = 3,
                                                      collectionMaxSize: Int = 3,
                                                      stringMinSize: Int = 20,
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

