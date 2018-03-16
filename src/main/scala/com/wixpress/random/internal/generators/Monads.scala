package com.wixpress.random.internal.generators

import com.wixpress.random.Generators

private[random] object Monads {
  val generators = Seq(
    Generators.byErasure[Option[_]]((t, context) =>
      if (context.basic.randomBoolean) Some(context.random(t.typeArgs.head)) else None),
    Generators.byErasure[Either[_, _]]((t, context) =>
      if (context.basic.randomBoolean) Left(context.random(t.typeArgs.head)) else Right(context.random(t.typeArgs.last)))
  )
}
