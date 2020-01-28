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
package net.kyori.feature.parser;

import net.kyori.feature.FeatureDefinition;
import net.kyori.feature.FeatureDefinitionContext;
import net.kyori.feature.reference.ReferenceFinder;
import net.kyori.xml.XMLException;
import net.kyori.xml.node.AttributeNode;
import net.kyori.xml.node.Node;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An abstract implementation of a feature parser.
 *
 * @param <D> the feature type
 */
@Deprecated
public abstract class AbstractFeatureDefinitionParser<D extends FeatureDefinition> implements FeatureDefinitionParser<D> {
  @Override
  public @NonNull D throwingParse(final Node node) throws XMLException {
    if(node instanceof AttributeNode) {
      return this.context().get(this.type(), node.value());
    }
    if(this.referenceFinder().test(node)) {
      return this.context().get(this.type(), node);
    }
    return this.realThrowingParse(node);
  }

  protected abstract @NonNull D realThrowingParse(final @NonNull Node node) throws XMLException;

  /**
   * Gets the reference finder.
   *
   * @return the reference finder
   */
  protected @NonNull ReferenceFinder referenceFinder() {
    return ReferenceFinder.finder();
  }

  /**
   * Gets the feature definition type.
   *
   * @return the feature definition type
   */
  protected abstract @NonNull Class<D> type();

  /**
   * Gets the feature definition context.
   *
   * @return the feature definition context
   */
  protected abstract @NonNull FeatureDefinitionContext context();
}
