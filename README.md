Chen sort is an improved bucket sort, which is a general-purpose sorting algorithm.

**The time complexity is O(n) at best and O(nlogn) at worst, the space complexity is O(n), and it is stable.**

Randomly generate [1000,10000000] random numbers in the range [-2^63,2^63-1], average speed is 3 times faster than Quicksort, fastest is 20 times. Traditional counting sorts and bucket sorts cannot handle such a large range of values because the performance is worse than Quicksort.

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

[Blog](https://mp.weixin.qq.com/s/uGNQxpBohPmlgxsHrE4pFg)
