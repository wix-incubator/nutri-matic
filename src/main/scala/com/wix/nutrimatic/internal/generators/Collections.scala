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
