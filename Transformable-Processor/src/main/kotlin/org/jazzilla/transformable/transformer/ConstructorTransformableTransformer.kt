package org.jazzilla.transformable.transformer

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/*
    Test(val a: String)
    TestTarget(val a: String)

Returns :

    TestTarget(a = this.a)
    Test(a = this.a)
 */

internal class ConstructorTransformableTransformer(private val origin: KSClassDeclaration,
                                          private val target: KSClassDeclaration,
                                          private val logger: KSPLogger? = null) : TransformableTransformer<FunSpec.Builder> {

    @OptIn(KotlinPoetKspPreview::class)
    override fun createTransformer(builder: FunSpec.Builder) {
        val originPrimaryConstructor = origin.primaryConstructor ?: origin.getConstructors().firstOrNull()
        val targetPrimaryConstructor = target.primaryConstructor ?: target.getConstructors().firstOrNull()

        requirePublicConstruct(originPrimaryConstructor) { "Constructor must not be null and public in class ${origin.toClassName().simpleName}" }
        requirePublicConstruct(targetPrimaryConstructor) { "Constructor must not be null and public in class ${target.toClassName().simpleName}" }

        val originProperties = origin.getAllProperties()
        var targetConstructorCallStatement = "return %T("

        targetPrimaryConstructor.parameters.forEach { parameter ->
            val name = parameter.name?.getShortName()
            requireNotNull(name)

            if (originProperties.count { it.simpleName.getShortName() == name } == 0) {
                throw IllegalStateException()
            }

            targetConstructorCallStatement += "$name = this.$name"
        }

        targetConstructorCallStatement += ")"

        builder.addStatement(targetConstructorCallStatement, target.toClassName())
    }

    @OptIn(ExperimentalContracts::class)
    private fun requirePublicConstruct(constructor: KSFunctionDeclaration?, lazyMessage: () -> String) {
        //Wanted to use contract but can't access to modifiers
        //Error in contract description: Error in contract description
        contract {
            returns() implies (constructor != null)
        }

        if(constructor == null) {
            throw IllegalStateException(lazyMessage())
        }

        val visibilityModifier = constructor.modifiers.firstOrNull { it == Modifier.PRIVATE || it == Modifier.INTERNAL || it == Modifier.PROTECTED }

        if(visibilityModifier != null) {
            throw IllegalStateException(lazyMessage())
        }
    }
}