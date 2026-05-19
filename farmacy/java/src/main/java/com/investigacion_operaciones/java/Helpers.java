package com.investigacion_operaciones.java;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class Helpers {

    public void printLines(String... lines) {
        for (String line : lines) {
            System.out.println(line);
        }
    }

    public void printSection(String title) {
        System.out.println(title);
    }

    public void printBlankLine() {
        System.out.println();
    }

    public String joinTerms(List<String> terms) {
        return String.join(" + ", terms);
    }

    public double getValueOrZero(Variable variable) {
        BigDecimal value = variable.getValue();
        return value != null ? value.doubleValue() : 0.0;
    }

    public String toPythonLikeStatus(Optimisation.Result result) {
        String state = String.valueOf(result.getState());
        if ("OPTIMAL".equalsIgnoreCase(state)) {
            return "Optimal";
        }
        if ("INFEASIBLE".equalsIgnoreCase(state)) {
            return "Infeasible";
        }
        return state;
    }

    public String formatPythonFloat(double value) {
        return String.format(Locale.US, "%.1f", value);
    }
}
