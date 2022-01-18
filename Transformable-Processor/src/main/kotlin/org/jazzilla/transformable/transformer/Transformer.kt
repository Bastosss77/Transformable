package org.jazzilla.transformable.transformer

import com.squareup.kotlinpoet.Taggable

interface Transformer<B> where B : Taggable.Builder<*> {
    fun createTransformer(builder: B)
    fun undoTransformer(builder: B)

    fun generate(builder: B) {
        createTransformer(builder)
        undoTransformer(builder)
    }
}