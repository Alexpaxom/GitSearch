package com.alexpaxom.gitsearch.domain.helpers

import com.alexpaxom.gitsearch.domain.interactors.search.ImmutableListUtils
import com.alexpaxom.homework_2.data.models.ListItem
import org.junit.Assert.*

import junit.framework.TestCase

import org.junit.Before
import org.junit.Test

class ImmutableListUtilsTest{

    lateinit var listUtils : ImmutableListUtils<Int>

    @Before
    fun setUp() {
        listUtils = ImmutableListUtils()
    }

    @Test
    fun appendToEmptyList() {
        val targetList = listOf(1,1,3,3)
        val newList = listOf(5,5)
        val resultList = listUtils.append(targetList, newList)

        assertTrue(
            resultList.size == 6 &&
                    resultList.containsAll(listOf(1,1,3,3,5,5))
        )
    }

    @Test
    fun appendToList() {
        val newList = listOf(1,1,3,3,5,5)
        val resultList = listUtils.append(listOf(), newList)

        assertTrue(
            resultList.size == 6 &&
                    resultList.containsAll(newList)
        )
    }

    @Test
    fun appendEmptyList() {
        val targetList = listOf(1,1,3,3,5,5)
        val resultList = listUtils.append(targetList, listOf())

        assertTrue(
            resultList.size == 6 &&
                    resultList.containsAll(targetList)
        )
    }

    @Test
    fun appendEmptyToEmptyList() {
        val resultList = listUtils.append(listOf(), listOf())

        assertTrue( resultList.isEmpty() )
    }


}