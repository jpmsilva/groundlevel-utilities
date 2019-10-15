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

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Utilities related to generics.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class GenericsUtilities {

  private GenericsUtilities() {
  }

  /**
   * Casts an object to a certain type, if it can be casted, or throws a {@link ClassCastException} otherwise.
   *
   * @param o the object to cast
   * @param <T> the type to cast to
   * @return the casted object
   */
  @SuppressWarnings("unchecked")
  public static <T> T cast(Object o) {
    return (T) o;
  }

  /**
   * Casts an object to a certain type, if it can be casted, or returns {@code null} otherwise.
   *
   * @param o the object to cast
   * @param type the type to cast to
   * @param <T> the type to cast to
   * @return the casted object
   */
  public static <T> T cast(Object o, Class<T> type) {
    return cast(o, () -> null, type);
  }

  /**
   * Casts an object to a certain type, if it can be casted, or returns a default value otherwise.
   *
   * @param o the object to cast
   * @param type the type to cast to
   * @param defaultValue the default value to use when the object cannot be cast
   * @param <T> the type to cast to
   * @return the casted object
   */
  public static <T> T cast(Object o, Class<T> type, T defaultValue) {
    return cast(o, () -> defaultValue, type);
  }

  /**
   * Casts an object to a certain type, if it can be casted, or returns the result of a supplier otherwise.
   *
   * @param o the object to cast
   * @param supplier the supplied to use when the object cannot be cast
   * @param type the type to cast to
   * @param <T> the type to cast to
   * @return the casted object
   */
  public static <T> T cast(Object o, Supplier<T> supplier, Class<T> type) {
    Objects.requireNonNull(o);
    Objects.requireNonNull(type);
    Objects.requireNonNull(supplier);
    return type.isInstance(o) ? cast(o) : supplier.get();
  }

  /**
   * Casts an object to a certain type, if it can be casted, or throws an error otherwise.
   *
   * @param o the object to cast
   * @param type the type to cast to
   * @param error the exception to throw
   * @param <T> the type to cast to
   * @param <E> the type of the error to throw
   * @return the casted object
   * @throws E when the object cannot be casted
   */
  public static <T, E extends Throwable> T cast(Object o, Class<T> type, E error) throws E {
    Objects.requireNonNull(o);
    Objects.requireNonNull(type);
    Objects.requireNonNull(error);
    if (type.isInstance(o)) {
      return cast(o);
    }
    throw error;
  }
}
