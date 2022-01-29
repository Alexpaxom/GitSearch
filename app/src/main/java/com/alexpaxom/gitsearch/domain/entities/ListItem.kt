package com.alexpaxom.homework_2.data.models

interface ListItem {
    val typeId: Int
        get() = error("View type is required $this")
}