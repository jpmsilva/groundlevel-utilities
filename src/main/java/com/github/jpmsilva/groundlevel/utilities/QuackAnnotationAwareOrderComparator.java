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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * If it looks like Ordered, then it must be Ordered. This specialized {@link OrderComparator} performs comparisons similar to {@link
 * org.springframework.core.annotation.AnnotationAwareOrderComparator}, but with some key differences.
 *
 * <p>1) the {@link Order} annotation is always checked first.
 *
 * <p>2) if the {@link Order} annotation is added to the bean producer method, it will also be checked, and take precedence over the annotation at the bean
 * class level. If multiple bean producer methods contribute the same instance to the bean factory, the behavior is unspecified.
 *
 * <p>3) beans do not need to implement {@link Ordered}, they simply need to have a public method with the signature {@code int getOrder()}, and that method
 * will be used to also determine the order value.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class QuackAnnotationAwareOrderComparator extends OrderComparator {

  private final ConfigurableListableBeanFactory beanFactory;

  /**
   * Creates a new {@code QuackAnnotationAwareOrderComparator}, using the provided bean factory to check for {@link Order} annotations on bean producer
   * methods.
   *
   * @param beanFactory The bean factory to check for method annotations.
   */
  public QuackAnnotationAwareOrderComparator(ConfigurableListableBeanFactory beanFactory) {
    Objects.requireNonNull(beanFactory);
    this.beanFactory = beanFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected int getOrder(Object obj) {
    if (obj != null) {
      // First we check if either the bean producer method or the bean class is annotated with @Order
      Map<String, Object> methodAttributes = SpringUtilities
          .beanAnnotationAttributes(beanFactory, Order.class, obj);
      if (methodAttributes.containsKey("value")) {
        return (Integer) methodAttributes.get("value");
      }

      // Second we check if the object implements Ordered
      if (obj instanceof Ordered) {
        return ((Ordered) obj).getOrder();
      }

      // Finally we check if it quacks like an Ordered
      try {
        Method getOrder = obj.getClass().getMethod("getOrder");
        if (Integer.TYPE.equals(getOrder.getReturnType())) {
          try {
            return (Integer) getOrder.invoke(obj);
          } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
          }
        }
      } catch (NoSuchMethodException ignored) {
      }
    }
    return Ordered.LOWEST_PRECEDENCE;
  }
}
