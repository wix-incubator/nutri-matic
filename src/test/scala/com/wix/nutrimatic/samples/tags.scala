package com.wix.nutrimatic.samples

object tags {
  
  sealed trait FirstNameTag
  type FirstName = Tagged[String, FirstNameTag]
  def firstName(v: String): FirstName = apply[String, FirstNameTag](v)

  sealed trait LastNameTag
  type LastName = Tagged[String, LastNameTag]
  def lastName(v: String): LastName = apply[String, LastNameTag](v)

  case class Person(firstName: FirstName, lastName: LastName, motto: String)

  sealed trait Tag[U]

  type Tagged[T, U] = T with Tag[U]
  
  def apply[T, U](value: T): Tagged[T, U] =
    value.asInstanceOf[Tagged[T, U]]
}
