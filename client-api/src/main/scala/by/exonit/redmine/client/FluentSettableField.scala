/*
 * Copyright 2017 Exon IT
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

package by.exonit.redmine.client

class FluentSettableField[@specialized TValue, TReturn](returnObject: TReturn) {
  private var _value: Option[TValue] = None

  def set(value: TValue): TReturn = {
    _value = Some(value)
    returnObject
  }

  def setOpt(value: Option[TValue]): TReturn = {
    _value = value
    returnObject
  }

  def unset(): TReturn = {
    _value = None
    returnObject
  }

  def isSet: Boolean = _value.isDefined
  def get: TValue = _value.get
  def toOpt: Option[TValue] = _value

  def initAndMutate(initial: => TValue, mutator: TValue => TValue): TReturn = {
    _value = Some(mutator(_value.getOrElse(initial)))
    returnObject
  }

  def foreach: ((TValue) => Any) => Unit = _value.foreach _
}

