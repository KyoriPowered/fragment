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
package net.kyori.feature.reference;

import com.google.common.collect.ImmutableSet;
import net.kyori.xml.node.Node;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A reference finder.
 */
@Deprecated
public interface ReferenceFinder extends Predicate<Node> {
  /**
   * Creates a finder builder.
   *
   * @return a new finder builder
   */
  static @NonNull ReferenceFinder finder() {
    return ReferenceFinderImpl.FINDER;
  }

  /**
   * Tests if {@code node} is a reference.
   *
   * @param node the node
   * @return {@code true} if {@code node} is a reference, {@code false} otherwise
   */
  @Override
  boolean test(final @NonNull Node node);

  /**
   * Creates a new finder with an additional ref element name.
   *
   * @param refElement the element name
   * @return a new finder
   */
  default @NonNull ReferenceFinder refs(final @NonNull String refElement) {
    return this.refs(Collections.singleton(refElement));
  }

  /**
   * Creates a new finder with additional ref element names.
   *
   * @param refElements the element names
   * @return a new finder
   */
  default @NonNull ReferenceFinder refs(final @NonNull String... refElements) {
    return this.refs(ImmutableSet.copyOf(refElements));
  }

  /**
   * Creates a new finder with additional ref element names.
   *
   * @param refElements the element names
   * @return a new finder
   */
  @NonNull ReferenceFinder refs(final @NonNull Set<String> refElements);
}
