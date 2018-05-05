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
import net.kyori.lunar.exception.Exceptions;
import net.kyori.xml.XMLException;
import net.kyori.xml.node.Node;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class FeatureContextImpl extends net.kyori.lunar.feature.FeatureContextImpl implements FeatureContext {
  @Override
  public <F> @NonNull F get(final @NonNull Class<F> type, final @NonNull Node node) {
    final Node id = node.attribute(Feature.REF_ATTRIBUTE_NAME).orElseGet(Exceptions.rethrowSupplier(() -> node.requireAttribute(Feature.ID_ATTRIBUTE_NAME)));
    return ((FeatureContextEntry<F>) this.entry(type, id.value())).ref(node).get();
  }

  @Override
  public <F> F add(final @NonNull Class<F> type, final @NonNull Node node, final @NonNull F feature) {
    return this.add(type, node.attribute(Feature.ID_ATTRIBUTE_NAME).map(Node::value).orElse(null), feature);
  }

  @Override
  public @NonNull List<XMLException> validate() {
    final List<XMLException> exceptions = new ArrayList<>();
    for(final net.kyori.lunar.feature.FeatureContextEntry<?> entry : this.entries.values()) {
      if(!entry.defined()) {
        ((FeatureContextEntry<?>) entry).references.forEach(reference -> exceptions.add(new FeatureNotDefinedException(reference, "feature of type " + entry.toString() + " has not been defined")));
      }
    }
    return exceptions;
  }

  @Override
  protected <F> net.kyori.lunar.feature.@NonNull FeatureContextEntry<F> createEntry(final @NonNull Class<F> type, final @NonNull String id) {
    return new FeatureContextEntry<>(type, id);
  }
}
