import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class ChenSort {
    private static final NumberFormat sPercentInstance = NumberFormat.getPercentInstance();

    static {
        sPercentInstance.setMinimumFractionDigits(2);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            Random random = new Random();
            Integer[] arr = new Integer[1000000];
            long maxValue = Integer.MAX_VALUE;
            long minValue = Integer.MIN_VALUE;
            long range = maxValue - minValue + 1;
            for (int j = 0; j < arr.length; j++) {
                arr[j] = (int) (minValue + random.nextLong(range));
            }
            Integer[] copy = new Integer[arr.length];
            System.arraycopy(arr, 0, copy, 0, arr.length);
            long start = System.currentTimeMillis();
            chenSort(arr);
            long chenSortTimeUsage = System.currentTimeMillis() - start;
            start = System.currentTimeMillis();
            Arrays.sort(copy);
            long quickSortTimeUsage = System.currentTimeMillis() - start;
            String percent = sPercentInstance.format((quickSortTimeUsage - chenSortTimeUsage) * 1.0 / quickSortTimeUsage);
            String rate = String.format("%.2f", quickSortTimeUsage * 1.0 / chenSortTimeUsage);
            System.out.println("ChenSort: " + chenSortTimeUsage + " ms, Quicksort: " + quickSortTimeUsage + " ms, " + percent + "(" + rate + "x) faster");
        }
    }

    private static void chenSort(Integer[] array) {
        int length = array.length;
        if (length < 2) {
            return;
        }

        Integer maxValue = Integer.MIN_VALUE;
        Integer minValue = Integer.MAX_VALUE;
        for (Integer element : array) {
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
            for (Integer element : array) {
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
            for (Integer element : array) {
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

        int index = 0;
        for (ArrayList<Integer> bucket : buckets) {
            if (bucket != null) {
                if (bucket.size() > 1) {
                    if (bucket.size() >= 1000) {
                        chenSort(bucket);
                    } else {
                        bucket.sort(comparator);
                    }
                    for (Integer element : bucket) {
                        array[index++] = element;
                    }
                } else {
                    array[index++] = bucket.get(0);
                }
            }
        }
    }

    private static void chenSort(ArrayList<Integer> list) {
        int length = list.size();
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

        int index = 0;
        for (ArrayList<Integer> bucket : buckets) {
            if (bucket != null) {
                if (bucket.size() > 1) {
                    if (bucket.size() >= 1000) {
                        chenSort(bucket);
                    } else {
                        bucket.sort(comparator);
                    }
                    for (Integer element : bucket) {
                        list.set(index++, element);
                    }
                } else {
                    list.set(index++, bucket.get(0));
                }
            }
        }
    }
}
