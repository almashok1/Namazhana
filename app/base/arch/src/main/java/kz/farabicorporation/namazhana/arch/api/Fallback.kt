package kz.farabicorporation.namazhana.arch.api

/**
 * Calls annotated with [Fallback] should fallback to the selected field for unknown properties
 * This class [does NOT have implementation], only to make code more readable
 * */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class Fallback
