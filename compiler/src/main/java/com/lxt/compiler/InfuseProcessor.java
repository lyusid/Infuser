package com.lxt.compiler;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.lxt.annotation.Infuse;
import com.lxt.annotation.InfuseInt;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

@AutoService(Processor.class)
public class InfuseProcessor extends AbstractProcessor {

    private Elements elements;

    private Filer filer;

    private Locale locale;

    private Messager messager;

    private SourceVersion sourceVersion;

    private Types types;

    private Set<String> classFile;

    private Map<String, TypeSpec.Builder> typeSpecMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elements = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        locale = processingEnvironment.getLocale();
        messager = processingEnvironment.getMessager();
        sourceVersion = processingEnvironment.getSourceVersion();
        types = processingEnvironment.getTypeUtils();
        classFile = new LinkedHashSet<>();
        typeSpecMap = new HashMap<>();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Infuse.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        printNote("Begin to run process");
        Map<TypeElement, ConstructorBinder> elementAndBinder = findElement(roundEnvironment);
        for (Entry<TypeElement, ConstructorBinder> constructorBinderEntry : elementAndBinder.entrySet()) {
            TypeElement typeElement = constructorBinderEntry.getKey();
            ConstructorBinder constructorBinder = constructorBinderEntry.getValue();
            JavaFile javaFile = constructorBinder.createJavaFile();
            try {
                javaFile.writeTo(filer);
                javaFile.writeTo(System.out);
            } catch (IOException e) {
                e.printStackTrace();
                printError(String.format("Failed to write constructor binder %s %s ", typeElement, e.getMessage()));
            }
        }
        return false;
    }

    private Map<TypeElement, ConstructorBinder> findElement(RoundEnvironment roundEnvironment) {
        Map<TypeElement, ConstructorBinder.Builder> map = new LinkedHashMap<>();
        Set<TypeElement> enclosingElements = new LinkedHashSet<>();
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Infuse.class)) {
            if (!SuperficialValidation.validateElement(element))
                continue;
            if (element.getKind() == ElementKind.FIELD) {
                parseInfuseElement(element, map, enclosingElements);
            }
        }
        for (Element element : roundEnvironment.getElementsAnnotatedWith(InfuseInt.class)) {
            if (!SuperficialValidation.validateElement(element))
                continue;
            parseInfuseIntElement(element, map, enclosingElements);
        }

        Map<TypeElement, ConstructorBinder> binderMap = new LinkedHashMap<>();
        for (Entry<TypeElement, ConstructorBinder.Builder> entry : map.entrySet()) {
            ConstructorBinder.Builder builder = entry.getValue();
            binderMap.put(entry.getKey(), builder.build());
        }
        return binderMap;
    }

    private void parseInfuseIntElement(Element element, Map<TypeElement, ConstructorBinder.Builder> map, Set<TypeElement> enclosingElements) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        ConstructorBinder.Builder builder = ConstructorBinder.builder(enclosingElement);
        int[] value = element.getAnnotation(InfuseInt.class).value();
        builder.addBinderPool(new InfuserIntBinderPool(Type.INT, value).build(element, Type.INT));
        map.put(enclosingElement, builder);
        enclosingElements.add(enclosingElement);
        if (!element.getModifiers().contains(Modifier.PUBLIC))
            ModifierException.printException(element.getSimpleName().toString(), enclosingElement.getQualifiedName().toString());

    }

    private void parseInfuseElement(Element element, Map<TypeElement, ConstructorBinder.Builder> map, Set<TypeElement> enclosingElements) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        ConstructorBinder.Builder builder = ConstructorBinder.builder(enclosingElement);
        builder.addBinderPool(new InfuserBinderPool().build(element, Type.EMTPY));
        map.put(enclosingElement, builder);
        enclosingElements.add(enclosingElement);
        if (!element.getModifiers().contains(Modifier.PUBLIC))
            ModifierException.printException(element.getSimpleName().toString(), enclosingElement.getQualifiedName().toString());
    }

    private void println(String message) {
        System.out.println(message);
    }

    private void printNote(String message) {
        messager.printMessage(Kind.NOTE, message);
    }

    private void printError(String message) {
        messager.printMessage(Kind.ERROR, message);
    }
}
