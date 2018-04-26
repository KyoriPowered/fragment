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

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import net.kyori.fragment.feature.Feature;
import net.kyori.violet.FriendlyTypeLiteral;
import net.kyori.violet.TypeArgument;

public class FeatureParserBinder {
  private final Binder binder;

  public FeatureParserBinder(final Binder binder) {
    this.binder = binder;
  }

  public <F extends Feature> void bindFeatureParser(final Class<F> type) {
    this.bindFeatureParser(TypeLiteral.get(type));
  }

  public <F extends Feature> void bindFeatureParser(final TypeLiteral<F> type) {
    this.binder.bind(new FriendlyTypeLiteral<FeatureParser<F>>() {}.where(new TypeArgument<F>(type) {})).to(new FriendlyTypeLiteral<FeatureParserImpl<F>>() {}.where(new TypeArgument<F>(type) {}));
  }
}