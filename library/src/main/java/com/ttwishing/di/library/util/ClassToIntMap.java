package com.ttwishing.di.library.util;

/**
 * Created by kurt on 8/11/15.
 */
public class ClassToIntMap {

    private static final int[] sPrimes = {3, 5, 11, 17, 31, 67, 131, 257, 509, 1021};
    private final int h;

    private final Bucket[] table;
    private final Class[] klasses;

    private final int hashMask;
    private final int size;

    public ClassToIntMap(int capacity) {
        int[] values = resize(capacity);
        this.size = values[0];
        this.h = values[1];
        this.hashMask = this.size - 1;
        this.klasses = new Class[capacity];
        this.table = new Bucket[this.size];
    }

    public int get(Class klass) {
        int hash = hash(indexFor(klass));
        Bucket bucket = this.table[hash];
        while (bucket != null) {
            if (bucket.klass == klass) {
                return bucket.id;
            }
            bucket = bucket.next;
        }
        return -1;
    }

    public void put(Class klass, int id) {
        int hash = hash(indexFor(klass));
        //hash指向新的Bucket,原Bucket作为新的Bucket的next结点
        this.table[hash] = new Bucket(klass, id, this.table[hash]);
        this.klasses[id] = klass;
    }

    private int indexFor(Class klass) {
        return this.h * klass.hashCode();
    }

    private int hash(int h) {
        return h & this.hashMask;
    }

    private final int[] resize(int capacity) {
        int max;
        if (capacity < 32) {
            max = capacity + capacity;
        } else {
            max = capacity + capacity >> 2;
        }

        int index = 0;
        int size = 2;
        while (size < max) {
            if (index < sPrimes.length + 1) {
                index += 1;
            }
            size += size;
        }
        int[] array = new int[]{size, sPrimes[index]};
        return array;
    }

    final class Bucket {
        private final Class klass;
        private final Bucket next;
        private final int id;

        Bucket(Class klass, int id, Bucket parent) {
            this.klass = klass;
            this.id = id;
            this.next = parent;
        }
    }
}
