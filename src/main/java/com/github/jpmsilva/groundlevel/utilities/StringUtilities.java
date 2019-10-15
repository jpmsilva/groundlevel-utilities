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

/**
 * Utilities related to {@link String}.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class StringUtilities {

  private StringUtilities() {
  }

  /**
   * Joins a {@link Collection} of strings with a specified separator.
   *
   * @param separator the separator to use
   * @param parts the parts to join
   * @return a string representation of the joined parts
   * @see StringJoiner
   */
  public static String join(CharSequence separator, Collection<?> parts) {
    StringJoiner stringJoiner = new StringJoiner(separator);
    parts.stream().map(Object::toString).forEach(stringJoiner::add);
    return stringJoiner.toString();
  }

  /**
   * Joins aa array of strings with a specified separator.
   *
   * @param separator the separator to use
   * @param parts the parts to join
   * @return a string representation of the joined parts
   * @see StringJoiner
   */
  public static String join(CharSequence separator, CharSequence[] parts) {
    return join(separator, Arrays.asList(parts));
  }

  /**
   * A {@link Predicate} that returns {@code true} when the argument is equal to a specific string.
   *
   * @param input the specific string to compare to
   * @return the predicate
   */
  public static Predicate<String> equals(String input) {
    return s -> s.equals(input);
  }
}
