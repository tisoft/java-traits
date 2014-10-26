package com.yahoo.annotations.writer.expressions;

import java.io.IOException;

import com.yahoo.annotations.utils.Utils;
import com.yahoo.annotations.writer.JavaFileWriter;

abstract class Reference implements Expression {

    private final String fieldName;
    
    public Reference(String fieldName) {
        if (Utils.isEmpty(fieldName)) {
            throw new IllegalArgumentException("fieldName can't be numm for a FieldReference expression");
        }
        this.fieldName = fieldName;
    }
    
    @Override
    public void writeExpression(JavaFileWriter writer) throws IOException {
        writeReferencedObject(writer);
        writer.appendString(fieldName);
    }
    
    protected abstract void writeReferencedObject(JavaFileWriter writer) throws IOException;
    
}
