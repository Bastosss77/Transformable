package org.jazzilla.transformable.visitors

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import org.jazzilla.transformable.annotations.Transformable
import org.jazzilla.transformable.transformer.ClassTransformer

class TransformableAnnotationVisitor(private val logger: KSPLogger,
                                     private val fileBuilder: FileSpec.Builder) : KSVisitorVoid() {

    @OptIn(KotlinPoetKspPreview::class)
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

        /*INTERFACE("interface"),
        CLASS("class"),
        ENUM_CLASS("enum_class"),
        ENUM_ENTRY("enum_entry"),
        OBJECT("object"),
        ANNOTATION_CLASS("annotation_class")*/

        when(classDeclaration.classKind) {
            ClassKind.CLASS -> { makeClassTransformer(classDeclaration) }
            else -> { throw IllegalStateException("Supported type: Class")}
        }
    }

    @OptIn(KotlinPoetKspPreview::class)
    private fun makeClassTransformer(classDeclaration: KSClassDeclaration) {
        val transformableAnnotation = classDeclaration.annotations.first()
        val targetArgument = transformableAnnotation.arguments.first { it.name?.asString() == Transformable::target.name }
        val targetClassDeclaration = retrieveTargetClassDeclaration(targetArgument)
        val classTransformer = ClassTransformer(classDeclaration, targetClassDeclaration, logger)

        classTransformer.generate(fileBuilder)
    }

    @OptIn(KotlinPoetKspPreview::class)
    private fun retrieveTargetClassDeclaration(argument: KSValueArgument) : KSClassDeclaration {
        val argumentValue = argument.value

        requireNotNull(argumentValue) {
            logger.error("Parameter target is null")
        }

        return ((argumentValue as KSType).declaration as KSClassDeclaration)
    }
}
