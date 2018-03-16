package com.wixpress.nutrimatic.internal.generators

import com.wixpress.nutrimatic.Generators

private[nutrimatic] object Collections {
  val generators = Seq(
    Generators.byErasure[List[_]]((t, context) => context.randomCollection(context.random(t.typeArgs.head)).toList),
    Generators.byErasure[Set[_]]((t, context) => context.randomCollection(context.random(t.typeArgs.head)).toSet),
    Generators.byErasure[Map[_, _]]((t, context) => context.randomCollection(context.random(t.typeArgs.head) -> context.random(t.typeArgs.last)).toMap),
    Generators.byErasure[Seq[_]]((t, context) => context.randomCollection(context.random(t.typeArgs.head)))
  )
}
