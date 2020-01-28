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
package net.kyori.fragment.filter;

import net.kyori.feature.parser.FeatureDefinitionParserBinder;
import net.kyori.fragment.filter.impl.AllFilterParser;
import net.kyori.fragment.filter.impl.AnyFilterParser;
import net.kyori.fragment.filter.impl.NotFilterParser;
import net.kyori.violet.AbstractModule;
import net.kyori.xml.node.parser.ParserBinder;

@Deprecated
public final class FilterModule extends AbstractModule {
  @Override
  protected void configure() {
    final ParserBinder parsers = new ParserBinder(this.binder());
    parsers.bindParser(Filter.class).to(FilterParser.class);

    final FeatureDefinitionParserBinder features = new FeatureDefinitionParserBinder(this.binder());
    features.bindFeatureParser(Filter.class).to(FilterParser.class);

    final FilterBinder filters = new FilterBinder(this.binder());
    filters.bindFilter("all").to(AllFilterParser.class);
    filters.bindFilter("any").to(AnyFilterParser.class);
    filters.bindFilter("not").to(NotFilterParser.class);
  }
}
