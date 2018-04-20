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

import com.google.common.base.MoreObjects;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.kyori.fragment.feature.Feature;
import net.kyori.fragment.proxy.Proxied;
import net.kyori.xml.XMLException;
import net.kyori.xml.node.Node;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FeatureContextImpl implements FeatureContext {
  protected final List<FeatureContextEntry<?>> features = new ArrayList<>();
  protected final Table<Class<?>, String, FeatureContextEntry<?>> featuresById = HashBasedTable.create();
  private final Map<Class<?>, FeatureList<?>> featureLists = new HashMap<>();

  @Override
  public <F> @NonNull List<F> all(final @NonNull Class<F> type) {
    return (List<F>) this.featureLists.computeIfAbsent(type, FeatureList::new);
  }

  private class FeatureList<F> extends ForwardingList<F> {
    private final Class<F> type;
    private @Nullable List<F> list;

    FeatureList(final Class<F> type) {
      this.type = type;
    }

    void invalidate() {
      this.list = null;
    }

    @Override
    protected List<F> delegate() {
      if(this.list == null) {
        this.list = FeatureContextImpl.this.features.stream()
          .filter(feature -> feature.is(this.type))
          .map(feature -> (F) feature.get())
          .collect(Collectors.toList());
      }
      return this.list;
    }
  }

  @Override
  public <F> @NonNull F get(final @NonNull Class<F> type, final @NonNull Node node) throws XMLException {
    final Node id = node.requireAttribute(Feature.ID_ATTRIBUTE_NAME);
    return this.feature(type, id.value()).ref(node).get();
  }

  @Override
  public <F> @NonNull F get(final @NonNull Class<F> type, final @NonNull String id) {
    return this.feature(type, id).get();
  }

  @Override
  public <F> F add(final @NonNull Class<F> type, final @NonNull Node node, final @NonNull F feature, final Set<Flag> flags) {
    return this.add(type, node.attribute(Feature.ID_ATTRIBUTE_NAME).map(Node::value).orElse(null), feature, flags);
  }

  @Override
  public <F> F add(final @NonNull Class<F> type, final @Nullable String id, final @NonNull F feature, final Set<Flag> flags) {
    // Don't insert a proxied feature.
    if(feature instanceof Proxied) {
      return feature;
    }

    // This feature has an id, and can be referenced.
    if(id != null || flags.contains(Flag.ADD_WITHOUT_ID)) {
      this.feature(type, id).define(feature);
      this.features(type).ifPresent(FeatureList::invalidate);
    }

    return feature;
  }

  protected <F> FeatureContextEntry<F> feature(final Class<F> type, final @Nullable String id) {
    FeatureContextEntry<F> entry;
    if(id != null) {
      entry = (FeatureContextEntry<F>) this.featuresById.get(type, id);
      if(entry == null) {
        entry = this.createFeature(type, id);
        this.featuresById.put(type, id, entry);
      }
      return entry;
    } else {
      entry = this.createFeature(type, id);
    }
    return entry;
  }

  private <F> FeatureContextEntry<F> createFeature(final Class<F> type, final @Nullable String id) {
    final FeatureContextEntry<F> entry = new FeatureContextEntry<>(type, id);
    this.features.add(entry);
    return entry;
  }

  private <F> Optional<FeatureList<F>> features(final Class<F> type) {
    return Optional.ofNullable((FeatureList<F>) this.featureLists.get(type));
  }

  @Override
  public @NonNull List<XMLException> validate() {
    final List<XMLException> exceptions = new ArrayList<>();
    for(final FeatureContextEntry<?> entry : this.features) {
      if(entry.virtual()) {
        entry.references.forEach(reference -> exceptions.add(new FeatureNotDefinedException(reference, "feature of type " + entry.toString() + " has not been defined")));
      }
    }
    return exceptions;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .addValue(this.features)
      .toString();
  }
}
