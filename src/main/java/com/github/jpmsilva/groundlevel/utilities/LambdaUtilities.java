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

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class LambdaUtilities {

  private LambdaUtilities() {
  }

  public static <T> BinaryOperator<T> throwingMerger() {
    return (u, v) -> {
      throw new IllegalStateException(String.format("Duplicate key %s", u));
    };
  }

  public static <T> Collection<T> typedList(Collection<?> list, Class<T> type) {
    return list.stream()
        .filter(type::isInstance)
        .map(type::cast)
        .collect(toList());
  }

  public static void withLock(Lock lock, Runnable runnable) {
    lock.lock();
    try {
      runnable.run();
    } finally {
      lock.unlock();
    }
  }

  public static <T> T withLock(Lock lock, Supplier<? extends T> supplier) {
    lock.lock();
    try {
      return supplier.get();
    } finally {
      lock.unlock();
    }
  }

  public static void withReadLock(ReadWriteLock lock, Runnable runnable) {
    withLock(lock.readLock(), runnable);
  }

  public static <T> T withReadLock(ReadWriteLock lock, Supplier<? extends T> supplier) {
    return withLock(lock.readLock(), supplier);
  }

  public static void withWriteLock(ReadWriteLock lock, Runnable runnable) {
    withLock(lock.writeLock(), runnable);
  }

  public static <T> T withWriteLock(ReadWriteLock lock, Supplier<? extends T> supplier) {
    return withLock(lock.writeLock(), supplier);
  }

  public static <T> Predicate<T> contains(final Collection<T> collection) {
    return collection::contains;
  }
}
