package org.example;

public class Main {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();

        Operand left = Operand.of(100);
        Operand right = Operand.of(200);

        Result result = calculator.calculate(left, right);

        System.out.println(result);
    }
}
