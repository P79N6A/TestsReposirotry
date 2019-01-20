/*
 * Copyright 2015 Goldman Sachs.
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

package com.gs.collections.api.partition.bag;

import com.gs.collections.api.bag.MutableBag;

/**
 * A PartitionMutableBag is the result of splitting a mutable bag into two mutable bags based on a Predicate.
 * The results that answer true for the Predicate will be returned from the getSelected() method and the results that answer
 * false for the predicate will be returned from the getRejected() method.
 */
public interface PartitionMutableBag<T> extends PartitionMutableBagIterable<T>, PartitionUnsortedBag<T>
{
    MutableBag<T> getSelected();

    MutableBag<T> getRejected();

    PartitionImmutableBag<T> toImmutable();
}
