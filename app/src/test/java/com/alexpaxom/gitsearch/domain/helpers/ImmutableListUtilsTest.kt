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

    /*
    * Проверяем добавление элементов иммутабельный в список
    * Ожидаем в результирующем списке должны присутствовать элементы из двух списков
    */
    @Test
    fun appendToxList() {
        val targetList = listOf(1,1,3,3)
        val newList = listOf(5,5)
        val resultList = listUtils.append(targetList, newList)

        assertTrue(
            resultList.size == 6 &&
                    resultList.containsAll(listOf(1,1,3,3,5,5))
        )
    }

    /*
    * Проверяем добавление элементов пустой иммутабельный в список
    * Ожидаем в результирующем списке должны присутствовать элементы из списка с элементами
    */
    @Test
    fun appendToEmptyList() {
        val newList = listOf(1,1,3,3,5,5)
        val resultList = listUtils.append(listOf(), newList)

        assertTrue(
            resultList.size == 6 &&
                    resultList.containsAll(newList)
        )
    }

    /*
    * Проверяем добавление пустого в списка
    * Ожидаем в результирующем списке должны присутствовать элементы из списка с элементами
    */
    @Test
    fun appendEmptyList() {
        val targetList = listOf(1,1,3,3,5,5)
        val resultList = listUtils.append(targetList, listOf())

        assertTrue(
            resultList.size == 6 &&
                    resultList.containsAll(targetList)
        )
    }

    /*
    * Проверяем добавление пустого в списка в пустой список
    * Ожидаем результирующий список должен быть пустым
    */
    @Test
    fun appendEmptyToEmptyList() {
        val resultList = listUtils.append(listOf(), listOf())

        assertTrue( resultList.isEmpty() )
    }


}