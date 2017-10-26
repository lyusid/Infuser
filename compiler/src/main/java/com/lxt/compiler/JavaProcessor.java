package com.lxt.compiler;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.lxt.annotation.Test;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Locale;
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
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

@AutoService(Processor.class)
public class JavaProcessor extends AbstractProcessor {

    private static final String PACKAGE_NAME = "com.org.lxt.infuse";

    private static final String METHOD_NEWINSTANCE = "newInstance";

    private Elements elements;

    private Filer filer;

    private Locale locale;

    private Messager messager;

    private SourceVersion sourceVersion;

    private Types types;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elements = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        locale = processingEnvironment.getLocale();
        messager = processingEnvironment.getMessager();
        sourceVersion = processingEnvironment.getSourceVersion();
        types = processingEnvironment.getTypeUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Test.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        printNote("Begin to run process");
        findAndParseElement(roundEnvironment);
        return false;
    }

    private void findAndParseElement(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Test.class)) {
            if (!SuperficialValidation.validateElement(element))
                continue;
            printNote("Find a element named " + element.getClass().getSimpleName());
            boolean isField = element.getKind() == ElementKind.FIELD;
//            if (isField)
                process(element);
//            else
//                printError("The type of target should be Filed");
        }

    }

    private void process(Element element) {
        TypeElement typeElement = (TypeElement) element;
        TypeSpec.Builder builder = TypeSpec.classBuilder(typeElement.getSimpleName() + "Factory")
                .addModifiers(Modifier.PUBLIC);
        MethodSpec factory = MethodSpec.methodBuilder(METHOD_NEWINSTANCE)
                .addModifiers(Modifier.PUBLIC, Modifier.SYNCHRONIZED)
                .returns(TypeName.BOOLEAN)
                .addStatement("return true")
                .build();
        TypeSpec typeSpec = builder.addMethod(factory)
                .build();
        JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, typeSpec)
                .build();
        try {
            javaFile.writeTo(System.out);
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printNote(String message) {
        messager.printMessage(Kind.NOTE, message);
    }

    private void printError(String message) {
        messager.printMessage(Kind.ERROR, message);
    }
}
