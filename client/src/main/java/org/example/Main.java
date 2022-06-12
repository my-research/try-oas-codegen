package org.example;

public class Main {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();

        Operand left = Operand.of(100);
        Operand right = Operand.of(200);

        Result result = calculator.plus(left, right);

        System.out.println(result);

        Operand newLeft = result.toOperand();

        Result newResult = calculator.minus(newLeft, right);

        System.out.println(newResult);
    }
}
