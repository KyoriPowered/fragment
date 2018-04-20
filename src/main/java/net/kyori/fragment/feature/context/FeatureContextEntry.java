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
import net.kyori.fragment.feature.ProxiedFeature;
import net.kyori.fragment.proxy.MethodHandleInvocationHandler;
import net.kyori.fragment.proxy.Proxied;
import net.kyori.xml.node.Node;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

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
  @Nullable private final String id;
  /**
   * The feature.
   */
  @Nullable private F feature;
  /**
   * The proxied feature.
   */
  @Nullable private F proxiedFeature;
  /**
   * A list of nodes referencing this feature.
   */
  final List<Node> references = new ArrayList<>();

  FeatureContextEntry(final Class<F> type, @Nullable final String id) {
    this.type = type;
    this.id = id;
  }

  boolean is(final Class<?> type) {
    return this.type.isAssignableFrom(type);
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
        @Nullable
        @Override
        protected Object object(final Method method) {
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
    return this.type.getName() + " with id '" + this.id + '\'';
  }
}
