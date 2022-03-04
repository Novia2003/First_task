package ru.vsu.cs.novichikhin;

import java.util.Locale;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Locale.setDefault(Locale.ROOT);

        Formula formula = new Formula();
        String line = readFormula();
        formula.prepare(line);
        String values = readValues();
        double result = formula.execute(values);

        writeResult(result);
    }

    private static String readFormula() {
        System.out.println("Введите формулу : ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private static String readValues() {
        System.out.println("Введите через запятую значения переменных в порядке, в котором они встречаются в формуле");
        System.out.println("p.s. если переменная встречается более одного раза, то не нужно вводить её значение повторно :");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private static void writeResult(double result) {
        System.out.print("Результат вычислений после подстановки значений в формулу равен: ");
        System.out.print(result);
    }
}
