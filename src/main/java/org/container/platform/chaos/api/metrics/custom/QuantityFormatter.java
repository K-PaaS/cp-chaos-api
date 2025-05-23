/*
Copyright 2020 The Kubernetes Authors.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.container.platform.chaos.api.metrics.custom;

import org.apache.commons.lang3.tuple.Pair;
import java.math.BigDecimal;
import java.math.MathContext;

public class QuantityFormatter {

  private static final String PARTS_RE = "[eEinumkKMGTP]+";

  public Quantity parse(final String value) {
    if (value == null || value.isEmpty()) {
      throw new QuantityFormatException("");
    }
    final String[] parts = value.split(PARTS_RE);
    final BigDecimal numericValue = parseNumericValue(parts[0]);
    final String suffix = value.substring(parts[0].length());
    final BaseExponent baseExponent = new SuffixFormatter().parse(suffix);
    final BigDecimal unitMultiplier =
            BigDecimal.valueOf(baseExponent.getBase())
                    .pow(baseExponent.getExponent(), MathContext.DECIMAL64);
    final BigDecimal unitlessValue = numericValue.multiply(unitMultiplier);
    return new Quantity(unitlessValue, baseExponent.getFormat());
  }

  private static BigDecimal parseNumericValue(String part) {
    try {
      return new BigDecimal(part);
    } catch (final NumberFormatException e) {
      throw new QuantityFormatException("Unable to parse numeric part of quantity: " + part);
    }
  }

  public String format(final Quantity quantity) {
      return switch (quantity.getFormat()) {
          case DECIMAL_SI, DECIMAL_EXPONENT -> toBase10String(quantity);
          case BINARY_SI -> {
              if (isFractional(quantity)) {
                  yield toBase10String(new Quantity(quantity.getNumber(), Quantity.Format.DECIMAL_SI));
              }
              yield toBase1024String(quantity);
          }
          default -> throw new IllegalArgumentException("Can't format a " + quantity.getFormat() + " quantity");
      };
  }

  private boolean isFractional(Quantity quantity) {
    return quantity.getNumber().scale() > 0;
  }

  private String toBase1024String(final Quantity quantity) {
    final BigDecimal amount = quantity.getNumber();
    final long value = amount.unscaledValue().longValue();
    final int exponent = -amount.scale();
    final Pair<Long, Integer> resultAndTimes = removeFactorsForBase(value, 1024);
    return resultAndTimes.getLeft()
        + new SuffixFormatter()
            .format(quantity.getFormat(), exponent + resultAndTimes.getRight() * 10);
  }

  private String toBase10String(final Quantity quantity) {
    final BigDecimal amount = quantity.getNumber();
    final long value = amount.unscaledValue().longValue();
    final int exponent = -amount.scale();
    final Pair<Long, Integer> resultAndTimes = removeFactorsForBase(value, 10);
    final int postFactoringExponent = exponent + resultAndTimes.getRight();
    final Pair<Long, Integer> valueAndExponent =
        ensureExponentIsMultipleOf3(resultAndTimes.getLeft(), postFactoringExponent);
    return valueAndExponent.getLeft()
        + new SuffixFormatter().format(quantity.getFormat(), valueAndExponent.getRight());
  }

  private Pair<Long, Integer> ensureExponentIsMultipleOf3(final long mantissa, final int exponent) {
    final long exponentRemainder = exponent % 3;
    if (exponentRemainder == 1 || exponentRemainder == -2) {
      return Pair.of(mantissa * 10, exponent - 1);
    } else if (exponentRemainder == -1 || exponentRemainder == 2) {
      return Pair.of(mantissa * 100, exponent - 2);
    } else {
      return Pair.of(mantissa, exponent);
    }
  }

  private Pair<Long, Integer> removeFactorsForBase(final long value, final int base) {
    int times = 0;
    long result = value;
    while (result >= base && result % base == 0) {
      times++;
      result = result / base;
    }
    return Pair.of(result, times);
  }
}
