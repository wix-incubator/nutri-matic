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

import com.wix.nutrimatic.{Generator, GeneratorGenerator, TypeAndContext}

import scala.reflect.runtime.universe._

private[nutrimatic] object ClassesWithConstructors extends GeneratorGenerator[Any] {

  override def isDefinedAt(tc: TypeAndContext): Boolean = {
    val (t, _) = tc
    val typeSymbol = t.typeSymbol
    typeSymbol.isClass && !typeSymbol.isAbstract && getPrimaryConstructor(t).isDefined
  }

  override def apply(tc: TypeAndContext): Generator[Any] = {
    val (t, _) = tc
    val mirror = rootMirror
    val clazz = t.typeSymbol.asClass
    val primaryConstructor = getPrimaryConstructor(t).get
    val ctorParams = primaryConstructor.infoIn(t).paramLists.flatten
    val reflected = mirror.reflectClass(clazz).reflectConstructor(primaryConstructor)

    {
      case (_, context) =>
        val args = ctorParams.map(s => {
          context.makeComponent(s.typeSignature, s.name.toString)
        })
        reflected.apply(args: _*)
    }
  }

  private def getPrimaryConstructor(t: Type): Option[MethodSymbol] = {
    val constructorDeclaration = t.decl(termNames.CONSTRUCTOR)
    if (constructorDeclaration.isTerm) {
      val alternatives = constructorDeclaration.asTerm.alternatives
      alternatives
        .collectFirst { case ctor: MethodSymbol if ctor.isPrimaryConstructor => ctor }
        .orElse(Some(alternatives.head.asMethod))
    } else {
      None
    }
  }
}
