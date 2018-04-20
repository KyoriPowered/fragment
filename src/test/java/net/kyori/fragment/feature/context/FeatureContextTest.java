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

import com.google.common.util.concurrent.UncheckedExecutionException;
import net.kyori.fragment.feature.Feature;
import net.kyori.xml.XMLException;
import net.kyori.xml.node.Node;
import org.jdom2.Element;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureContextTest {
  private final FeatureContext context = new FeatureContextImpl();

  @Test
  void testAddThenGet() {
    final TestFeature feature = new TestFeatureImpl(69);
    this.context.add(TestFeature.class, "abc", feature);
    assertEquals(feature.value(), this.context.get(TestFeature.class, "abc").value());
  }

  @Test
  void testGetWithoutAdd() {
    final UncheckedExecutionException exception = assertThrows(UncheckedExecutionException.class, () -> this.context.get(TestFeature.class, "def").value());
    assertTrue(exception.getCause() instanceof IllegalStateException);
    System.out.println(exception.getCause().getMessage());
    assertEquals("feature of type " + TestFeature.class.getName() + " with id 'def' has not been defined", exception.getCause().getMessage());
  }

  @Test
  void testGetThenAdd() {
    final TestFeature feature = this.context.get(TestFeature.class, "def");
    this.context.add(TestFeature.class, "def", new TestFeatureImpl(69));
    assertEquals(69, feature.value());
    assertEquals(feature.value(), this.context.get(TestFeature.class, "def").value());
  }

  @Test
  void testValidate() throws XMLException {
    this.context.get(TestFeature.class, Node.of(
      new Element("test")
        .setAttribute(Feature.ID_ATTRIBUTE_NAME, "foo")
    ));
    final List<XMLException> exceptions = this.context.validate();
    assertEquals(1, exceptions.size());
    assertEquals("feature of type " + TestFeature.class.getName() + " with id 'foo' has not been defined", exceptions.get(0).getMessage());
  }

  private interface TestFeature extends Feature {
    int value();
  }

  private static class TestFeatureImpl implements TestFeature {
    private final int value;

    private TestFeatureImpl(final int value) {
      this.value = value;
    }

    @Override
    public int value() {
      return this.value;
    }
  }
}
