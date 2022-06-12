package org.example;

public class Calculator {
    public Result plus(Operand op1, Operand op2) {
        return Result.of(op1.plus(op2).getValue());
    }

    public Result minus(Operand op1, Operand op2) {
        return Result.of(op1.minus(op2).getValue());
    }
}
