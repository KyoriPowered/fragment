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

import com.google.common.collect.Sets;
import net.kyori.feature.FeatureDefinition;
import net.kyori.xml.node.ElementNode;
import net.kyori.xml.node.Node;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jdom2.Element;

import java.util.Collections;
import java.util.Set;

import static java.util.Objects.requireNonNull;

@Deprecated
final class ReferenceFinderImpl implements ReferenceFinder {
  static final ReferenceFinder FINDER = new ReferenceFinderImpl(Collections.singleton(FeatureDefinition.REF_ELEMENT_NAME));
  private final Set<String> refElements;

  private ReferenceFinderImpl(final Set<String> refElements) {
    this.refElements = refElements;
  }

  @Override
  public boolean test(final @NonNull Node node) {
    if(node instanceof ElementNode) {
      final Element element = ((ElementNode) node).element();
      return this.refElements.contains(element.getName())
        && element.getAttributes().size() == 1
        && FeatureDefinition.ID_ATTRIBUTE_NAME.equals(element.getAttributes().get(0).getName());
    }
    return false;
  }

  @Override
  public ReferenceFinder refs(final @NonNull Set<String> refElements) {
    requireNonNull(refElements, "ref elements");
    return new ReferenceFinderImpl(Sets.union(this.refElements, refElements));
  }
}
