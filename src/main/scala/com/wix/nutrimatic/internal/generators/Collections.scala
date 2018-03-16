package com.wix.nutrimatic.internal.generators

import com.wix.nutrimatic.Generators

private[nutrimatic] object Collections {
  val generators = Seq(
    Generators.byErasure[List[_]]((t, context) => context.randomCollection(context.makeComponent(t.typeArgs.head)).toList),
    Generators.byErasure[Set[_]]((t, context) => context.randomCollection(context.makeComponent(t.typeArgs.head)).toSet),
    Generators.byErasure[Map[_, _]]((t, context) => context.randomCollection(context.makeComponent(t.typeArgs.head) -> context.makeComponent(t.typeArgs.last)).toMap),
    Generators.byErasure[Seq[_]]((t, context) => context.randomCollection(context.makeComponent(t.typeArgs.head)))
  )
}
