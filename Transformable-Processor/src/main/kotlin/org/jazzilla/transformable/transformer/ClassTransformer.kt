package org.jazzilla.transformable.transformer

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName

/*

    class Test(val a: String)
    class TestTarget(val a: String)


Returns :

    fun Test.transform() : TestTarget {
        return TestTarget(a = this.a)
    }

    fun TestTarget.undo() : Test {
        return Test(a = this.a)
    }
 */

class ClassTransformer(private val origin: KSClassDeclaration,
                       private val target: KSClassDeclaration,
                       private val logger: KSPLogger? = null) : Transformer<FileSpec.Builder> {
    private val transformFunExtensionName = "transform"
    private val undoFunExtensionName = "undo"

    private val constructorTransformer = ConstructorTransformer(origin, target, logger)

    @OptIn(KotlinPoetKspPreview::class)
    override fun createTransformer(builder: FileSpec.Builder) {
        val transformExtensionFunSpecBuilder = FunSpec.builder(transformFunExtensionName)
            .receiver(origin.toClassName())
            .returns(target.toClassName())

        constructorTransformer.createTransformer(transformExtensionFunSpecBuilder)
        builder.addFunction(transformExtensionFunSpecBuilder.build())
    }

    @OptIn(KotlinPoetKspPreview::class)
    override fun undoTransformer(builder: FileSpec.Builder) {
        val undoExtensionFunSpecBuilder = FunSpec.builder(undoFunExtensionName)
            .receiver(target.toClassName())
            .returns(origin.toClassName())

        constructorTransformer.undoTransformer(undoExtensionFunSpecBuilder)
        builder.addFunction(undoExtensionFunSpecBuilder.build())
    }
}
