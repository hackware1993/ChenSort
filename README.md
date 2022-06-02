**The time complexity is O(n), the space complexity is O(n), and it is stable**

Dart code:

```dart
void chenSort(List<int> list) {
  int max = -2 ^ 63;
  for (final element in list) {
    if (element > max) {
      max = element;
    }
  }
  int slot;
  List<List<int>?> buckets = List.filled(list.length + 1, null);
  double factor = list.length / max;
  for (final element in list) {
    slot = (element * factor).toInt();
    if (buckets[slot] == null) {
      buckets[slot] = [];
    }
    buckets[slot]!.add(element);
  }
  int compare(int left, int right) {
    return left - right;
  }

  int index = 0;
  for (final bucket in buckets) {
    if (bucket != null) {
      if (bucket.length > 1) {
        bucket.sort(compare);
      }
      for (final element in bucket) {
        list[index++] = element;
      }
    }
  }
}
```

[Blog](https://mp.weixin.qq.com/s?__biz=Mzk0NTM3MzM4OQ==&amp;mid=2247483744&amp;idx=1&amp;sn=b3680981b81ad58b8bf781647aa18ee5&amp;chksm=c31728c2f460a1d41536fb10a210d576373783faa0b47226833ecf05a5a7c8d6a0824e2b54a1&token=239406941&lang=zh_CN#rd)