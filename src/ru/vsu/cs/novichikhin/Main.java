package ru.vsu.cs.novichikhin;

import java.util.Locale;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Locale.setDefault(Locale.ROOT);

        Formula formula = new Formula();
        String line = readFormula();
        formula.prepare(line);

        int errorNumber = formula.getErrorNumber();

        if (errorNumber == 0) {

            String values = readValues();
            double result = formula.execute(values);

            errorNumber = formula.getErrorNumber();

            if (errorNumber == 0) {
                writeResult(result);
            } else {
                writeErrorMessage(errorNumber);
            }
        } else {
            writeErrorMessage(errorNumber);
        }
    }

    private static String readFormula() {
        System.out.println("Введите формулу : ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private static String readValues() {
        System.out.println("Введите, разделяя пробелами, значения переменных в порядке, в котором они встречаются в формуле");
        System.out.println("p.s. если переменная встречается более одного раза, то не нужно вводить её значение повторно :");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private static void writeResult(double result) {
        System.out.print("Результат вычислений после подстановки значений в формулу равен: ");
        System.out.print(result);
    }

    private static void writeErrorMessage(int errorNumber) {
        switch (errorNumber) {
            case 1 -> System.err.print("Введённая формула некорректна");
            case 2 -> System.err.print("Количество введёных значений не соответствует количеству переменных в формуле");
            case 3 -> System.err.print("В формуле присутствует деление на 0");
        }
    }
}
