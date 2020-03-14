package ch6.step7;

import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/15
 */
class TargetTest {
    @Test
    void execution() throws Exception {
        System.out.println(Target.class.getMethod("minus", int.class, int.class));
    }

    @Test
    void methodSignaturePointcut() throws Exception {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public int ch6.step7.Target.minus(int, int) throws java.lang.RuntimeException)");

        assertThat(
                pointcut.getClassFilter().matches(Target.class) && pointcut.getMethodMatcher()
                        .matches(Target.class.getMethod("minus", int.class, int.class), null)
        ).isTrue();

        assertThat(
                pointcut.getClassFilter().matches(Target.class) && pointcut.getMethodMatcher()
                        .matches(Target.class.getMethod("plus", int.class, int.class), null)
        ).isFalse();

        assertThat(
                pointcut.getClassFilter().matches(Bean.class) && pointcut.getMethodMatcher()
                        .matches(Target.class.getMethod("method"), null)
        ).isFalse();
    }

    @Test
    void methodSignaturePointcut2() throws Exception {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(int minus(int, int))");

        assertThat(
                pointcut.getClassFilter().matches(Target.class) && pointcut.getMethodMatcher()
                        .matches(Target.class.getMethod("minus", int.class, int.class), null)
        ).isTrue();

        assertThat(
                pointcut.getClassFilter().matches(Target.class) && pointcut.getMethodMatcher()
                        .matches(Target.class.getMethod("plus", int.class, int.class), null)
        ).isFalse();

        assertThat(
                pointcut.getClassFilter().matches(Bean.class) && pointcut.getMethodMatcher()
                        .matches(Target.class.getMethod("method"), null)
        ).isFalse();
    }

    @Test
    void methodSignaturePointcut3() throws Exception {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* *(..))");

        assertThat(
                pointcut.getClassFilter().matches(Target.class) && pointcut.getMethodMatcher()
                        .matches(Target.class.getMethod("minus", int.class, int.class), null)
        ).isTrue();

        assertThat(
                pointcut.getClassFilter().matches(Target.class) && pointcut.getMethodMatcher()
                        .matches(Target.class.getMethod("plus", int.class, int.class), null)
        ).isTrue();

        assertThat(
                pointcut.getClassFilter().matches(Bean.class) && pointcut.getMethodMatcher()
                        .matches(Target.class.getMethod("method"), null)
        ).isTrue();
    }

    @Test
    void pointcut() throws Exception{
        targetClassPointcutMatches("execution(* *(..))", true, true, true, true, true, true);
    }

    @Test
    void pointcut2() throws Exception{
        targetClassPointcutMatches("execution(* *..TargetInterface.*(..))",
                true, true, true, true, false, false);
    }

    public void pointcutMatches(String expression, Boolean expected, Class<?> clazz, String methodName, Class<?>... args) throws Exception {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);

        assertThat(
                pointcut.getClassFilter().matches(clazz) &&
                        pointcut.getMethodMatcher().matches(clazz.getMethod(methodName, args), null)
        ).isEqualTo(expected);
    }

    public void targetClassPointcutMatches(String expression, boolean... expected) throws Exception{
        pointcutMatches(expression, expected[0], Target.class, "hello");
        pointcutMatches(expression, expected[1], Target.class, "hello", String.class);
        pointcutMatches(expression, expected[2], Target.class, "plus", int.class, int.class);
        pointcutMatches(expression, expected[3], Target.class, "minus", int.class, int.class);
        pointcutMatches(expression, expected[4], Target.class, "method");
        pointcutMatches(expression, expected[5], Bean.class, "method");
    }
}