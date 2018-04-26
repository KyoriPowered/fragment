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
package net.kyori.fragment.parser;

import com.google.inject.TypeLiteral;
import net.kyori.xml.XMLException;
import net.kyori.xml.node.Node;
import net.kyori.xml.node.parser.Parser;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

public final class EnumParser<E extends Enum<E>> implements Parser<E> {
  private final TypeLiteral<E> type;
  private final Map<String, E> map = new HashMap<>();

  @Inject
  private EnumParser(final TypeLiteral<E> type) {
    this.type = type;
  }

  private void loadMap() {
    if(this.map.isEmpty()) {
      for(final E constant : ((Class<E>) this.type.getRawType()).getEnumConstants()) {
        this.map.put(constant.name().toLowerCase(Locale.ENGLISH), constant);
      }
    }
  }

  @Override
  public E throwingParse(final Node node) throws XMLException {
    this.loadMap();
    /* @Nullable */ final E constant = this.map.get(node.value().toLowerCase(Locale.ENGLISH).replace(' ', '_'));
    if(constant != null) {
      return constant;
    }
    throw new XMLException("Could not find " + this.type.getRawType().getName() + " with name '" + node.value() + '\'');
  }
}
