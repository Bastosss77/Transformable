package org.jazzilla.transformable.transformer

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.isConstructor
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

class ConstructorTransformer(private val origin: KSClassDeclaration,
                             private val target: KSClassDeclaration,
                             private val logger: KSPLogger? = null) : Transformer<FunSpec.Builder> {

    @OptIn(KotlinPoetKspPreview::class)
    override fun createTransformer(builder: FunSpec.Builder) {
       createConstructCall(origin, target, builder)
    }

    override fun undoTransformer(builder: FunSpec.Builder) {
        createConstructCall(target, origin, builder)
    }

    @OptIn(KotlinPoetKspPreview::class)
    private fun createConstructCall(from: KSClassDeclaration, to: KSClassDeclaration, builder: FunSpec.Builder) {
        val fromPrimaryConstructor = from.primaryConstructor ?: from.getConstructors().firstOrNull()
        val toPrimaryConstructor = to.primaryConstructor ?: to.getConstructors().firstOrNull()

        requirePublicConstruct(fromPrimaryConstructor) { "Constructor must not be null and public in class ${from.toClassName().simpleName}" }
        requirePublicConstruct(toPrimaryConstructor) { "Constructor must not be null and public in class ${to.toClassName().simpleName}" }

        val fromProperties = from.getAllProperties()
        var targetConstructorCallStatement = "return %T("

        toPrimaryConstructor.parameters.forEach { parameter ->
            val name = parameter.name?.getShortName()
            requireNotNull(name)

            if(fromProperties.count { it.simpleName.getShortName() == name } == 0) {
                throw IllegalStateException()
            }

            targetConstructorCallStatement += "$name = this.$name"
        }
        targetConstructorCallStatement += ")"

        builder.addStatement(targetConstructorCallStatement, to.toClassName())
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