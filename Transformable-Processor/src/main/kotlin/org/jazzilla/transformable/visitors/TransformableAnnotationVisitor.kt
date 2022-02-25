package org.jazzilla.transformable.visitors

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import org.jazzilla.transformable.annotations.Transformable
import org.jazzilla.transformable.transformer.ClassTransformableTransformer

internal class TransformableAnnotationVisitor(private val logger: KSPLogger,
                                     private val fileBuilder: FileSpec.Builder) : KSVisitorVoid() {

    @OptIn(KotlinPoetKspPreview::class)
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

        /*INTERFACE("interface"),
        CLASS("class"),
        ENUM_CLASS("enum_class"),
        ENUM_ENTRY("enum_entry"),
        OBJECT("object"),
        ANNOTATION_CLASS("annotation_class")*/

        if(classDeclaration.classKind == ClassKind.CLASS) {
            logger.error("Only class supported")
            return
        }

        val transformableAnnotation = classDeclaration.annotations.first()
        val targetArgument = transformableAnnotation.arguments.first { it.name?.asString() == Transformable::target.name }
        val targetClassDeclaration = retrieveTargetClassDeclaration(targetArgument)

        val targetProperties = targetClassDeclaration.getAllProperties()
        val originProperties = classDeclaration.getAllProperties()

        val mappedProperties = targetProperties.

        //makeClassTransformer(classDeclaration)
    }

    @OptIn(KotlinPoetKspPreview::class)
    private fun makeClassTransformer(classDeclaration: KSClassDeclaration) {
        val transformableAnnotation = classDeclaration.annotations.first()
        val targetArgument = transformableAnnotation.arguments.first { it.name?.asString() == Transformable::target.name }
        val targetClassDeclaration = retrieveTargetClassDeclaration(targetArgument)
        val classTransformer = ClassTransformableTransformer(classDeclaration, targetClassDeclaration, logger)

        classTransformer.createTransformer(fileBuilder)
    }

    @OptIn(KotlinPoetKspPreview::class)
    private fun retrieveTargetClassDeclaration(argument: KSValueArgument) : KSClassDeclaration {
        val argumentValue = argument.value
        requireNotNull(argumentValue)

        return ((argumentValue as KSType).declaration as KSClassDeclaration)
    }
}

private fun KSClassDeclaration.isDataClass() =
    modifiers.contains(Modifier.DATA)
