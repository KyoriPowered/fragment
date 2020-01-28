/*
 * This file is part of fragment, licensed under the MIT License.
 *
 * Copyright (c) 2018 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.feature;

import net.kyori.xml.XMLException;
import net.kyori.xml.node.Node;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.stream.Stream;

/**
 * A feature definition context is responsible for storing feature definitions for referencing.
 */
@Deprecated
public interface FeatureDefinitionContext {
  /**
   * Gets a stream of all feature definitions.
   *
   * @return a stream of all feature definitions
   */
  default @NonNull Stream<FeatureDefinition> all() {
    return this.all(FeatureDefinition.class);
  }

  /**
   * Gets a stream of all feature definitions of type {@code type}.
   *
   * @param type the type
   * @param <D> the type
   * @return a stream of all feature definitions
   */
  <D extends FeatureDefinition> @NonNull Stream<D> all(final @NonNull Class<D> type);

  /**
   * Gets a feature definition.
   *
   * <p>The returned feature definition may be proxied.</p>
   *
   * @param type the type
   * @param node the node
   * @param <D> the type
   * @return the feature definition
   * @throws XMLException if {@code node} does not have an {@link FeatureDefinition#ID_ATTRIBUTE_NAME id attribute}
   */
  default <D extends FeatureDefinition> @NonNull D get(final @NonNull Class<D> type, final @NonNull Node node) throws XMLException {
    final Node id = node.attribute(FeatureDefinition.ID_ATTRIBUTE_NAME).required();
    return this.get(type, id.value());
  }

  /**
   * Gets a feature definition.
   *
   * <p>The returned feature definition may be proxied.</p>
   *
   * @param type the type
   * @param id the id
   * @param <D> the type
   * @return the feature definition
   */
  <D extends FeatureDefinition> @NonNull D get(final @NonNull Class<D> type, final @NonNull String id);

  /**
   * Defines a feature in this context.
   *
   * @param type the type
   * @param definition the feature definition
   * @param <D> the type
   * @return the feature definition
   */
  default <D extends FeatureDefinition> @NonNull D define(final @NonNull Class<D> type, final @NonNull D definition) {
    return this.define(type, (String) null, definition);
  }

  /**
   * Defines a feature in this context.
   *
   * @param type the type
   * @param node the node
   * @param definition the feature definition
   * @param <D> the type
   * @return the feature definition
   */
  default <D extends FeatureDefinition> @NonNull D define(final @NonNull Class<D> type, final @NonNull Node node, final @NonNull D definition) {
    return this.define(type, node.attribute(FeatureDefinition.ID_ATTRIBUTE_NAME).map(Node::value).optional(null), definition);
  }

  /**
   * Defines a feature in this context.
   *
   * @param id the id
   * @param definition the feature definition
   * @param <D> the type
   * @return the feature definition
   */
  default <D extends FeatureDefinition> @NonNull D define(final @Nullable String id, final @NonNull D definition) {
    return this.define((Class<D>) definition.getClass(), id, definition);
  }

  /**
   * Defines a feature in this context.
   *
   * @param type the type
   * @param id the id
   * @param definition the feature definition
   * @param <D> the type
   * @return the feature definition
   */
  <D extends FeatureDefinition> @NonNull D define(final @NonNull Class<D> type, final @Nullable String id, final @NonNull D definition);
}
