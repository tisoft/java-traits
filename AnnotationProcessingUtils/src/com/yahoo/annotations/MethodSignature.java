/**
 * Copyright 2014 Yahoo Inc.
 *
 * See the file "LICENSE" for the full license governing this code.
 */
package com.yahoo.annotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodSignature {

    private String methodName;
    private TypeName returnType;
    private List<TypeName> argTypes = new ArrayList<TypeName>();

    public MethodSignature(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public TypeName getReturnType() {
        return returnType;
    }

    public void setReturnType(TypeName returnType) {
        this.returnType = returnType;
    }

    public List<TypeName> getArgTypes() {
        return argTypes;
    }

    public void addArgType(TypeName... types) {
        argTypes.addAll(Arrays.asList(types));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((argTypes == null) ? 0 : argTypes.hashCode());
        result = prime * result
                + ((methodName == null) ? 0 : methodName.hashCode());
        result = prime * result
                + ((returnType == null) ? 0 : returnType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MethodSignature other = (MethodSignature) obj;
        if (argTypes == null) {
            if (other.argTypes != null)
                return false;
        } else if (!compareTypeList(argTypes, other.argTypes))
            return false;
        if (methodName == null) {
            if (other.methodName != null)
                return false;
        } else if (!methodName.equals(other.methodName))
            return false;
        if (returnType == null) {
            if (other.returnType != null)
                return false;
        } else if (!compareTypes(returnType, other.returnType))
            return false;
        return true;
    }

    private boolean compareTypeList(List<TypeName> l1, List<TypeName> l2) {
        if (l1.size() != l2.size())
            return false;
        for (int i = 0; i < l1.size(); i++) {
            if (!compareTypes(l1.get(i), l2.get(i)))
                return false;
        }
        return true;
    }

    private boolean compareTypes(TypeName t1, TypeName t2) {
        if (!t1.equals(t2))
            return false;
        if (!(t1.getArrayDepth() == t2.getArrayDepth() && t1.isVarArgs() == t2.isVarArgs()))
            return false;
        if (t1 instanceof ClassName && t2 instanceof ClassName)
            return compareTypeList(((ClassName) t1).getTypeArgs(), ((ClassName) t2).getTypeArgs());
        return true;
    }


}
