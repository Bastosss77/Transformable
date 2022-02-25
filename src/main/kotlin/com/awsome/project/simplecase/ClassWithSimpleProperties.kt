package com.awsome.project.simplecase

import org.jazzilla.transformable.annotations.Transformable

/**
 * Requirements :
 * - Target propertie must have the same type as origin propertie
 * - If propertie is used in target constructor, origin must provide the same propertie
 */

@Transformable(target = ClassWithSimplePropertiesTarget::class)
class ClassWithSimpleProperties constructor(val aProperty: String)

class ClassWithSimplePropertiesTarget(val aProperty: String)
