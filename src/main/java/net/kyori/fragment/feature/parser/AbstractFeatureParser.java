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
package net.kyori.fragment.feature.parser;

import com.google.inject.TypeLiteral;
import net.kyori.fragment.feature.Feature;
import net.kyori.fragment.feature.context.FeatureContext;
import net.kyori.xml.XMLException;
import net.kyori.xml.node.Node;

import javax.inject.Inject;
import javax.inject.Provider;

public abstract class AbstractFeatureParser<F extends Feature> implements FeatureParser<F> {
  @Inject private TypeLiteral<F> type;
  @Inject private Provider<FeatureContext> context;

  @Override
  public F throwingParse(final Node node) throws XMLException {
    if(node.attribute(Feature.ID_ATTRIBUTE_NAME).isPresent()) {
      return this.ref(node);
    }
    final F feature = this.parseFeature(node);
    return this.context.get().add(this.type(), node, feature);
  }

  protected abstract F parseFeature(final Node node) throws XMLException;

  protected F ref(final Node node) throws XMLException {
    return this.context.get().get(this.type(), node);
  }

  private Class<F> type() {
    return (Class<F>) this.type.getRawType();
  }
}
