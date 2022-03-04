package ru.vsu.cs.novichikhin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formula {

    private List<String> processedFormula;

    public void prepare(String originalFormula) {
        String formula = originalFormula.replaceAll(" ", "");

        List<String> variablesAndFormula = new ArrayList<>();
        Pattern alphabet = Pattern.compile("[a-z]");
        Matcher matcher = alphabet.matcher(formula);

        while (matcher.find()) {
            if (isThisVariableNotWas(matcher.group(), variablesAndFormula))
                variablesAndFormula.add(matcher.group());
        }

        variablesAndFormula.add(formula);

        processedFormula = variablesAndFormula;
    }

    private boolean isThisVariableNotWas(String x, List<String> variables) {
        for (String variable : variables) {
            if (variable.equals(x)) {
                return false;
            }
        }
        return true;
    }

    public double execute(String originalLineWithNumbers) {
        String lineWithNumbers = originalLineWithNumbers.replaceAll(" ", "");
        String[] numbers = lineWithNumbers.split(",");
        String formula = processedFormula.get(processedFormula.size() - 1);
        for (int i = 0; i < processedFormula.size() - 1; i++) {
            formula = replaceVariableWithNumber(formula, processedFormula.get(i), numbers[i]);
        }

        String result = parseFormulaIntoExpressionInBrackets(formula);

        if (result.contains("-")) {
            result = result.substring(1, result.length() - 1);
        }

        return Double.parseDouble(result);
    }

    private String replaceVariableWithNumber(String formula, String variable, String number) {
        if (Double.parseDouble(number) < 0) {
            number = "(" + number + ")";
        }
        return formula.replaceAll(variable, number);
    }

    private String parseFormulaIntoExpressionInBrackets(String formula) {
        String result;

        while (formula.contains("(") && formula.contains(")")) {

            int indexFirstInnermostBracket = formula.lastIndexOf("(");
            int indexSecondInnermostBracket = formula.indexOf(")", indexFirstInnermostBracket);

            if (indexSecondInnermostBracket != -1 && indexFirstInnermostBracket != -1) {

                String expressionInBrackets = formula.substring(indexFirstInnermostBracket + 1, indexSecondInnermostBracket);
                StringBuilder builder = new StringBuilder(formula);

                if (expressionInBrackets.lastIndexOf("-") == 0 && !expressionInBrackets.contains("+") &&
                        !expressionInBrackets.contains("*") && !expressionInBrackets.contains("/")) {

                    builder.setCharAt(indexFirstInnermostBracket, '{');
                    builder.setCharAt(indexSecondInnermostBracket, '}');

                } else {

                    String number = calculateExpressionInBrackets(expressionInBrackets);
                    builder.replace(indexFirstInnermostBracket, indexSecondInnermostBracket + 1, number);

                }

                formula = String.valueOf(builder);
            }
        }
        result = calculateExpressionInBrackets(formula);
        return result;
    }

    private String calculateExpressionInBrackets(String expression) {
        if (expression.charAt(0) == '-') {
            expression = "0" + expression;
        }

        List<String> allSymbols = Arrays.asList("*", "/", "+", "-");

        while (!((findIndexLastRequiredSymbol(allSymbols, expression) == -1) ||
                (expression.lastIndexOf("{") == 0 && findIndexLastRequiredSymbol(allSymbols, expression) == 1))) {

            int indexFirstSymbol, indexMiddleSymbol, indexLastSymbol;

            List<String> firstOrLastSymbols, middleSymbols;
            List<String> multiplicationAndDivision = Arrays.asList("*", "/");

            if (findIndexLastRequiredSymbol(multiplicationAndDivision, expression) != -1) {
                firstOrLastSymbols = allSymbols;
                middleSymbols = multiplicationAndDivision;
            } else {
                firstOrLastSymbols = middleSymbols = Arrays.asList("+", "-");
            }

            indexMiddleSymbol = findIndexFirstRequiredSymbol(middleSymbols, expression);
            if (expression.charAt(indexMiddleSymbol - 1) == '{') {
                String partExpression = expression.substring(indexMiddleSymbol + 1);
                indexMiddleSymbol = findIndexFirstRequiredSymbol(middleSymbols, partExpression) + indexMiddleSymbol + 1;
            }

            String firstPart = expression.substring(0, indexMiddleSymbol);
            String secondPart = expression.substring(indexMiddleSymbol + 1);

            double firstNumber, secondNumber;

            indexFirstSymbol = findIndexLastRequiredSymbol(firstOrLastSymbols, firstPart);
            if (expression.charAt(indexMiddleSymbol - 1) == '}') {
                indexFirstSymbol = firstPart.lastIndexOf('{') - 1;
                firstNumber = Double.parseDouble(firstPart.substring(indexFirstSymbol + 2, firstPart.length() - 1));
            } else {
                firstNumber = Double.parseDouble(expression.substring(indexFirstSymbol + 1, indexMiddleSymbol));
            }

            indexLastSymbol = findIndexFirstRequiredSymbol(firstOrLastSymbols, secondPart);
            if (expression.charAt(indexMiddleSymbol + 1) == '{') {
                secondNumber = Double.parseDouble(secondPart.substring(indexLastSymbol, secondPart.indexOf('}')));
                indexLastSymbol = indexMiddleSymbol + secondPart.indexOf('}') + 2;
            } else {

                if (indexLastSymbol == -1) {
                    indexLastSymbol = expression.length();
                } else {
                    indexLastSymbol = indexMiddleSymbol + indexLastSymbol + 1;
                }
                secondNumber = Double.parseDouble(expression.substring(indexMiddleSymbol + 1, indexLastSymbol));
            }

            String symbol = Character.toString(expression.charAt(indexMiddleSymbol));
            String result = findResult(symbol, firstNumber, secondNumber);
            StringBuilder builder = new StringBuilder(expression);

            expression = String.valueOf(builder.replace(indexFirstSymbol + 1, indexLastSymbol, result));
        }
        return expression;
    }


    private int findIndexFirstRequiredSymbol(List<String> symbols, String expression) {
        int index = expression.length();

        for (String symbol : symbols) {
            if (expression.contains(symbol) && expression.indexOf(symbol) < index) {
                index = expression.indexOf(symbol);
            }
        }

        if (index == expression.length()) {
            index = -1;
        }

        return index;
    }

    private int findIndexLastRequiredSymbol(List<String> symbols, String expression) {
        int index = -1;

        for (String symbol : symbols) {
            if (expression.contains(symbol) && expression.lastIndexOf(symbol) > index) {
                index = expression.lastIndexOf(symbol);
            }
        }

        return index;
    }

    private String findResult(String symbol, double firstNumber, double secondNumber) {
        String result;
        switch (symbol) {
            case ("*") -> result = Double.toString(firstNumber * secondNumber);
            case ("/") -> result = Double.toString(firstNumber / secondNumber);
            case ("+") -> result = Double.toString(firstNumber + secondNumber);
            case ("-") -> result = Double.toString(firstNumber - secondNumber);
            default -> result = null;
        }

        assert result != null;
        if (Double.parseDouble(result) < 0) {
            result = "{" + result + "}";
        }
        return result;
    }
}



//  x*x +y
// 2*( x+x+(x+(y+(x*y)+(x+x)))+y)
//  2, 5.7
// - x+y*(x - (a - b* c)*d)
// 2 ,  2.1, -3, -3.1,   5, 0

