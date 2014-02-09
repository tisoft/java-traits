/**
 * Copyright 2014 Yahoo Inc.
 *
 * See the file "LICENSE" for the full license governing this code.
 */
package com.yahoo.javatraits.processor.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import com.yahoo.annotations.ClassName;
import com.yahoo.annotations.GenericName;
import com.yahoo.annotations.TypeName;
import com.yahoo.annotations.Utils;
import com.yahoo.javatraits.annotations.HasTraits;

public class ClassWithTraits extends TypeElementWrapper {

    public static final String GEN_SUFFIX = "Gen";
    public static final String DELEGATE_SUFFIX = "Delegate";

    private List<TraitElement> traitClasses;
    private ClassName desiredSuperclass;
    private ClassName generatedSuperclass;
    private Map<String, ClassName> prefer;

    public ClassWithTraits(TypeElement elem, Messager messager, Map<ClassName, TraitElement> traitMap) {
        super(elem, messager);
        initTraitClasses(traitMap);
        initSuperclasses();
        initPreferValues();
    }

    private void initTraitClasses(final Map<ClassName, TraitElement> traitMap) {
        List<ClassName> traitNames = Utils.getClassValuesFromAnnotation(HasTraits.class, elem, "traits", messager);
        traitClasses = Utils.map(traitNames, new Utils.MapFunction<ClassName, TraitElement>() {
            @Override
            public TraitElement map(ClassName arg) {
                TraitElement correspondingTrait = traitMap.get(arg);
                if (correspondingTrait == null)
                    messager.printMessage(Kind.ERROR, "Couldn't find TraitElement for name " + arg.toString());
                return correspondingTrait;
            }
        });
    }

    private void initSuperclasses() {
        List<ClassName> desiredSuperclassResult = Utils.getClassValuesFromAnnotation(HasTraits.class, elem, "desiredSuperclass", messager);
        desiredSuperclass = desiredSuperclassResult.size() > 0 ? desiredSuperclassResult.get(0) : new ClassName("java.lang.Object");
        generatedSuperclass = new ClassName(fqn.toString() + GEN_SUFFIX);
    }

    private void initPreferValues() {
        prefer = new HashMap<String, ClassName>();
        AnnotationMirror hasTraits = Utils.findAnnotationMirror(elem, HasTraits.class);
        AnnotationValue preferValue = Utils.findAnnotationValue(hasTraits, "prefer");
        if (preferValue != null && preferValue.getValue() instanceof List) {
            @SuppressWarnings("unchecked")
            List<? extends AnnotationValue> preferList = (List<? extends AnnotationValue>) preferValue.getValue();
            for (AnnotationValue entry : preferList) {
                Object value = entry.getValue();
                if (value instanceof AnnotationMirror) {
                    AnnotationMirror preferMirror = (AnnotationMirror) value;
                    AnnotationValue targetValue = Utils.findAnnotationValue(preferMirror, "target");
                    AnnotationValue methodValue = Utils.findAnnotationValue(preferMirror, "method");

                    ClassName targetName = Utils.getClassValuesFromAnnotationValue(targetValue).get(0);
                    String method = (String) methodValue.getValue();
                    prefer.put(method, targetName);
                }
            }
        }
    }

    public ClassName getFullyQualifiedGeneratedSuperclassName() {
        return generatedSuperclass;
    }

    public List<TraitElement> getTraitClasses() {
        return traitClasses;
    }

    public ClassName getDesiredSuperclass() {
        return desiredSuperclass;
    }

    public Map<String, ClassName> getPreferMap() {
        return prefer;
    }

    public ClassName getDelegateClassNameForTraitElement(TraitElement traitElement) {
        ClassName delegate = new ClassName(traitElement.getFullyQualifiedName() + "__" + getSimpleName() + DELEGATE_SUFFIX);
        delegate.setTypeArgs(traitElement.getTypeParameters());
        return delegate;
    }

    public List<TypeName> getTypeParametersForDelegate(TraitElement onlyForThisElement) {
        List<TypeName> result = new ArrayList<TypeName>();
        for (int i = 0; i < traitClasses.size(); i++) {
            TraitElement elem = traitClasses.get(i);
            if (elem.hasTypeParameters()) {
                if (onlyForThisElement != null && !onlyForThisElement.getFullyQualifiedName().equals(elem.getFullyQualifiedName())) {
                    int paramCount = elem.getTypeParameters().size();
                    for (int p = 0; p < paramCount; p++) {
                        result.add(new GenericName("?", null, null));
                    }
                } else {
                    result.addAll(elem.getTypeParameters());
                }
            }
        }
        return result;
    }
}
