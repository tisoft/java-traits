package com.sambosley.javatraits.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

public class JavaFileWriter {

    private static final String INDENT = "    ";
    
    private Writer out;
    private Map<String, List<FullyQualifiedName>> knownNames;
    private Scope currentScope = Scope.IMPORTS;
    
    private static enum Scope {
        PACKAGE,
        IMPORTS,
        TYPE_DECLARATION,
        TYPE_DEFINITION,
        FIELD_DECLARATION,
        METHOD_DECLARATION,
        METHOD_DEFINITION
    }
    
    public JavaFileWriter(Writer out) {
        if (out == null)
            throw new IllegalArgumentException("Writer must be non-null");
        this.out = out;
        this.knownNames = new HashMap<String, List<FullyQualifiedName>>();
    }
    
    public void close() throws IOException {
        out.close();
    }
    
    public void writePackage(String packageName) throws IOException {
        checkScope(Scope.PACKAGE);
        out.append("package ").append(packageName).append(";\n\n");
    }
    
    public void writeImports(Collection<FullyQualifiedName> imports) throws IOException {
        checkScope(Scope.IMPORTS);
        for (FullyQualifiedName item : imports) {
            String simpleName = item.getSimpleName();
            List<FullyQualifiedName> allNames = knownNames.get(simpleName);
            if (allNames == null) {
                allNames = new ArrayList<FullyQualifiedName>();
                knownNames.put(simpleName, allNames);
            }
            
            if (!allNames.contains(item)) {
                out.append("import ").append(item.toString()).append(";\n");
                allNames.add(item);
            }
        }
        out.append("\n");
    }
    
    public void beginTypeDeclaration(String name, String kind, List<Modifier> modifiers) throws IOException {
        checkScope(Scope.IMPORTS);
        if (modifiers != null) {
            for (Modifier mod : modifiers) {
                out.append(mod.toString()).append(" ");
            }
        }
        out.append(kind).append(" ").append(name);
        moveToScope(Scope.TYPE_DECLARATION);
    }
    
    public void appendGenericDeclaration(List<String> generics, List<FullyQualifiedName> bounds) throws IOException {
        checkScope(Scope.TYPE_DECLARATION);
        if (generics == null || generics.size() == 0)
            return;
        out.append("<");
        if (generics.size() > 0 && bounds != null && generics.size() != bounds.size())
            throw new IllegalArgumentException("Generics and bounds must have the same size");
        
        for (int i = 0; i < generics.size(); i++) {
            String generic = generics.get(i);
            FullyQualifiedName bound = bounds != null ? bounds.get(i) : null;
            
            out.append(generic);
            if (bound != null)
                out.append(" extends ").append(shortenName(bound));
            if (i < generics.size() - 1)
                out.append(", ");
        }
        out.append(">");
    }
    
    public void addSuperclassToTypeDeclaration(FullyQualifiedName superclass, List<String> generics) throws IOException {
        checkScope(Scope.TYPE_DECLARATION);
        out.append(" extends ").append(shortenName(superclass));
        if (generics != null && generics.size() > 0) {
            out.append("<");
            for (int i = 0; i < generics.size(); i++) {
                out.append(generics.get(i));
                if (i < generics.size() - 1)
                    out.append(", ");
            }
            out.append(">");
        }
    }
    
    public void addInterfacesToTypeDeclaration(List<FullyQualifiedName> interfaces, List<List<String>> generics) throws IOException {
        checkScope(Scope.TYPE_DECLARATION);
        if (interfaces != null && generics != null && interfaces.size() != generics.size())
            throw new IllegalArgumentException("When specifying generics for implementing interfaces, lists must be the same size");
        out.append(" implements ");
        for (int i = 0; i < interfaces.size(); i++) {
            out.append(shortenName(interfaces.get(i)));
            List<String> genericsForInterface = generics.get(i);
            if (genericsForInterface != null && generics.size() > 0) {
                out.append("<");
                for (int j = 0; j < genericsForInterface.size(); j++) {
                    out.append(genericsForInterface.get(j));
                    if (j < genericsForInterface.size() - 1)
                        out.append(", ");
                }
                out.append(">");
            }
            if (i < interfaces.size() - 1)
                out.append(", ");
        }
    }
    
    public void finishTypeDeclaration() throws IOException {
        checkScope(Scope.TYPE_DECLARATION);        
        out.append(" {\n");
        moveToScope(Scope.TYPE_DEFINITION);
    }
    
    private String shortenName(FullyQualifiedName name) {
        String simpleName = name.getSimpleName();
        List<FullyQualifiedName> allNames = knownNames.get(simpleName);
        if (allNames == null || allNames.size() == 0)
            return name.toString();
        if (allNames.get(0).equals(name))
            return simpleName;
        return name.toString();
        
    }
    
    private void checkScope(Scope expectedScope) {
        if (currentScope != expectedScope)
            throw new IllegalStateException("Expected scope " + expectedScope + ", current scope " + currentScope);
    }
    
    private void moveToScope(Scope moveTo) {
        this.currentScope = moveTo;
    }
}
