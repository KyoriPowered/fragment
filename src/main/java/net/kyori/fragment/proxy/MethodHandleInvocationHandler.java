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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.kyori.lunar.exception.Exceptions;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

public abstract class MethodHandleInvocationHandler implements InvocationHandler {
  private static final LoadingCache<Method, MethodHandle> SHARED_CACHE = CacheBuilder.newBuilder().build(CacheLoader.from(MoreMethodHandles::unreflect));
  private final Cache<Method, MethodHandle> cache = CacheBuilder.newBuilder().build();

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    return this.handle(method).invokeWithArguments(args);
  }

  private MethodHandle handle(final Method method) {
    try {
      return this.cache.get(method, () -> {
        final Object object = this.object(method);
        return SHARED_CACHE.getUnchecked(method).bindTo(object);
      });
    } catch(final ExecutionException e) {
      throw Exceptions.propagate(e);
    }
  }

  @Nullable
  protected abstract Object object(final Method method);
}
