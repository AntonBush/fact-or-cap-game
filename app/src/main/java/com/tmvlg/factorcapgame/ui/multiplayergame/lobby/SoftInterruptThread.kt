package com.tmvlg.factorcapgame.ui.multiplayergame.lobby

open class SoftInterruptThread : Thread() {
    protected var interrupted: Boolean = false

    override fun interrupt() {
        interrupted = true
    }
}