package com.wix.nutrimatic.samples

object tags {

  //http://etorreborre.blogspot.com/2011/11/practical-uses-for-unboxed-tagged-types.html
  type Tagged[U] = { type Tag = U }
  type @@[T, U] = T with Tagged[U]
  
  sealed trait FirstNameTag
  type FirstName = String @@ FirstNameTag
  def firstName(v: String): FirstName = v.asInstanceOf[FirstName]

  sealed trait LastNameTag
  type LastName = String @@ LastNameTag
  def lastName(v: String): LastName = v.asInstanceOf[LastName]

  sealed trait AgeTag
  type Age = Short @@ AgeTag
  
  def age(v: Short): Either[IllegalArgumentException, Age] =
    if (v >= 0 && v < 123) Right(v.asInstanceOf[Age]) else Left(new IllegalArgumentException)
  
  case class Person(firstName: FirstName, lastName: LastName, motto: String, age: Age)
}
