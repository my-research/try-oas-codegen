package org.example;

public class Calculator {
    public Result calculate(Operand op1, Operand op2) {
        return Result.of(op1.plus(op2).getValue());
    }
}
