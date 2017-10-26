package com.lxt.compiler;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.lxt.annotation.Infuse;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
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
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

@AutoService(Processor.class)
public class JavaProcessor extends AbstractProcessor {

    private static final String PACKAGE_NAME = "com.org.lxt.infuse";

    private static final String METHOD_NEWINSTANCE = "bind";

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
        findAndParseElement(roundEnvironment);
        return false;
    }

    private void findAndParseElement(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Infuse.class)) {
            if (!SuperficialValidation.validateElement(element))
                continue;
            PackageElement packageElement = elements.getPackageOf(element);
            Name qualifiedName = packageElement.getQualifiedName();
            printNote("Find a element annotated with Infuse " + qualifiedName);
            boolean isField = element.getKind() == ElementKind.FIELD;
            if (isField)
                process(element);
            else
                printError("The type of target should be Filed");
        }

    }

    private void process(Element element) {
        VariableElement typeElement = (VariableElement) element;
        TypeSpec.Builder builder = TypeSpec.classBuilder("InfuserFactory")
                .addModifiers(Modifier.PUBLIC);
        JavaFile javaFile;
        Element enclosingElement = typeElement.getEnclosingElement();
        TypeName typeName = TypeName.get(typeElement.asType());
        printNote(typeElement.getSimpleName() + " " + typeName);
        Builder factory = MethodSpec.methodBuilder(METHOD_NEWINSTANCE)
                .addModifiers(Modifier.PUBLIC, Modifier.SYNCHRONIZED, Modifier.STATIC)
                .addParameter(ClassName.get(enclosingElement.asType()), "object")
                .returns(typeName);
        Set<Modifier> modifiers = typeElement.getModifiers();
        if (modifiers.contains(Modifier.PUBLIC)) {
            factory.addStatement("object.$N = ($T)new $T()", typeElement.getSimpleName(), ClassName.get(typeElement.asType()), typeName);
            factory.addStatement("return object.$N", typeElement.getSimpleName());
        }else{
//            printError("This filed should be public");
            factory.addStatement("return null");
        }
        TypeSpec typeSpec = builder.addMethod(factory.build())
                .superclass(TypeName.get(enclosingElement.asType()))
                .build();
        javaFile = JavaFile.builder(PACKAGE_NAME, typeSpec)
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
