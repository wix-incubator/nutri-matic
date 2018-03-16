package com.wixpress.nutrimatic.internal.generators

import com.wixpress.nutrimatic.Generators

private[nutrimatic] object Monads {
  val generators = Seq(
    Generators.byErasure[Option[_]]((t, r) =>
      if (r.randomBoolean) Some(r.random(t.typeArgs.head)) else None),
    Generators.byErasure[Either[_, _]]((t, r) =>
      if (r.randomBoolean) Left(r.random(t.typeArgs.head)) else Right(r.random(t.typeArgs.last)))
  )
}
