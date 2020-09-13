package com.kania.randomgallery

import java.util.*
import kotlin.collections.ArrayList

object RandomUtil {
    fun <T> getRandomList (originList: ArrayList<T>): ArrayList<T> {
        val randomList = ArrayList<T>()
        val size = originList.size
        val selected = Array<Boolean>(size) { false }
        val random = Random()
        var count = 0
        while (count < size) {
            var target = random.nextInt(size)
            while (selected[target]) {
                target = (target + 1) % size
            }
            randomList.add(originList[target])
            selected[target] = true;
            count++
        }
        return randomList
    }
}
