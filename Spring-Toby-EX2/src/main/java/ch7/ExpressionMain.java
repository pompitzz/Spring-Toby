package ch7;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class ExpressionMain {
    public static void main(String[] args) {
        ExpressionParser parser = new SpelExpressionParser();

        Expression ex = parser.parseExpression("1+2");
        int result = (int) ex.getValue();


    }
}
