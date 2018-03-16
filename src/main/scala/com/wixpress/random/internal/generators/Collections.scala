package com.wixpress.random.internal.generators

import com.wixpress.random.Generators

private[random] object Collections {
  val generators = Seq(
    Generators.byErasure[List[_]]((t, context) => context.basic.randomCollection(context.random(t.typeArgs.head)).toList),
    Generators.byErasure[Set[_]]((t, context) => context.basic.randomCollection(context.random(t.typeArgs.head)).toSet),
    Generators.byErasure[Map[_, _]]((t, context) => context.basic.randomCollection(context.random(t.typeArgs.head) -> context.random(t.typeArgs.last)).toMap),
    Generators.byErasure[Seq[_]]((t, context) => context.basic.randomCollection(context.random(t.typeArgs.head)))
  )
}
