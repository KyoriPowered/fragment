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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.kyori.lunar.collection.MoreTables;
import net.kyori.lunar.proxy.MethodHandleInvocationHandler;
import net.kyori.lunar.proxy.Proxied;
import net.kyori.lunar.proxy.Proxies;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Deprecated
public class FeatureDefinitionContextImpl implements FeatureDefinitionContext {
  private final List<Entry<? extends FeatureDefinition>> all = new ArrayList<>();
  private final Map<FeatureDefinition, Entry<? extends FeatureDefinition>> byDefinition = new IdentityHashMap<>();
  protected final Table<Class<? extends FeatureDefinition>, String, Entry<? extends FeatureDefinition>> byId = HashBasedTable.create();

  @Override
  public @NonNull <D extends FeatureDefinition> Stream<D> all(final @NonNull Class<D> type) {
    return this.all.stream()
      .filter(entry -> type.isAssignableFrom(entry.type))
      .map(entry -> (D) entry.get());
  }

  @Override
  public <D extends FeatureDefinition> @NonNull D get(final @NonNull Class<D> type, final @NonNull String id) {
    return ((Entry<D>) MoreTables.computeIfAbsent(this.byId, type, id, Identified::new)).get();
  }

  @Override
  public <D extends FeatureDefinition> @NonNull D define(final @NonNull Class<D> type, final @Nullable String id, final @NonNull D definition) {
    return this.defineInternal(type, id, definition).get();
  }

  private <D extends FeatureDefinition> @NonNull Entry<D> defineInternal(final @NonNull Class<D> type, final @Nullable String id, final @NonNull D definition) {
    final /* @Nullable */ Entry<? extends FeatureDefinition> existing = this.byDefinition.get(definition);
    if(existing != null) {
      if(existing.definition != definition) {
        throw new IllegalStateException("attempted redefinition of " + definition);
      }
      return (Entry<D>) existing;
    }

    final Entry<D> entry;
    if(id == null) {
      entry = new Anonymous<>(type, definition);
    } else {
      if(!type.isInterface()) {
        throw new IllegalArgumentException(type + " is not an interface and cannot be assigned an id");
      }

      entry = (Entry<D>) MoreTables.computeIfAbsent(this.byId, type, id, Identified::new);

      if(!entry.defined()) {
        entry.define(definition);
      }
    }

    return entry;
  }

  protected abstract class Entry<D extends FeatureDefinition> {
    /**
     * The feature definition type.
     */
    final Class<D> type;
    /**
     * The feature definition.
     */
    @MonotonicNonNull D definition;

    Entry(final @NonNull Class<D> type) {
      this.type = type;

      FeatureDefinitionContextImpl.this.all.add(this);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    final boolean defined() {
      return this.definition != null;
    }

    void define(final @NonNull D definition) {
      this.definition = definition;
      FeatureDefinitionContextImpl.this.byDefinition.put(definition, this);
    }

    public abstract @NonNull D get();
  }

  class Anonymous<D extends FeatureDefinition> extends Entry<D> {
    Anonymous(final @NonNull Class<D> type, final @NonNull D definition) {
      super(type);
      this.define(definition);
    }

    @Override
    public @NonNull D get() {
      return this.definition;
    }
  }

  class Identified<D extends FeatureDefinition> extends Entry<D> {
    /**
     * The id of the feature definition, used for referencing.
     */
    final String id;
    /**
     * The proxied feature definition.
     */
    @MonotonicNonNull D proxiedDefinition;

    Identified(final @NonNull Class<D> type, final @NonNull String id) {
      super(type);
      this.id = id;
    }

    @Override
    public @NonNull D get() {
      if(this.definition != null) {
        return this.definition;
      }
      return this.proxy();
    }

    private D feature() {
      if(!this.defined()) {
        throw new IllegalStateException("feature of type " + this.toString() + " has not been defined");
      }
      return this.definition;
    }

    private D proxy() {
      if(this.proxiedDefinition == null) {
        this.proxiedDefinition = Proxies.create(this.type.getClassLoader(), this.type, Collections.singletonList(Proxied.class), new ProxiedDefinition());
      }
      return this.proxiedDefinition;
    }

    @Override
    public String toString() {
      return this.type.getName() + " with id '" + this.id + '\'';
    }

    class ProxiedDefinition extends MethodHandleInvocationHandler {
      @Override
      protected @NonNull Object object(final @NonNull Method method) {
        return Identified.this.feature();
      }
    }
  }
}
