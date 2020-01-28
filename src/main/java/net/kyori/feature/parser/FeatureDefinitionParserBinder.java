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

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import net.kyori.feature.FeatureDefinition;
import net.kyori.violet.FriendlyTypeLiteral;
import net.kyori.violet.TypeArgument;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A feature definition parser binder.
 */
@Deprecated
public class FeatureDefinitionParserBinder {
  private final Binder binder;

  public FeatureDefinitionParserBinder(final Binder binder) {
    this.binder = binder;
  }

  /**
   * Creates a binding builder for binding a feature definition parser for {@code D}.
   *
   * @param type the type
   * @param <D> the type
   * @return the parser binding binder
   */
  public <D extends FeatureDefinition> @NonNull AnnotatedBindingBuilder<FeatureDefinitionParser<D>> bindFeatureParser(final Class<D> type) {
    return this.bindFeatureParser(TypeLiteral.get(type));
  }

  /**
   * Creates a binding builder for binding a feature definition parser for {@code D}.
   *
   * @param type the type
   * @param <D> the type
   * @return the parser binding binder
   */
  public <D extends FeatureDefinition> @NonNull AnnotatedBindingBuilder<FeatureDefinitionParser<D>> bindFeatureParser(final TypeLiteral<D> type) {
    return this.binder.bind(new FriendlyTypeLiteral<FeatureDefinitionParser<D>>() {}.where(new TypeArgument<D>(type) {}));
  }
}
