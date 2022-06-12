package org.example;

import lombok.Value;

@Value(staticConstructor = "of")
public class Operand {
    long value;

    public Operand plus(Operand target) {
        return Operand.of(value + target.getValue());
    }
}
