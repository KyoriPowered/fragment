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
package net.kyori.fragment.proxy;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.kyori.lunar.EvenMoreObjects;
import net.kyori.lunar.exception.Exceptions;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// TODO(kashike): This can be removed when we're running on Java 9.
public final class MoreMethodHandles {
  private static final Constructor<MethodHandles.Lookup> LOOKUP_CONSTRUCTOR = EvenMoreObjects.get(Exceptions.rethrowSupplier(() -> {
    final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
    constructor.setAccessible(true);
    return constructor;
  }));
  private static final LoadingCache<Class<?>, MethodHandles.Lookup> LOOKUPS = CacheBuilder.newBuilder().build(CacheLoader.from(requestedLookupClass -> {
    try {
      return LOOKUP_CONSTRUCTOR.newInstance(requestedLookupClass);
    } catch(final InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw Exceptions.rethrow(e);
    }
  }));

  private MoreMethodHandles() {
  }

  private static MethodHandles.Lookup privateLookup(final Class<?> requestedLookupClass) {
    return LOOKUPS.getUnchecked(requestedLookupClass);
  }

  private static MethodHandle unreflect(final Class<?> requestedLookupClass, final Method method) {
    try {
      return privateLookup(requestedLookupClass).unreflect(method);
    } catch(final IllegalAccessException e) {
      throw Exceptions.rethrow(e);
    }
  }

  public static MethodHandle unreflect(final Method method) {
    return unreflect(method.getDeclaringClass(), method);
  }
}
