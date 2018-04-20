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

import net.kyori.fragment.feature.Feature;
import net.kyori.fragment.feature.context.FeatureContext;
import net.kyori.fragment.filter.impl.FilterReferenceParser;
import net.kyori.lunar.EvenMoreObjects;
import net.kyori.lunar.exception.Exceptions;
import net.kyori.xml.XMLException;
import net.kyori.xml.node.Node;
import net.kyori.xml.parser.Parser;
import org.jdom2.Element;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public final class RootFilterParserImpl implements Parser<Filter> {
  private final Map<String, Parser<? extends Filter>> parsers;
  private final FilterReferenceParser refParser;
  @Inject private Provider<FeatureContext> featureContext;

  @Inject
  private RootFilterParserImpl(final Map<String, Parser<? extends Filter>> parsers, final FilterReferenceParser refParser) {
    this.parsers = parsers;
    this.refParser = refParser;
  }

  @Override
  public Filter throwingParse(final Node node) throws XMLException {
    /* @Nullable */ Parser<? extends Filter> parser = this.parsers.get(node.name());
    if(parser != null) {
      return this.featureContext.get().add(Filter.class, node, parser.parse(node));
    } else if(node.attribute(Filter.REFERENCE_ID).isPresent()) {
      if(node.elements().findFirst().isPresent()) {
        throw new XMLException("Could not parse element with name '" + node.name() + "' as a reference as it has children");
      } else {
        return this.refParser.throwingParse(Node.of(EvenMoreObjects.make(new Element("hack"), Exceptions.rethrowConsumer(element -> {
          element.setAttribute(Feature.ID_ATTRIBUTE_NAME, node.requireAttribute(Filter.REFERENCE_ID).value());
        }))));
      }
    } else {
      throw new XMLException("Could not find filter parser with name '" + node.name() + '\'');
    }
  }
}
