package com.aapnainfotech.expensemanagementsystem.util

import java.util.concurrent.atomic.AtomicInteger

class RandomUtil {


    companion object {

        private val seed = AtomicInteger()
        fun getRandomInt() = seed.getAndIncrement() + System.currentTimeMillis().toInt()

    }


}