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
package net.kyori.fragment.feature.context;

import net.kyori.fragment.feature.Feature;
import net.kyori.xml.XMLException;
import net.kyori.xml.node.Node;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/**
 * A feature context is responsible for storing features for referencing.
 */
public interface FeatureContext {
  /**
   * Gets a feature.
   *
   * <p>The returned feature may be proxied.</p>
   *
   * @param type the feature type
   * @param node the node with the {@link Feature#ID_ATTRIBUTE_NAME id attribute}
   * @param <F> the feature type
   * @return the feature
   * @throws XMLException if {@code node} does not have an {@link Feature#ID_ATTRIBUTE_NAME id attribute}
   */
  <F extends Feature> @NonNull F get(final @NonNull Class<F> type, final @NonNull Node node) throws XMLException;

  /**
   * Gets a feature.
   *
   * <p>The returned feature may be proxied.</p>
   *
   * @param type the feature type
   * @param id the feature id
   * @param <F> the feature type
   * @return the feature
   */
  <F extends Feature> @NonNull F get(final @NonNull Class<F> type, final @NonNull String id);

  /**
   * Defines a feature with this context.
   *
   * @param type the feature type
   * @param node the node with the {@link Feature#ID_ATTRIBUTE_NAME id attribute}
   * @param feature the feature
   * @param <F> the feature type
   * @return the feature
   */
  <F extends Feature> @NonNull F add(final @NonNull Class<F> type, final @NonNull Node node, final @NonNull F feature);

  /**
   * Defines a feature with this context.
   *
   * @param type the feature type
   * @param id the feature id
   * @param feature the feature
   * @param <F> the feature type
   * @return the feature
   * @deprecated use {@link #add(Class, Node, Feature)} when you have the node defining the feature present
   */
  @Deprecated
  <F extends Feature> @NonNull F add(final @NonNull Class<F> type, final @Nullable String id, final @NonNull F feature);

  /**
   * Validate this feature context.
   *
   * @return a list of exceptions
   */
  @NonNull List<XMLException> validate();
}
