package com.yahoo.annotations.writer.expressions;

import com.yahoo.annotations.writer.JavaFileWriter;

import java.io.IOException;

class AssignmentExpression extends Expression {

    private Expression assignTo;
    private Expression assignFrom;
    
    public AssignmentExpression(Expression assignTo, Expression assignFrom) {
        this.assignTo = assignTo;
        this.assignFrom = assignFrom;
    }
    
    @Override
    public boolean writeExpression(JavaFileWriter writer) throws IOException {
        assignTo.writeExpression(writer);
        writer.appendString(" = ");
        assignFrom.writeExpression(writer);
        return true;
    }
    
}