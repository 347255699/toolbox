package org.mendora.util.core.data;

import java.util.*;

/**
 * 元组
 *
 * @author menfre
 */
public class Tuple {
    private List<Object> arr;

    private Tuple() {
    }

    /**
     * 元素大小
     *
     * @return 大小
     */
    public int size() {
        return arr.size();
    }

    /**
     * 获取元组元素值
     *
     * @param i 下标
     * @return 元组元素值
     */
    public Object getValue(int i) {
        return arr.get(i);
    }

    /**
     * 设置数组元素
     *
     * @param args 参数
     * @return 元组实例
     */
    private Tuple setArr(Object... args) {
        this.arr = Arrays.asList(args);
        return this;
    }

    /**
     * 元组构造方法
     *
     * @param args 参数
     * @return 元组实例
     */
    public static Tuple of(Object... args) {
        return new Tuple().setArr(args);
    }
}

