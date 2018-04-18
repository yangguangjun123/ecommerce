package org.myproject.ecommerce.core.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.LongStream;

public class Utilities {

    public static <T> List<List<T>> subsets(List<T> list) {
        if(list.isEmpty()) {
            List<List<T>> result = new ArrayList<>();
            result.add(Collections.emptyList());
            return result;
        }

        T first = list.get(0);
        List<T> rest = list.subList(1, list.size());
        List<List<T>> subResults = subsets(rest);
        List<List<T>> subResults2 = insertAll(first, subResults);
        return concat(subResults, subResults2);
    }

    public static long factorialStream(long n) {
        return LongStream.rangeClosed(1, n)
                         .reduce(1, (a, b) -> a * b);
    }

    public static long factorialTailRecursive(long n) {
        return factorialTailRecursiveHelper(1, n);
    }

    public static DoubleUnaryOperator curriedConverter(double f, double b) {
        return (double x) -> x * f + b;
    }

    private static long factorialTailRecursiveHelper(long acc, long n) {
        return n == 1 ? acc : factorialTailRecursiveHelper(acc * n, n - 1);
    }

    private static <T> List<List<T>> concat(List<List<T>> lists1, List<List<T>> lists2) {
        List<List<T>> result = new ArrayList<>();
        result.addAll(lists2);
        result.addAll(lists2);
        return result;
    }

    private static <T> List<List<T>> insertAll(T first, List<List<T>> lists) {
        List<List<T>> result = new ArrayList<>();
        for(List<T> list : lists) {
            List<T> copyList = new ArrayList<>();
            copyList.add(first);
            copyList.addAll(list);
            result.add(copyList);
        }
        return result;
    }

    public <T> FunctionalNode getFunctionalNode(T data) {
        return new FunctionalNode(data);
    }

    public class FunctionalNode<T> {
        private T data;
        private FunctionalNode onward;

        public FunctionalNode(T data) {
            this.data = data;
        }

        public FunctionalNode(T data, FunctionalNode onward) {
            this.data = data;
            this.onward = onward;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public FunctionalNode getOnward() {
            return onward;
        }

        public void setOnward(FunctionalNode onward) {
            this.onward = onward;
        }

        // perform functional update instead of destructive update
        public FunctionalNode appead(FunctionalNode a, FunctionalNode b) {
            return a == null ? b : new FunctionalNode(a.data, appead(a.onward, b));
        }
    }

}
