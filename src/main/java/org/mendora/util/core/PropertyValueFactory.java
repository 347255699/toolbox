package org.mendora.util.core;

@FunctionalInterface
public interface PropertyValueFactory {
    /**
     * 生产数值
     *
     * @param propertyName 属性名称
     * @return 产生的数值
     */
    Object val(String propertyName);
}
