package com.sparta.modulecommon.common.distributedlock;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class SpELParser {

    public static Object getDynamicValue(String[] parameterNames, Object[] args, String spELString) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 파라미터와 값을 컨텍스트에 추가
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return parser.parseExpression(spELString).getValue(context, Object.class);
    }
}