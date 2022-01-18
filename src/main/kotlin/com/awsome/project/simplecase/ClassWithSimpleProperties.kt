package com.awsome.project.simplecase

import org.jazzilla.transformable.annotations.Transformable

@Transformable(target = ClassWithSimplePropertiesTarget::class)
class ClassWithSimpleProperties constructor(val aProperty: String)

class ClassWithSimplePropertiesTarget(val aProperty: String)
