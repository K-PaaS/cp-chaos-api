package org.container.platform.chaos.api.metrics.custom;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.ObjectUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Quantity 클래스
 *
 * @author Luna
 * @version 1.0
 * @since 2024-09-02
 */
@JsonAdapter(Quantity.QuantityAdapter.class)
public class Quantity {

    private final BigDecimal number;
    private final Format format;

    public enum Format {
        DECIMAL_EXPONENT(10),
        DECIMAL_SI(10),
        BINARY_SI(2);

        private final int base;

        Format(final int base) {
            this.base = base;
        }
        public int getBase() {
            return base;
        }

    }

    public Quantity(final BigDecimal number, final Format format) {
        this.number = number;
        this.format = format;
    }
    public BigDecimal getNumber() {
        return number;
    }

    public Format getFormat() {
        return format;
    }

    public Quantity(final String value) {
        final Quantity quantity = fromString(value);
        this.number = quantity.number;
        this.format = quantity.format;
    }

    public static Quantity fromString(final String value) {
        return new QuantityFormatter().parse(value);
    }

    public String toSuffixedString() {
        return new QuantityFormatter().format(this);
    }

    @Override
    public String toString() {
        return "Quantity{" + "number=" + number + ", format=" + format + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Quantity otherQuantity = (Quantity) o;

        return ObjectUtils.compare(this.number, otherQuantity.number) == 0
                && Objects.equals(this.format, otherQuantity.format);
    }

    public static class QuantityAdapter extends TypeAdapter<Quantity> {
        @Override
        public void write(JsonWriter jsonWriter, Quantity quantity) throws IOException {
            jsonWriter.value(quantity != null ? quantity.toSuffixedString() : null);
        }

        @Override
        public Quantity read(JsonReader jsonReader) throws IOException {
            return Quantity.fromString(jsonReader.nextString());
        }
    }
}