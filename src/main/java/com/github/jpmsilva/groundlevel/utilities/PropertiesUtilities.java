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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

/**
 * Utilities related to {@link Properties}.
 */
public abstract class PropertiesUtilities {

  private PropertiesUtilities() {
  }

  /**
   * Reads a {@link Properties} from a string representation.
   *
   * @param input the string representation
   * @return the loaded properties
   * @throws IOException when the string cannot be read (should not really occur)
   */
  public static Properties fromString(String input) throws IOException {
    Properties properties = new Properties();
    properties.load(new StringReader(input));
    return properties;
  }

  /**
   * Writes a {@link Properties} to a string representation.
   *
   * @param properties the properties to write
   * @return the string representation
   * @throws IOException when the string cannot be created (should not really occur)
   */
  public static String asString(Properties properties) throws IOException {
    StringWriter writer = new StringWriter();
    properties.store(writer, null);
    return writer.toString();
  }
}
