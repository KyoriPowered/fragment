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

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.kyori.feature.FeatureDefinitionContext;
import net.kyori.feature.FeatureDefinitionContextImpl;
import net.kyori.fragment.test.TestFilter;
import net.kyori.fragment.test.TestQuery;
import net.kyori.lunar.EvenMoreObjects;
import net.kyori.lunar.exception.Exceptions;
import net.kyori.violet.AbstractModule;
import net.kyori.xml.document.factory.DocumentFactory;
import net.kyori.xml.node.Node;
import net.kyori.xml.node.parser.Parser;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParseTest {
  private final Node node = EvenMoreObjects.make(Exceptions.rethrowSupplier(() -> {
    final Document document = DocumentFactory.builder()
      .builder(new SAXBuilder())
      .build()
      .read(Paths.get(ParseTest.class.getResource("/test.xml").toURI()));
    return Node.of(document.getRootElement());
  }));
  private final Injector injector = Guice.createInjector(new AbstractModule() {
    @Override
    protected void configure() {
      this.bind(FeatureDefinitionContext.class).toInstance(new FeatureDefinitionContextImpl());

      this.install(new FilterModule());

      final FilterBinder filters = new FilterBinder(this.binder());
      filters.bindFilter("test").toInstance(node -> new TestFilter(Integer.parseInt(node.value())));
    }
  });

  @Test
  void testParse() {
    final Parser<Filter> parser = this.injector.getInstance(FilterParser.class);
    final List<Filter> filters = this.node.nodes()
      .flatMap(Node::nodes)
      .map(parser)
      .collect(Collectors.toList());
    final Filter test = filters.get(0);
    final Filter any = filters.get(1);
    assertEquals(FilterResponse.ALLOW, test.query((TestQuery) () -> 1024));
    assertEquals(FilterResponse.ALLOW, any.query((TestQuery) () -> 1024));
  }
}
