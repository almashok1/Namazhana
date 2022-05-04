package kz.farabicorporation.namazhana.common.extensions

import android.view.View
import androidx.annotation.IdRes
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import kz.farabicorporation.namazhana.common.R

fun View.spring(
        property: DynamicAnimation.ViewProperty
): SpringAnimation {
    val key = getKey(property)
    var springAnim = getTag(key) as? SpringAnimation?
    if (springAnim == null) {
        springAnim = SpringAnimation(this, property)
        setTag(key, springAnim)
    }
    springAnim.spring = (springAnim.spring ?: SpringForce()).apply {
        this.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
    }
    return springAnim
}

fun View.spring(
        property: DynamicAnimation.ViewProperty,
        stiffness: Float = 500f,
        damping: Float = SpringForce.DAMPING_RATIO_NO_BOUNCY,
        startVelocity: Float? = null
): SpringAnimation {
    val key = getKey(property)
    var springAnim = getTag(key) as? SpringAnimation?
    if (springAnim == null) {
        springAnim = SpringAnimation(this, property)
        setTag(key, springAnim)
    }
    springAnim.spring = (springAnim.spring ?: SpringForce()).apply {
        this.dampingRatio = damping
        this.stiffness = stiffness
    }
    startVelocity?.let { springAnim.setStartVelocity(it) }
    return springAnim
}

@IdRes
private fun getKey(property: DynamicAnimation.ViewProperty): Int {
    return when (property) {
        SpringAnimation.TRANSLATION_X -> R.id.translation_x
        SpringAnimation.TRANSLATION_Y -> R.id.translation_y
        SpringAnimation.TRANSLATION_Z -> R.id.translation_z
        SpringAnimation.SCALE_X -> R.id.scale_x
        SpringAnimation.SCALE_Y -> R.id.scale_y
        SpringAnimation.ROTATION -> R.id.rotation
        SpringAnimation.ROTATION_X -> R.id.rotation_x
        SpringAnimation.ROTATION_Y -> R.id.rotation_y
        SpringAnimation.X -> R.id.x
        SpringAnimation.Y -> R.id.y
        SpringAnimation.Z -> R.id.z
        SpringAnimation.ALPHA -> R.id.alpha
        SpringAnimation.SCROLL_X -> R.id.scroll_x
        SpringAnimation.SCROLL_Y -> R.id.scroll_y
        else -> throw IllegalAccessException("Unknown ViewProperty: $property")
    }
}