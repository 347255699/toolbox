package org.mendora.util.core;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/11/21
 * desc: Bean工具类(属性)
 */
@UtilityClass
public class BeanUtil {
    private static final String FIELD_CLASS = "class";

    /**
     * 加载类成员属性名称
     *
     * @param tClass 待加载类
     * @param <T>    具体类型
     * @return 成员属性名称列表
     */
    @SneakyThrows
    public <T> Set<String> getPropertyNames(Class<T> tClass) {
        return getPropertyDescriptors(tClass).stream()
                .map(PropertyDescriptor::getName)
                .collect(Collectors.toSet());
    }

    /**
     * 加载成员属性描述器列表
     *
     * @param tClass 待加载类
     * @param <T>    具体类型
     * @return 成员属性描述起列表
     */
    @SneakyThrows
    public <T> List<PropertyDescriptor> getPropertyDescriptors(Class<T> tClass) {
        return Arrays.stream(Introspector.getBeanInfo(tClass).getPropertyDescriptors())
                .filter(pd -> !pd.getName().equals(FIELD_CLASS))
                .collect(Collectors.toList());
    }

    /**
     * 为成员属性填充数据
     *
     * @param t   待填充对象实例
     * @param pvf 属性值工厂
     * @param <T> 具体类型
     * @return 具体填充类实例
     */
    @SneakyThrows
    public <T> T filling(T t, PropertyValueFactory pvf) {
        List<PropertyDescriptor> pds = getPropertyDescriptors(t.getClass());
        for (PropertyDescriptor pd : pds) {
            pd.getWriteMethod().invoke(t, pvf.val(pd.getName()));
        }
        return t;
    }

    /**
     * 映射为Map
     *
     * @param t   具体实例
     * @param <T> 具体类型
     * @return Map
     */
    @SneakyThrows
    public <T> Map<String, Object> toMap(T t) {
        final List<PropertyDescriptor> pds = BeanUtil.getPropertyDescriptors(t.getClass());
        final Map<String, Object> args = new HashMap<>(pds.size(), 1.0F);
        for (PropertyDescriptor pd : pds) {
            args.put(pd.getName(), pd.getReadMethod().invoke(t));
        }
        return args;
    }
}
