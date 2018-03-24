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

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.MethodMetadata;

/**
 * Utilities related to the Spring framework.
 */
public abstract class SpringUtilities {

  private SpringUtilities() {
  }

  /**
   * Obtains a {@link List} of beans from a {@link ApplicationContext}, sorted under {@link QuackAnnotationAwareOrderComparator} rules.
   *
   * @param applicationContext the application context to obtain the beans
   * @param type the type of beans to obtain
   * @param <T> the type of beans to obtain
   * @return the sorted list of beans
   */
  public static <T> List<T> getSortedBeansOfType(ApplicationContext applicationContext,
      Class<T> type) {
    if (applicationContext instanceof ConfigurableApplicationContext) {
      return getSortedBeansOfType(
          ((ConfigurableApplicationContext) applicationContext).getBeanFactory(), type);
    }
    return getSortedBeansOfType((BeanFactory) applicationContext, type);
  }

  /**
   * Obtains a {@link List} of beans from a {@link BeanFactory}, sorted under {@link QuackAnnotationAwareOrderComparator} rules.
   *
   * @param factory the bean factory to obtain the beans
   * @param type the type of beans to obtain
   * @param <T> the type of beans to obtain
   * @return the sorted list of beans
   */
  public static <T> List<T> getSortedBeansOfType(BeanFactory factory, Class<T> type) {
    if (!(factory instanceof ConfigurableListableBeanFactory)) {
      return Collections.emptyList();
    }
    ConfigurableListableBeanFactory configurableFactory = (ConfigurableListableBeanFactory) factory;
    return configurableFactory.getBeansOfType(type).values().stream()
        .sorted(new QuackAnnotationAwareOrderComparator(configurableFactory))
        .collect(Collectors.toList());
  }

  /**
   * Obtains a {@link List} of beans from a {@link BeanFactory} that have a specific annotation.
   *
   * @param beanFactory the bean factory to obtain the beans
   * @param type the annotation type beans must have
   * @return the list of beans with the annotation
   */
  public static List<String> beansAnnotatedWith(ConfigurableListableBeanFactory beanFactory,
      Class<? extends Annotation> type) {
    return Arrays.stream(beanFactory.getBeanDefinitionNames())
        .filter(beanAnnotatedWith(beanFactory, type))
        .collect(Collectors.toList());
  }

  private static Predicate<String> beanAnnotatedWith(ConfigurableListableBeanFactory beanFactory,
      Class<? extends Annotation> type) {
    return name -> {
      BeanDefinition bd = beanFactory.getBeanDefinition(name);

      if (bd.getSource() instanceof MethodMetadata) {
        MethodMetadata metadata = (MethodMetadata) bd.getSource();
        return metadata.isAnnotated(type.getName());
      }

      return false;
    };
  }

  /**
   * Calculates the attributes of annotation {@code type}, taking into consideration class level annotations (from {@code bean.getClass()}), as well as method
   * level annotations when the bean has been added to the bean factory by a Java method.
   *
   * <p>Take the following example:
   *
   * <pre>
   *     &#064;Order(value=1)
   *     public class MyBean {
   *     }
   * </pre>
   *
   * <p>In such a case, fetching the annotation attributes of {@code @Order} on an instance of {@code MyBean} will wield a map with key="value" and value=1.
   *
   * <p>Now consider that an instance of the class {@code MyBean} is also defined as a Spring bean, using a configuration class such as:
   *
   * <pre>
   *     &#064;Configuration
   *     public class MyConfiguration {
   *         &#064;Bean @Order(value=2)}
   *         public MyBean myBean() {
   *           return new MyBean();
   *         }
   *     }
   * </pre>
   *
   * <p>In such a case, fetching the annotation attributes of {@code @Order} on the instance {@code myBean()} will wield a map with key="value" and value=2.
   *
   * <p>Annotations at the method level take precedence over annotations at the class level.
   *
   * @param beanFactory The bean factory where the bean may haves been defined.
   * @param type The type of the annotation.
   * @param bean The object for which annotation attributes should be calculated.
   * @return A {@link Map} containing as keys the annotation attribute names and as values the annotation attribute values.
   */
  public static Map<String, Object> beanAnnotationAttributes(
      ConfigurableListableBeanFactory beanFactory, Class<? extends Annotation> type, Object bean) {
    List<BeanDefinition> beanTypes = Arrays.stream(beanFactory.getBeanNamesForType(bean.getClass()))
        .filter(beanFactory::containsBeanDefinition)
        .map(beanFactory::getBeanDefinition)
        .filter(hasMethodAnnotation(type))
        .collect(Collectors.toList());

    // Load annotation attributes from the bean class
    Map<String, Object> results = new HashMap<>(beanAnnotationAttributes(type, bean));

    if (beanTypes.size() > 0) {
      // Load annotation attributes from the bean method
      results.putAll(beanAnnotationAttributes(beanTypes.get(0), type));
    }

    return results;
  }

  private static Map<String, Object> beanAnnotationAttributes(Class<? extends Annotation> type,
      Object bean) {
    Annotation annotation = AnnotationUtils.findAnnotation(bean.getClass(), type);
    if (annotation == null) {
      return Collections.emptyMap();
    }
    return AnnotationUtils.getAnnotationAttributes(annotation);
  }

  private static Map<String, Object> beanAnnotationAttributes(BeanDefinition beanDefinition,
      Class<? extends Annotation> type) {
    if (beanDefinition.getSource() instanceof MethodMetadata) {
      MethodMetadata metadata = (MethodMetadata) beanDefinition.getSource();
      return metadata.getAnnotationAttributes(type.getName());
    }

    return Collections.emptyMap();
  }

  private static Predicate<BeanDefinition> hasMethodAnnotation(Class<? extends Annotation> type) {
    return beanDefinition -> {
      if (beanDefinition.getSource() instanceof MethodMetadata) {
        MethodMetadata metadata = (MethodMetadata) beanDefinition.getSource();
        return metadata.isAnnotated(type.getName());
      }
      return false;
    };
  }
}
