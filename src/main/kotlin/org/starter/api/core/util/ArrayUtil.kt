package org.starter.api.core.util

import java.util.*

object ArrayUtil {
    /**
     * 주어진 배열의 크기를 늘린다.
     *
     * @param array
     * @param length
     * @return
     * @throws ArrayIndexOutOfBoundsException
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    fun extend(array: Array<String?>, length: Int): Array<String?> {
        if (array.size > length) {
            throw ArrayIndexOutOfBoundsException("원본 길이보다 큰 수를 입력하세요")
        }

        val newArray = arrayOfNulls<String>(length)
        System.arraycopy(array, 0, newArray, 0, array.size)
        return newArray
    }


    /**
     * 주어진 배열을 확장하고 데이터를 넣어준다.
     *
     * @param array
     * @param length
     * @param fillStr
     * @return
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    fun extendAndFill(array: Array<String?>, length: Int, fillStr: String?): Array<String?> {
        println("arrayLength, length ==> " + array.size + ", " + length)
        // #. 3, 5라고 가정했을 때
        // #. 0, 1, 2 채워지고
        // #. 3, 4 더 넣어줘야함.
        if (array.size < length) {
            val newArray = extend(array, length)
            Arrays.fill(newArray, array.size, length - 1, fillStr)
            return newArray
        } else {
            return array
        }
    }

    /**
     * 주어진 배열을 확장하고 데이터를 넣어준다.
     *
     * @param array
     * @param length
     * @param fillStr
     * @return
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    fun extendAndFill(array: IntArray, length: Int, fillStr: Int): IntArray {
        if (array.size > length) {
            throw ArrayIndexOutOfBoundsException("원본 길이보다 큰 수를 입력하세요")
        }

        if (array.size < length) {
            val newArray = IntArray(length)
            System.arraycopy(array, 0, newArray, 0, array.size)
            Arrays.fill(newArray, array.size, length - 1, fillStr)
            return newArray
        } else {
            return array
        }
    }

    /**
     * 주어진 배열을 확장하고 데이터를 넣어준다.
     *
     * @param array
     * @param length
     * @param fillStr
     * @return
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    fun extendAndFill(array: LongArray, length: Int, fillStr: Long): LongArray {
        if (array.size > length) {
            throw ArrayIndexOutOfBoundsException("원본 길이보다 큰 수를 입력하세요")
        }

        if (array.size < length) {
            val newArray = LongArray(length)
            System.arraycopy(array, 0, newArray, 0, array.size)
            Arrays.fill(newArray, array.size, length - 1, fillStr)

            return newArray
        } else {
            return array
        }
    }
}
