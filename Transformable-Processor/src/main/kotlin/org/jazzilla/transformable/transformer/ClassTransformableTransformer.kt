package org.jazzilla.transformable.transformer

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
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

internal class ClassTransformableTransformer(private val origin: KSClassDeclaration,
                                    private val target: KSClassDeclaration,
                                    logger: KSPLogger? = null) : TransformableTransformer<FileSpec.Builder> {
    private val transformFunExtensionName = "transform"

    private val constructorTransformer = ConstructorTransformableTransformer(origin, target, logger)

    @OptIn(KotlinPoetKspPreview::class)
    override fun createTransformer(builder: FileSpec.Builder) {
        val transformExtensionFunSpecBuilder = FunSpec.builder(transformFunExtensionName)
            .receiver(origin.toClassName())
            .returns(target.toClassName())

        constructorTransformer.createTransformer(transformExtensionFunSpecBuilder)
        builder.addFunction(transformExtensionFunSpecBuilder.build())
    }
}
