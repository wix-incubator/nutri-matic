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

package com.wix.nutrimatic.internal.util

import com.wix.nutrimatic.internal.JavaRandomRandomValues
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.Scope
import org.specs2.mutable.Specification

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class SimpleCacheTest(implicit ee: ExecutionEnv) extends Specification {

  "simple cache" should {
    "find a value after you put it in" in new Context {
      val (key, value) = randomPair
      
      infiniteCache.put(key, value)
      infiniteCache.getIfPresent(key) must beSome(value)
    }
    
    "not find a value if it's not present" in new Context {
      val (key, value) = randomPair
      
      infiniteCache.put(key, value)
      infiniteCache.getIfPresent(randomKey) must beNone
    }

    "remove least recently used value once size limit is reached" in {
      val pair1 #:: pair2 #:: pair3 #:: pair4 #:: _ = randomPairs
      val cache = aCache(3)
      cache.put(pair1._1, pair1._2)
      cache.put(pair2._1, pair2._2)
      cache.put(pair3._1, pair3._2)

      cache.getIfPresent(pair2._1) must beSome(pair2._2)
      cache.getIfPresent(pair1._1) must beSome(pair1._2)
      cache.getIfPresent(pair3._1) must beSome(pair3._2)

      cache.put(pair4._1, pair4._2)

      cache.getIfPresent(pair1._1) must beSome(pair1._2)
      cache.getIfPresent(pair2._1) must beNone
      cache.getIfPresent(pair3._1) must beSome(pair3._2)
      cache.getIfPresent(pair4._1) must beSome(pair4._2)
    }

    "be thread safe" in new Context {
      val pairs = randomPairs.take(1000).toMap
      val future = Future.sequence(pairs.map { pair =>
        Future(infiniteCache.put(pair._1, pair._2))
      })
      
      Await.result(future, 1.second)
      pairs.foreach { case (key, value) => 
        infiniteCache.getIfPresent(key) must beSome(value)
      }
    }
  }

  trait Context extends Scope {
    val infiniteCache = aCache()
  }

  def aCache(size: Int = Int.MaxValue) = new SimpleCache[String, Int](size)

  val randomValues = JavaRandomRandomValues()

  def randomKey: String = randomValues.randomStr

  def randomPair = (randomKey, randomValues.randomInt)

  def randomPairs = Stream.continually(randomPair)
}
