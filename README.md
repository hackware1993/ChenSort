# ChenSort

ChenSort is an improved bucket sort, which is a general-purpose sorting algorithm. All performance data is performed under a single thread, which can easily support multi-threading.

**The time complexity is O(n) at best and O(nlogn) at worst, the space complexity is O(n), and it is stable.**

Randomly generate [1000,10000000] random numbers in the range [-2^63,2^63-1], average speed is 3 times faster than Quicksort, fastest is 20 times(single thread). Traditional counting sorts and bucket sorts cannot handle such a large range of values because the performance is worse than Quicksort.

[Android APK demo, 6.05 MB](https://github.com/hackware1993/ChenSort/blob/master/ChenSort_Android.apk)

[Windows exe demo, 5.8 MB](https://github.com/hackware1993/ChenSort/blob/master/ChenSort_Windows.7z)

The demos are all built on Flutter.

Currently writing an academic paper and expecting to be recognized by the academic community.

Dart code:

```dart
/// The essence of Chen Sort is an improved bucket sort
void chenSort(List<int> list) {
  if (list.length < 2) {
    return;
  }

  int maxValue = list[0];
  int minValue = maxValue;
  for (final element in list.skip(1)) {
    if (element > maxValue) {
      maxValue = element;
    }
    if (element < minValue) {
      minValue = element;
    }
  }

  /// All elements are the same and do not need to be sorted.
  if (maxValue == minValue) {
    return;
  }

  /// Limit the maximum size of the bucket to ensure the performance of long list
  /// sorting, which can be adjusted according to the actual situation.
  ///
  /// The essential difference between this and bucket sorting is that the size of
  /// the bucket is only related to the length of the list, not the range of element values.
  int bucketSize = min(list.length, 50000);
  int maxBucketIndex = bucketSize - 1;

  List<List<int>?> buckets = List.filled(bucketSize, null);
  int slot;

  /// Calculate the bucket in which the element is located based on the value of the element
  /// and the maximum and minimum values.

  /// Overflow detection
  BigInt range = BigInt.from(maxValue) - BigInt.from(minValue);
  if (BigInt.from(range.toInt()) == range) {
    int range = maxValue - minValue;
    double factor = maxBucketIndex / range;
    for (final element in list) {
      // slot = (((element - minValue) / range) * maxBucketIndex).toInt();
      slot = ((element - minValue) * factor).toInt();
      if (buckets[slot] == null) {
        buckets[slot] = [];
      }
      buckets[slot]!.add(element);
    }
  } else {
    /// Overflowed(positive minus negative)
    int positiveRange = maxValue;
    int negativeRange = -1 - minValue;
    int positiveStartBucketIndex = maxBucketIndex ~/ 2 + 1;
    int positiveBucketLength = maxBucketIndex - positiveStartBucketIndex;
    int negativeBucketLength = positiveStartBucketIndex - 1;
    for (final element in list) {
      if (element < 0) {
        slot = (((element - minValue) / negativeRange) * negativeBucketLength)
            .toInt();
      } else {
        slot = positiveStartBucketIndex +
            ((element / positiveRange) * positiveBucketLength).toInt();
      }
      if (buckets[slot] == null) {
        buckets[slot] = [];
      }
      buckets[slot]!.add(element);
    }
  }

  int compare(int left, int right) {
    return left - right;
  }

  int index = 0;
  for (final bucket in buckets) {
    if (bucket != null) {
      if (bucket.length > 1) {
        if (bucket.length >= 1000) {
          chenSort(bucket);
        } else {
          /// The sort method here represents the fastest comparison-type algorithm (Quick sort, Tim sort, etc.)
          bucket.sort(compare);
        }
        for (final element in bucket) {
          list[index++] = element;
        }
      } else {
        list[index++] = bucket[0];
      }
    }
  }
}
```

Java code(Multi-thread. The code just shows that this algorithm can easily support multi-threaded sorting, and the actual performance data is performed under a single thread):

```java
static void chenSort(Integer[] list) {
    int length = list.length;
    if (length < 2) {
        return;
    }

    Integer maxValue = Integer.MIN_VALUE;
    Integer minValue = Integer.MAX_VALUE;
    for (Integer element : list) {
        if (element > maxValue) {
            maxValue = element;
        }
        if (element < minValue) {
            minValue = element;
        }
    }

    /// All elements are the same and do not need to be sorted.
    if (maxValue.equals(minValue)) {
        return;
    }

    /// Limit the maximum size of the bucket to ensure the performance of long list
    /// sorting, which can be adjusted according to the actual situation.
    ///
    /// The essential difference between this and bucket sorting is that the size of
    /// the bucket is only related to the length of the list, not the range of element values.
    int bucketSize = Math.min(length, 50000);
    int maxBucketIndex = bucketSize - 1;

    ArrayList<Integer>[] buckets = new ArrayList[bucketSize];
    int slot;

    /// Calculate the bucket in which the element is located based on the value of the element
    /// and the maximum and minimum values.

    /// Overflow detection
    BigInteger bigRange = BigInteger.valueOf(maxValue).subtract(BigInteger.valueOf(minValue));
    if (BigInteger.valueOf(bigRange.intValue()).equals(bigRange)) {
        double factor = maxBucketIndex * 1.0 / (maxValue - minValue);
        for (Integer element : list) {
            slot = (int) ((element - minValue) * factor);
            if (buckets[slot] == null) {
                buckets[slot] = new ArrayList<>();
            }
            buckets[slot].add(element);
        }
    } else {
        /// Overflowed(positive minus negative)
        double positiveRange = maxValue;
        double negativeRange = -1 - minValue;
        int positiveStartBucketIndex = maxBucketIndex / 2 + 1;
        int positiveBucketLength = maxBucketIndex - positiveStartBucketIndex;
        int negativeBucketLength = positiveStartBucketIndex - 1;
        Integer zero = 0;
        for (Integer element : list) {
            if (element < zero) {
                slot = (int) (((element - minValue) / negativeRange) * negativeBucketLength);
            } else {
                slot = (int) (positiveStartBucketIndex + ((element / positiveRange) * positiveBucketLength));
            }
            if (buckets[slot] == null) {
                buckets[slot] = new ArrayList<>();
            }
            buckets[slot].add(element);
        }
    }

    Comparator<Integer> comparator = Comparator.comparingInt(left -> left);

    // Multi-thread sorting between buckets
    CountDownLatch countDownLatch = new CountDownLatch(buckets.length);
    for (ArrayList<Integer> bucket : buckets) {
        if (bucket != null) {
            if (bucket.size() > 1) {
                executor.execute(() -> {
                    bucket.sort(comparator);
                    countDownLatch.countDown();
                });
            } else {
                countDownLatch.countDown();
            }
        } else {
            countDownLatch.countDown();
        }
    }
    try {
        countDownLatch.await();
    } catch (InterruptedException ignored) {
    }

    int index = 0;
    for (ArrayList<Integer> bucket : buckets) {
        if (bucket != null) {
            if (bucket.size() > 1) {
                for (Integer element : bucket) {
                    list[index++] = element;
                }
            } else {
                list[index++] = bucket.get(0);
            }
        }
    }
}
```

Performance(10 million random numbers sorted, single thread):

```java
Random random = new Random();
Integer[] arr = new Integer[10000000];
long maxValue = Integer.MAX_VALUE;
long minValue = Integer.MIN_VALUE;
long range = maxValue - minValue + 1;
for (int i = 0; i < arr.length; i++) {
    arr[i] = (int) (minValue + random.nextLong(range));
}
Integer[] copy = new Integer[arr.length];
System.arraycopy(arr, 0, copy, 0, arr.length);
long start = System.currentTimeMillis();
chenSort(arr);
long chenSortTimeUsage = System.currentTimeMillis() - start;
start = System.currentTimeMillis();
Arrays.sort(copy);
long quickSortTimeUsage = System.currentTimeMillis() - start;
```

```java
chen sort: 3384 ms, quick sort: 9366 ms, 63.869314541960286%(2.767730496453901x) faster
chen sort: 3450 ms, quick sort: 7223 ms, 52.2359130555171%(2.093623188405797x) faster
chen sort: 1693 ms, quick sort: 5000 ms, 66.14%(2.9533372711163617x) faster
chen sort: 2306 ms, quick sort: 6267 ms, 63.204084889101644%(2.717692974848222x) faster
chen sort: 2922 ms, quick sort: 10145 ms, 71.19763430261213%(3.471937029431896x) faster
chen sort: 3285 ms, quick sort: 9211 ms, 64.33611985669309%(2.803957382039574x) faster
chen sort: 2661 ms, quick sort: 9236 ms, 71.18882633174535%(3.4708756106726795x) faster
chen sort: 2538 ms, quick sort: 6422 ms, 60.47960137028963%(2.530338849487786x) faster
chen sort: 1749 ms, quick sort: 4928 ms, 64.50892857142857%(2.8176100628930816x) faster
chen sort: 1775 ms, quick sort: 5254 ms, 66.21621621621621%(2.96x) faster
chen sort: 1626 ms, quick sort: 5155 ms, 68.45780795344326%(3.1703567035670357x) faster
chen sort: 2375 ms, quick sort: 4877 ms, 51.302029936436334%(2.0534736842105263x) faster
chen sort: 1923 ms, quick sort: 5250 ms, 63.37142857142857%(2.730109204368175x) faster
chen sort: 3028 ms, quick sort: 9237 ms, 67.21879398072967%(3.0505284015852046x) faster
chen sort: 2692 ms, quick sort: 9030 ms, 70.18826135105205%(3.3543833580980684x) faster
```

[Blog](https://mp.weixin.qq.com/s/uGNQxpBohPmlgxsHrE4pFg)

[XiSort](https://github.com/hackware1993/XiSort) The slowest sorting algorithm I've developed with the most efficient code execution in the world.

# Support me

If it helps you a lot, consider sponsoring me a cup of milk tea, or giving a star. Your support is
the driving force for me to continue to maintain.

[Paypal](https://www.paypal.com/paypalme/hackware1993)

![sponsorship.webp](https://github.com/hackware1993/ChenSort/blob/master/sponsorship.webp?raw=true)

Thanks to the following netizens for their sponsorship.

1. 小小鸟 2022.06.08
2. 孟焱 2022.06.08
