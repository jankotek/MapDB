package org.mapdb.tree

import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mapdb.*
import org.mapdb.hasher.Hasher
import org.mapdb.hasher.Hashers
import org.mapdb.serializer.GroupSerializer
import org.mapdb.serializer.Serializers
import org.mapdb.store.*
import org.mapdb.tree.guavaTests.ConcurrentMapInterfaceTest
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentMap


@RunWith(Parameterized::class)
class BTreeMap_ConcurrentMap_GuavaTest(
        val mapMaker:(generic:Boolean?)-> ConcurrentMap<Any?, Any?>
    ):ConcurrentMapInterfaceTest<Int, String>(
            false,  // boolean allowsNullKeys,
            false,  // boolean allowsNullValues,
            true,   // boolean supportsPut,
            true,   // boolean supportsRemove,
            true,   // boolean supportsClear,
            true    // boolean supportsIteratorRemove
    ){

    companion object {


        @Parameterized.Parameters
        @Throws(IOException::class)
        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun params(): Iterable<Any> {
            val ret = ArrayList<Any>()

            val bools = if(TT.shortTest()) TT.boolsFalse else TT.bools

            for(valueInline in bools)
            for(otherComparator in bools)
            for(small in bools)
            for(storeType in 0..2)
            for(isThreadSafe in bools)
            for(counter in bools)
            {
                ret.add(arrayOf<Any>({generic:Boolean?->
                    val store = when(storeType){
                        0-> StoreOnHeap()
                        1-> StoreTrivial()
                        2-> StoreDirect.make()
                        else -> throw AssertionError()
                    }

                    val nodeSize = if(small) 4 else 32
                    val counterRecid = if(counter) store.put(0L, Serializers.LONG_PACKED) else 0L
                    var keySer:GroupSerializer<Int> = if(generic==null) Serializers.INTEGER else {
                            if(generic) Serializers.wrapGroupSerializer(Serializers.ELSA) as GroupSerializer<Int> else Serializers.INTEGER
                        }

                    if(otherComparator && generic!=null && generic.not())
                        keySer = object: GroupSerializer<Int> by keySer{

                            override fun defaultHasher(): Hasher<Int> {
                                return Hashers.FAIL as Hasher<Int>
                            }
                        }

                    val valSer = if(generic==null) Serializers.INTEGER else{
                            if(generic) Serializers.wrapGroupSerializer(Serializers.ELSA) else Serializers.STRING
                        }
                    BTreeMap.make(keySerializer = keySer, valueSerializer = valSer,
                            comparator = if(otherComparator) Serializers.ELSA as Comparator<Int> else keySer.defaultHasher(),
                            store = store, maxNodeSize =  nodeSize, isThreadSafe = isThreadSafe,
                            counterRecid = counterRecid, valueInline = valueInline)
                }))

            }

            return ret
        }

    }

    override fun getKeyNotInPopulatedMap(): Int = -10

    override fun getValueNotInPopulatedMap(): String = "-120"
    override fun getSecondValueNotInPopulatedMap(): String = "-121"

    override fun makeEmptyMap(): ConcurrentMap<Int?, String?> {
        @Suppress("UNCHECKED_CAST")
        return mapMaker(false) as ConcurrentMap<Int?, String?>
    }

    override fun makePopulatedMap(): ConcurrentMap<Int?, String?>? {
        val ret = makeEmptyMap()
        for(i in 0 until 30) {
            ret.put(i,  "aa"+i)
        }
        return ret;
    }

    override fun supportsValuesHashCode(map: MutableMap<Int, String>?): Boolean {
        // keySerializer returns wrong hash on purpose for this test, so pass it
        return false;
    }

    @Test fun randomInsert(){
        //tests random inserts
        val map = makeEmptyMap() as BTreeMap
        val max = if(TT.shortTest()) 100 else 10000
        val maxKey = 1e8.toInt()
        val r = Random(1)
        val ref = IntHashSet()
        for(i in 0..max){
            val key = r.nextInt(maxKey)
            ref.add(key)
            map.put(key, "")
            map.verify()
        }

        assertEquals(ref.size(), map.size)
        for(i in ref.toArray()){
            assertTrue(map.containsKey(i))
        }
    }

    @Test fun test_empty_iterator() {
        val m = makeEmptyMap()
        assertFalse(m.keys.iterator().hasNext())
        assertFalse(m.values.iterator().hasNext())
    }



}
