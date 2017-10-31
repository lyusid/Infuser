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
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

@AutoService(Processor.class)
public class InfuseProcessor extends AbstractProcessor {

    private static final String PACKAGE_NAME = "com.org.lxt.infuse";

    private static final String METHOD_NEWINSTANCE = "build";

    private static final String PARAMA_OBJECT = "object";

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
        Map<TypeElement, ConstructorBinder> binderMap = new LinkedHashMap<>();
        for (Entry<TypeElement, ConstructorBinder.Builder> entry : map.entrySet()) {
            ConstructorBinder.Builder builder = entry.getValue();
            binderMap.put(entry.getKey(), builder.build());
        }
        return binderMap;
    }

    private void parseInfuseElement(Element element, Map<TypeElement, ConstructorBinder.Builder> map, Set<TypeElement> enclosingElements) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        String simpleName = element.getSimpleName().toString();
        if (!map.containsKey(enclosingElement)) {
            ConstructorBinder.Builder builder = ConstructorBinder.builder(enclosingElement, elements);
            builder.addBinderPool(new InfuserBinderPool().build(element, Type.EMTPY));
            map.put(enclosingElement, builder);
        }
        enclosingElements.add(enclosingElement);
    }

    private Map<TypeElement, ConstructorBinder> findAndParseElement(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Infuse.class)) {
            if (!SuperficialValidation.validateElement(element))
                continue;
            PackageElement packageElement = elements.getPackageOf(element);
            Name qualifiedName = packageElement.getQualifiedName();
            printNote("Find a element annotated with Infuse " + qualifiedName);
            boolean isField = element.getKind() == ElementKind.FIELD;
            if (isField)
//                process(element);
//            else
                printError("The type of target should be Filed");
        }
        return null;
    }

    private void process(Element element) {
        VariableElement typeElement = (VariableElement) element;
        String simpleName = typeElement.getSimpleName().toString();
        TypeName typeName = TypeName.get(typeElement.asType());
        Element enclosingElement = typeElement.getEnclosingElement();
        String typeMirrorName = enclosingElement.asType().toString();
        printNote("Enclosing Element Name = " + typeMirrorName);
        if (classFile.contains(typeMirrorName)) {
            TypeSpec.Builder builder = typeSpecMap.get(typeMirrorName);
            Builder factory = MethodSpec.methodBuilder(simpleName)
                    .addModifiers(Modifier.PUBLIC, Modifier.SYNCHRONIZED, Modifier.STATIC)
                    .addParameter(ClassName.get(enclosingElement.asType()), PARAMA_OBJECT)
                    .returns(typeName);
            Set<Modifier> modifiers = typeElement.getModifiers();
            Name objectName = typeElement.getSimpleName();
            if (modifiers.contains(Modifier.PUBLIC)) {
                factory.addStatement("$N.$N = ($T)new $T()", PARAMA_OBJECT, objectName,
                        ClassName.get(typeElement.asType()), typeName)
                        .addStatement("return $N.$N", PARAMA_OBJECT, typeElement.getSimpleName());
            } else {
                factory.addStatement("return null");
                ModifierException.printException(objectName);
            }
            TypeSpec typeSpec = builder.addMethod(factory.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, typeSpec)
                    .build();
            try {
                javaFile.writeTo(System.out);
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        classFile.add(typeMirrorName);
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder("Infuser")
                .addModifiers(Modifier.PUBLIC);
        JavaFile javaFile;
        printNote(typeElement.getSimpleName() + " " + typeName);
        Builder factory = MethodSpec.methodBuilder(simpleName)
                .addModifiers(Modifier.PUBLIC, Modifier.SYNCHRONIZED, Modifier.STATIC)
                .addParameter(ClassName.get(enclosingElement.asType()), "object")
                .returns(typeName);
        typeSpecMap.put(typeMirrorName, typeSpecBuilder);
        Set<Modifier> modifiers = typeElement.getModifiers();
        Name objectName = typeElement.getSimpleName();
        if (modifiers.contains(Modifier.PUBLIC)) {
            factory.addStatement("$N.$N = ($T)new $T()", PARAMA_OBJECT, objectName,
                    ClassName.get(typeElement.asType()), typeName)
                    .addStatement("return $N.$N", PARAMA_OBJECT, typeElement.getSimpleName());
        } else {
//            printError("This filed should be public");
            factory.addStatement("return null");
            ModifierException.printException(objectName);
        }
        TypeSpec typeSpec = typeSpecBuilder.addMethod(factory.build())
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
