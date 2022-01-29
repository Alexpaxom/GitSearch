package com.alexpaxom.gitsearch.app.baseelements

// Базовый компонент Store - The Elm Architecture
interface BaseStore<S: BaseState, E: BaseEvent> {
    fun processEvent(event: E)
    fun setState(state: S)
}

interface BaseEvent

interface BaseState
