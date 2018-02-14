package org.mapdb.tree

import org.mapdb.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mapdb.serializer.Serializers

class BTreeMapParTest {


    internal var scale = TT.testScale()
    internal val threadNum = 6 * scale
    internal val max = 1e6.toInt() * scale

    @Test
    @Throws(InterruptedException::class)
    fun parInsert() {
        if (scale == 0)
            return


        val m = DBMaker.memoryDB().make().treeMap("test").valueSerializer(Serializers.LONG).keySerializer(Serializers.LONG).createOrOpen()

        TT.fork(threadNum, {core->
                var n: Long = core.toLong()
                while (n < max) {
                    m.put(n, n)
                    n += threadNum.toLong()
                }
       })


        assertEquals(max.toLong(), m.size.toLong())
    }
}
