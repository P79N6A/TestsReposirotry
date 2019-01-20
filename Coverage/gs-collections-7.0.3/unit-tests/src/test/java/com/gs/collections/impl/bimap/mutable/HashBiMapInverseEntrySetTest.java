/*
 * Copyright 2013 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.impl.bimap.mutable;

import com.gs.collections.api.bimap.MutableBiMap;

public class HashBiMapInverseEntrySetTest extends AbstractMutableBiMapEntrySetTest
{
    @Override
    protected <K, V> MutableBiMap<K, V> newMap()
    {
        return HashBiMap.<V, K>newMap().inverse();
    }

    @Override
    protected <K, V> MutableBiMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        return HashBiMap.newWithKeysValues(value1, key1, value2, key2).inverse();
    }

    @Override
    protected <K, V> MutableBiMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return HashBiMap.newWithKeysValues(value1, key1, value2, key2, value3, key3).inverse();
    }

    @Override
    protected <K, V> MutableBiMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return HashBiMap.newWithKeysValues(value1, key1, value2, key2, value3, key3, value4, key4).inverse();
    }

    @Override
    protected <K, V> MutableBiMap<K, V> newMapWithKeyValue(K key, V value)
    {
        return HashBiMap.newWithKeysValues(value, key).inverse();
    }
}
