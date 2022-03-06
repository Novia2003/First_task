package ru.vsu.cs.novichikhin;

import org.junit.Assert;

public class FormulaTest {

    @org.junit.Test
    public void testExecuteForLineWithAllVariables() {
        String line = "x*x+y";
        String values = "2, 5.7";
        double expectedResult = 9.7;

        Formula formula = new Formula();
        formula.prepare(line);

        double result = formula.execute(values);

        Assert.assertEquals(expectedResult, result, 0.000000000000000001);
    }

    @org.junit.Test
    public void testExecuteForOneVariable() {
        String line = "7.6/a*8-7";
        String values = "3.8";
        double expectedResult = 9;

        Formula formula = new Formula();
        formula.prepare(line);

        double result = formula.execute(values);

        Assert.assertEquals(expectedResult, result, 0.000000000000000001);
    }

    @org.junit.Test
    public void testExecuteForNegativeValue() {
        String line = "-d+c*(d*0.1-(c+5)/4.5)+21";
        String values = "-3.5, -0.5";
        double expectedResult = 25.175;

        Formula formula = new Formula();
        formula.prepare(line);

        double result = formula.execute(values);

        Assert.assertEquals(expectedResult, result, 0.000000000000000001);
    }

    @org.junit.Test
    public void testExecuteForNegativeResult() {
        String line = "((-v)+(-9.36)+c)/(0-2.5*m)";
        String values = "-6, -8.64 -10";
        double expectedResult = -0.48;

        Formula formula = new Formula();
        formula.prepare(line);

        double result = formula.execute(values);

        Assert.assertEquals(expectedResult, result, 0.000000000000000001);
    }

    @org.junit.Test
    public void testExecuteForDifficultFormula() {
        String line = "8.694+c/(123-v*f-c*a/(a-c-(c+v)*d)+4.94)+x";
        String values = "300, 800 90 5 -600 -3";
        double expectedResult = 5.689825916350399;

        Formula formula = new Formula();
        formula.prepare(line);

        double result = formula.execute(values);

        Assert.assertEquals(expectedResult, result, 0.000000000000000001);
    }
}