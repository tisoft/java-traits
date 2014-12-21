package com.yahoo.annotations.visitors;

import com.yahoo.annotations.model.DeclaredTypeName;
import com.yahoo.annotations.model.GenericName;
import com.yahoo.annotations.model.TypeName;
import com.yahoo.annotations.model.TypeName.TypeNameVisitor;

import java.util.List;
import java.util.Set;

/**
 * A {@link com.yahoo.annotations.model.TypeName.TypeNameVisitor} used to accumulate required imports from
 * {@link com.yahoo.annotations.model.TypeName}s. Basically just includes any class referenced by the
 * {@link com.yahoo.annotations.model.TypeName} (e.g. itself, upper/lower bounds, etc.)
 */
public class ImportGatheringTypeNameVisitor implements TypeNameVisitor<Void, Set<DeclaredTypeName>> {

    @Override
    public Void visitClassName(DeclaredTypeName typeName, Set<DeclaredTypeName> imports) {
        imports.add(typeName);
        List<? extends TypeName> typeArgs = typeName.getTypeArgs();
        if (typeArgs != null) {
            for (TypeName arg : typeArgs) {
                arg.accept(this, imports);
            }
        }
        return null;
    }

    @Override
    public Void visitGenericName(GenericName genericName, Set<DeclaredTypeName> imports) {
        List<? extends TypeName> extendsBounds = genericName.getExtendsBound();
        if (extendsBounds != null) {
            for (TypeName extendsBound : extendsBounds) {
                extendsBound.accept(this, imports);
            }
        }
        TypeName superBound = genericName.getSuperBound();
        if (superBound != null) {
            superBound.accept(this, imports);
        }
        return null;
    }
}