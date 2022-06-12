package org.example;

import java.net.HttpCookie;
import lombok.Value;

@Value(staticConstructor = "of")
public class Operand {
    long value;

    public Operand plus(Operand target) {
        return Operand.of(value + target.getValue());
    }

    public Operand minus(Operand target) {
        return Operand.of(value - target.getValue());
    }
}
