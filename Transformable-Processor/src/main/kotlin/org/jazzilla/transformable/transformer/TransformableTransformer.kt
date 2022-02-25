package org.jazzilla.transformable.transformer

import com.squareup.kotlinpoet.Taggable

internal interface TransformableTransformer<B> where B : Taggable.Builder<*> {
    fun createTransformer(builder: B)
}