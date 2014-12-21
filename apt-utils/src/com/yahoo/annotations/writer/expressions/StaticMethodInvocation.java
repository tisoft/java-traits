package com.yahoo.annotations.writer.expressions;

import com.yahoo.annotations.model.DeclaredTypeName;
import com.yahoo.annotations.utils.AptUtils;
import com.yahoo.annotations.writer.JavaFileWriter;

import java.io.IOException;
import java.util.List;

class StaticMethodInvocation extends Expression {

    private final DeclaredTypeName calledType;
    private final String methodName;
    private final List<?> arguments;
    
    public StaticMethodInvocation(DeclaredTypeName calledType, String methodName, Object... arguments) {
        this(calledType, methodName, AptUtils.asList(arguments));
    }
    
    public StaticMethodInvocation(DeclaredTypeName calledType, String methodName, List<?> arguments) {
        this.calledType = calledType;
        this.methodName = methodName;
        this.arguments = arguments;
    }
    
    @Override
    public boolean writeExpression(JavaFileWriter writer) throws IOException {
        if (calledType != null) {
            writer.appendString(writer.shortenNameForStaticReference(calledType)).appendString(".");
        }
        writer.appendString(methodName).writeArgumentNameList(arguments);
        return true;
    }
    
}