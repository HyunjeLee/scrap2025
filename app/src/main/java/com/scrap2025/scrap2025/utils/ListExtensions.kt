package com.scrap2025.scrap2025.utils

/**
 * 리스트의 특정 아이템을 다른 위치로 이동시키고 새로운 리스트를 반환합니다.
 */
fun <T> List<T>.move(fromIndex: Int, toIndex: Int): List<T> {
    if (fromIndex == toIndex) return this

    return this.toMutableList().also { list ->
        val element = list.removeAt(fromIndex)  // 해당 위치의 원소 삭제 후 삭제된 원소 반환
        list.add(toIndex, element)  // 새로운 위치에 원소 삽입
    }
}