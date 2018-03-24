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
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Utilities related to lambdas.
 */
public abstract class LambdaUtilities {

  private LambdaUtilities() {
  }

  /**
   * Merger operator to use in map collectors that throws {@link IllegalStateException} when duplicate keys occur.
   *
   * @param <T> the type of the the objects
   * @return the merger operator
   */
  public static <T> BinaryOperator<T> throwingMerger() {
    return (u, v) -> {
      throw new IllegalStateException(String.format("Duplicate key %s", u));
    };
  }

  /**
   * Transforms any collection into a {@link List} representation, ensuring that all members are of the correct type.
   *
   * @param collection the collection to transform
   * @param type the type of the members
   * @param <T> the type of the members
   * @return a new list with only the elements that are of the correct type
   */
  public static <T> List<T> typedList(Collection<?> collection, Class<T> type) {
    return collection.stream()
        .filter(type::isInstance)
        .map(type::cast)
        .collect(toList());
  }

  /**
   * Runs a {@link Runnable} under a lock.
   *
   * @param lock the lock to use
   * @param runnable the runnable to run under the lock
   */
  public static void withLock(Lock lock, Runnable runnable) {
    lock.lock();
    try {
      runnable.run();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Fetches a value from a {@link Supplier} under a lock.
   *
   * @param lock the lock to use
   * @param supplier the supplier to get the value from under the lock
   * @param <T> the type of the object returned
   * @return the value obtained from the supplier
   */
  public static <T> T withLock(Lock lock, Supplier<? extends T> supplier) {
    lock.lock();
    try {
      return supplier.get();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Runs a {@link Runnable} under a read lock.
   *
   * @param lock the lock to use
   * @param runnable the runnable to run under the lock
   */
  public static void withReadLock(ReadWriteLock lock, Runnable runnable) {
    withLock(lock.readLock(), runnable);
  }

  /**
   * Fetches a value from a {@link Supplier} under a read lock.
   *
   * @param lock the lock to use
   * @param supplier the supplier to get the value from under the lock
   * @param <T> the type of the object returned
   * @return the value obtained from the supplier
   */
  public static <T> T withReadLock(ReadWriteLock lock, Supplier<? extends T> supplier) {
    return withLock(lock.readLock(), supplier);
  }

  /**
   * Runs a {@link Runnable} under a write lock.
   *
   * @param lock the lock to use
   * @param runnable the runnable to run under the lock
   */
  public static void withWriteLock(ReadWriteLock lock, Runnable runnable) {
    withLock(lock.writeLock(), runnable);
  }

  /**
   * Fetches a value from a {@link Supplier} under a write lock.
   *
   * @param lock the lock to use
   * @param supplier the supplier to get the value from under the lock
   * @param <T> the type of the object returned
   * @return the value obtained from the supplier
   */
  public static <T> T withWriteLock(ReadWriteLock lock, Supplier<? extends T> supplier) {
    return withLock(lock.writeLock(), supplier);
  }

  /**
   * A predicate that is {@code true} whenever a {@link Collection} contains the argument.
   *
   * @param collection the collection to check
   * @param <T> the type of objects in the collection
   * @return the predicate
   */
  public static <T> Predicate<T> contains(final Collection<T> collection) {
    return collection::contains;
  }
}
