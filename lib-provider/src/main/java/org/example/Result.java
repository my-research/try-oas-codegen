package org.example;

import lombok.Value;

@Value(staticConstructor = "of")
public class Result {
    long value;

    public Operand toOperand() {
        return Operand.of(value);
    }
}
