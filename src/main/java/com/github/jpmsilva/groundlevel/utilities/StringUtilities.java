/*
 * Copyright 2018 Joao Silva
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

package com.github.jpmsilva.groundlevel.utilities;

import java.util.Arrays;
import java.util.Collection;
import java.util.StringJoiner;
import java.util.function.Predicate;

public abstract class StringUtilities {

  private StringUtilities() {
  }

  public static String join(CharSequence separator, Collection<?> parts) {
    StringJoiner stringJoiner = new StringJoiner(separator);
    parts.stream().map(Object::toString).forEach(stringJoiner::add);
    return stringJoiner.toString();
  }

  public static String join(CharSequence separator, CharSequence[] parts) {
    return join(separator, Arrays.asList(parts));
  }

  public static Predicate<String> equals(String input) {
    return s -> s.equals(input);
  }
}
