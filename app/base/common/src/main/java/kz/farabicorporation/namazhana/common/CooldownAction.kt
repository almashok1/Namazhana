package kz.farabicorporation.namazhana.common

class CooldownAction(private val refreshIntervalMillis: Long, val action: () -> Unit) {

    var lastEventTimeMillis = 0L

    fun tryAction() {
        if (System.currentTimeMillis() - lastEventTimeMillis > refreshIntervalMillis) {
            lastEventTimeMillis = System.currentTimeMillis()
            action()
        }
    }

}