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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import net.kyori.fragment.feature.Feature;
import net.kyori.fragment.feature.ProxiedFeature;
import net.kyori.lunar.proxy.MethodHandleInvocationHandler;
import net.kyori.lunar.proxy.Proxied;
import net.kyori.xml.XMLException;
import net.kyori.xml.node.Node;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class FeatureContextImpl implements FeatureContext {
  protected final ListMultimap<Class<?>, FeatureContextEntry<?>> featuresByType = ArrayListMultimap.create();
  protected final Table<Class<?>, String, FeatureContextEntry<?>> featuresById = HashBasedTable.create();

  @Override
  public <F> @NonNull Collection<F> all(final @NonNull Class<F> type) {
    return Lists.transform(this.featuresByType.get(type), input -> (F) input.get());
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
    }

    return feature;
  }

  protected <F> FeatureContextEntry<F> feature(final Class<F> type, final @Nullable String id) {
    FeatureContextEntry<F> entry = null;
    if(id != null) {
      entry = (FeatureContextEntry<F>) this.featuresById.get(type, id);
    }
    if(entry == null) {
      entry = new FeatureContextEntry<>(type, id);
    }
    return entry;
  }

  @Override
  public @NonNull List<XMLException> validate() {
    final List<XMLException> exceptions = new ArrayList<>();
    for(final FeatureContextEntry<?> entry : this.featuresByType.values()) {
      if(entry.virtual()) {
        entry.references.forEach(reference -> exceptions.add(new FeatureNotDefinedException(reference, "feature of type " + entry.toString() + " has not been defined")));
      }
    }
    return exceptions;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .addValue(this.featuresByType)
      .toString();
  }

  /**
   * An entry in a feature context.
   *
   * @param <F> the feature type
   */
  public class FeatureContextEntry<F> {
    /**
     * The feature type.
     */
    private final Class<F> type;
    /**
     * The id used for referencing this feature.
     */
    private final @Nullable String id;
    /**
     * The feature.
     */
    private @MonotonicNonNull F feature;
    /**
     * The proxied feature.
     */
    private @Nullable F proxiedFeature;
    /**
     * A list of nodes referencing this feature.
     */
    final List<Node> references = new ArrayList<>();

    FeatureContextEntry(final Class<F> type, final @Nullable String id) {
      this.type = type;
      this.id = id;

      FeatureContextImpl.this.featuresByType.put(type, this);
      if(id != null) {
        FeatureContextImpl.this.featuresById.put(type, id, this);
      }
    }

    boolean virtual() {
      return this.feature == null;
    }

    FeatureContextEntry<F> ref(final Node node) {
      this.references.add(node);
      return this;
    }

    F get() {
      if(this.feature != null) {
        return this.feature;
      }
      return this.proxy();
    }

    void define(final F feature) {
      if(!this.virtual() && this.feature != feature) {
        throw new IllegalStateException(this.toString() + " already defined as " + this.feature + ", cannot redefine as " + feature);
      }
      this.feature = feature;
    }

    private F proxy() {
      if(this.proxiedFeature == null) {
        class ProxiedFeatureImpl extends MethodHandleInvocationHandler {
          @Override
          protected @Nullable Object object(final Method method) {
            return FeatureContextEntry.this.feature();
          }
        }
        this.proxiedFeature = (F) Proxy.newProxyInstance(this.type.getClassLoader(), this.proxyClasses(), new ProxiedFeatureImpl());
      }
      return this.proxiedFeature;
    }

    private Class<?>[] proxyClasses() {
      final List<Class<?>> classes = new ArrayList<>(2);
      classes.add(this.type);
      if(Feature.class.isAssignableFrom(this.type)) {
        classes.add(ProxiedFeature.class);
      } else {
        classes.add(Proxied.class);
      }
      return classes.toArray(new Class<?>[classes.size()]);
    }

    private F feature() {
      if(this.feature == null) {
        throw new IllegalStateException("feature of type " + this.toString() + " has not been defined");
      }
      return this.feature;
    }

    @Override
    public String toString() {
      return this.type.getName() + (this.id != null ? (" with id '" + this.id + '\'') : "");
    }
  }
}
