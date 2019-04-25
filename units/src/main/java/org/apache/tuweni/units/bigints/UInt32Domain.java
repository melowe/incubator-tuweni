/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuweni.units.bigints;

import com.google.common.collect.DiscreteDomain;

/**
 * A {@link DiscreteDomain} over {@link UInt32}.
 */
public final class UInt32Domain extends DiscreteDomain<UInt32> {

  @Override
  public UInt32 next(UInt32 value) {
    return value.add(1);
  }

  @Override
  public UInt32 previous(UInt32 value) {
    return value.subtract(1);
  }

  @Override
  public long distance(UInt32 start, UInt32 end) {
    boolean negativeDistance = start.compareTo(end) < 0;
    UInt32 distance = negativeDistance ? end.subtract(start) : start.subtract(end);
    if (!distance.fitsLong()) {
      return negativeDistance ? Long.MIN_VALUE : Long.MAX_VALUE;
    }
    long distanceLong = distance.toLong();
    return negativeDistance ? -distanceLong : distanceLong;
  }

  @Override
  public UInt32 minValue() {
    return UInt32.MIN_VALUE;
  }

  @Override
  public UInt32 maxValue() {
    return UInt32.MAX_VALUE;
  }
}
