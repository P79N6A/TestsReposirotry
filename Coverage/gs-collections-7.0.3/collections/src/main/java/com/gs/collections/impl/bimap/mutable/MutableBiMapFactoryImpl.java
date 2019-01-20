/*
 * Copyright 2014 Goldman Sachs.
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
import com.gs.collections.api.factory.bimap.MutableBiMapFactory;

public class MutableBiMapFactoryImpl implements MutableBiMapFactory
{
    public <K, V> MutableBiMap<K, V> empty()
    {
        return HashBiMap.newMap();
    }

    public <K, V> MutableBiMap<K, V> of()
    {
        return this.empty();
    }

    public <K, V> MutableBiMap<K, V> with()
    {
        return this.empty();
    }

    public <K, V> MutableBiMap<K, V> of(K key, V value)
    {
        return this.with(key, value);
    }

    public <K, V> MutableBiMap<K, V> with(K key, V value)
    {
        return HashBiMap.newWithKeysValues(key, value);
    }

    public <K, V> MutableBiMap<K, V> of(K key1, V value1, K key2, V value2)
    {
        return this.with(key1, value1, key2, value2);
    }

    public <K, V> MutableBiMap<K, V> with(K key1, V value1, K key2, V value2)
    {
        return HashBiMap.newWithKeysValues(key1, value1, key2, value2);
    }

    public <K, V> MutableBiMap<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return this.with(key1, value1, key2, value2, key3, value3);
    }

    public <K, V> MutableBiMap<K, V> with(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return HashBiMap.newWithKeysValues(key1, value1, key2, value2, key3, value3);
    }

    public <K, V> MutableBiMap<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return this.with(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    public <K, V> MutableBiMap<K, V> with(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return HashBiMap.newWithKeysValues(key1, value1, key2, value2, key3, value3, key4, value4);
    }
}
