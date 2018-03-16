package com.wix.nutrimatic.internal.generators

import com.wix.nutrimatic.Generators

private[nutrimatic] object Monads {
  val generators = Seq(
    Generators.byErasure[Option[_]]((t, r) =>
      if (r.randomBoolean) Some(r.makeComponent(t.typeArgs.head)) else None),
    Generators.byErasure[Either[_, _]]((t, r) =>
      if (r.randomBoolean) Left(r.makeComponent(t.typeArgs.head)) else Right(r.makeComponent(t.typeArgs.last)))
  )
}
