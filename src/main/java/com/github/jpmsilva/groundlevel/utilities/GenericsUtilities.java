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

import static org.apache.commons.lang3.Validate.notNull;

import java.util.function.Supplier;

public abstract class GenericsUtilities {

  private GenericsUtilities() {
  }

  @SuppressWarnings("unchecked")
  public static <T> T cast(Object o) {
    return (T) o;
  }

  public static <T> T cast(Object o, Class<T> type) {
    return cast(o, () -> null, type);
  }

  public static <T> T cast(Object o, Class<T> type, T defaultValue) {
    return cast(o, () -> defaultValue, type);
  }

  public static <T> T cast(Object o, Supplier<T> supplier, Class<T> type) {
    notNull(o);
    notNull(type);
    notNull(supplier);
    return type.isInstance(o) ? cast(o) : supplier.get();
  }

  public static <T, E extends Throwable> T cast(Object o, Class<T> type, E error) throws E {
    notNull(o);
    notNull(type);
    notNull(error);
    if (type.isInstance(o)) {
      return cast(o);
    }
    throw error;
  }
}
