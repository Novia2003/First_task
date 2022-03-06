package ru.vsu.cs.novichikhin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formula {

    private List<String> variables;
    private String processedFormula;
    private int errorNumber = 0;

    public Formula() {
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public void prepare(String originalFormula) {
        String formula = originalFormula.replaceAll(" ", "");
        processedFormula = formula;

        List<String> variables = new ArrayList<>();
        Pattern allSymbols = Pattern.compile("[^a-z0-9+*/().-]");
        Matcher firstMatcher = allSymbols.matcher(formula);

        if (firstMatcher.find()) {
            errorNumber = 1;
            return;
        }


        Pattern firstBracket = Pattern.compile("[(]");
        int quantityFirstBrackets = findQuantityBracket(firstBracket);
        Pattern secondBracket = Pattern.compile("[)]");
        int quantitySecondBrackets = findQuantityBracket(secondBracket);

        if (quantityFirstBrackets != quantitySecondBrackets) {
            errorNumber = 1;
            return;
        }

        Pattern alphabet = Pattern.compile("[a-z]");
        Matcher secondMatcher = alphabet.matcher(formula);

        while (secondMatcher.find()) {
            if (isThisVariableNotWas(secondMatcher.group(), variables))
                variables.add(secondMatcher.group());
        }

        if (variables.size() == 0) {
            errorNumber = 1;
            return;
        }

        this.variables = variables;
    }

    private boolean isThisVariableNotWas(String x, List<String> variables) {
        for (String variable : variables) {
            if (variable.equals(x)) {
                return false;
            }
        }
        return true;
    }

    private int findQuantityBracket(Pattern bracket) {
        int quantity = 0;
        Matcher matcher = bracket.matcher(processedFormula);

        while (matcher.find()) {
            quantity++;
        }
        return quantity;
    }

    public double execute(String lineWithNumbers) {
        List<String> numbers = findNumbers(lineWithNumbers);

        if (numbers.size() == variables.size() && errorNumber == 0) {

            String formula = processedFormula;

            for (int i = 0; i < variables.size(); i++) {

                if (Double.parseDouble(numbers.get(i)) < 0) {
                    numbers.set(i, "(" + numbers.get(i) + ")");
                }

                formula = formula.replaceAll(variables.get(i), numbers.get(i));
            }

            String result = parseFormulaIntoExpressionInBrackets(formula);

            if (result.contains("-")) {
                result = result.substring(1, result.length() - 1);
            }

            return Double.parseDouble(result);

        } else {
            if (errorNumber == 0) {
                errorNumber = 2;
            }
            return 0;
        }
    }

    private List<String> findNumbers(String lineWithNumbers) {
        List<String> numbers = new ArrayList<>();

        Pattern pattern = Pattern.compile("[-]?[0-9]+[.]?[0-9]*");
        Matcher matcher = pattern.matcher(lineWithNumbers);

        while (matcher.find()) {
            numbers.add(matcher.group());
        }

        return numbers;
    }

    private String parseFormulaIntoExpressionInBrackets(String formula) {
        String result;

        while (formula.contains("(") && formula.contains(")") && errorNumber == 0) {

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

        while (!((findIndexLastRequiredSymbol(allSymbols, expression) == -1) || (expression.lastIndexOf("{") == 0 &&
                findIndexLastRequiredSymbol(allSymbols, expression) == 1)) && errorNumber == 0) {

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
        double result = 0;

        if (symbol.equals("/") && secondNumber == 0) {
            errorNumber = 3;
        }

        if (symbol.equals("*")) {
            result = firstNumber * secondNumber;
        }

        if (symbol.equals("+")) {
            result = firstNumber + secondNumber;
        }

        if (symbol.equals("-")) {
            result = firstNumber - secondNumber;
        }

        if (symbol.equals("/")) {
            result = firstNumber / secondNumber;
        }


        if (result < 0) {
            return "{" + result + "}";
        }

        return Double.toString(result);
    }
}