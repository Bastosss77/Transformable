package org.jazzilla.transformable

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.writeTo
import org.jazzilla.transformable.annotations.Transformable
import org.jazzilla.transformable.visitors.TransformableAnnotationVisitor

internal class TransformableProcessor(private val generator: CodeGenerator, private val logger: KSPLogger) : SymbolProcessor {
    private val transformableQualifiedName = Transformable::class.qualifiedName
    private val transformerPackage = "org.jazzilla.transformable"
    private val transformerFileNameSuffix = "Transformer"

    @OptIn(KotlinPoetKspPreview::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val qualifiedName = transformableQualifiedName
            ?: throw IllegalStateException("Qualified name for annotation class is null")

        logger.info("Start resolving classes with ${Transformable::class.simpleName} annotation...")
        val classes = resolver.getSymbolsWithAnnotation(qualifiedName).filterIsInstance<KSClassDeclaration>()
        logger.info("Found ${classes.count()} files with ${Transformable::class.simpleName} annotation")

        classes.filter { it.validate() }.forEach { classDeclaration ->
            val className = classDeclaration.simpleName.asString()
            val fileSpecBuilder = FileSpec.builder(transformerPackage, "$className$transformerFileNameSuffix")
            val visitor = TransformableAnnotationVisitor(logger, fileSpecBuilder)

            classDeclaration.accept(visitor, Unit)
            fileSpecBuilder.build().writeTo(generator, Dependencies(false))
        }

        return classes.filterNot { it.validate() }.toList()
    }
}

internal class TransformableProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return TransformableProcessor(environment.codeGenerator, environment.logger)
    }
}
