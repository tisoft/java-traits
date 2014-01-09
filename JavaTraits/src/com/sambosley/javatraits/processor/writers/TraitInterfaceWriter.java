/**
 * Copyright 2014 Sam Bosley
 * 
 * See the file "LICENSE" for the full license governing this code.
 */
package com.sambosley.javatraits.processor.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.sambosley.javatraits.processor.data.TraitElement;
import com.sambosley.javatraits.utils.Utils;

public class TraitInterfaceWriter {

    private final TraitElement element;
    private Messager messager;

    public TraitInterfaceWriter(TraitElement element, Messager messager) {
        this.element = element;
        this.messager = messager;
    }

    public void writeInterface(Filer filer) {
        try {
            JavaFileObject jfo = filer.createSourceFile(element.getFullyQualifiedInterfaceName(), element.getSourceElement());
            Writer writer = jfo.openWriter();
            writer.write(emitInterface());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            messager.printMessage(Kind.ERROR, "IOException writing interface for trait", element.getSourceElement());
        }
    }

    private String emitInterface() {
        StringBuilder builder = new StringBuilder();
        emitPackage(builder);
        emitImports(builder);
        emitInterfaceDeclaration(builder);
        return builder.toString();
    }

    private void emitPackage(StringBuilder builder) {
        builder.append("package ").append(element.getPackageName()).append(";\n\n");
    }

    private void emitImports(StringBuilder builder) {
        Set<String> imports = new HashSet<String>();
        Utils.accumulateImportsFromExecutableElements(imports, element.getDeclaredMethods(), messager);
        for (String s : imports) {
            builder.append("import ").append(s).append(";\n");
        }
        builder.append("\n");
    }

    private void emitInterfaceDeclaration(StringBuilder builder) {
        builder.append("public interface ");
        element.emitParametrizedInterfaceName(builder, true);
        builder.append(" {\n");
        emitMethodDeclarations(builder);
        builder.append("}");
    }

    private void emitMethodDeclarations(StringBuilder builder) {
        for (ExecutableElement exec : element.getDeclaredMethods()) {
            emitMethodDeclarationForExecutableElement(builder, exec);
        }
    }

    private void emitMethodDeclarationForExecutableElement(StringBuilder builder, ExecutableElement exec) {
        Utils.emitMethodSignature(builder, exec, element.getSimpleName(), false);
        builder.append(";\n");
    }

}
