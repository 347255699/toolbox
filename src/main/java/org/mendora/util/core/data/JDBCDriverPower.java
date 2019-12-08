package org.mendora.util.core.data;

import lombok.SneakyThrows;
import org.mendora.util.core.BeanUtil;

import java.beans.PropertyDescriptor;
import java.sql.*;
import java.util.*;

/**
 * @author menfre
 * @version 1.0
 * date: 2019/10/31
 * desc: JDBC 驱动增强
 */
public class JDBCDriverPower {
    /**
     * 数据库链接
     */
    private Connection conn;

    private JDBCDriverPower() {
    }

    private static class JDBCDriverPowerHolder {
        private static final JDBCDriverPower INSTANCE = new JDBCDriverPower();
    }

    public static JDBCDriverPower getInstance() {
        return JDBCDriverPowerHolder.INSTANCE;
    }

    /**
     * 检查链接
     *
     * @param sources 数据源
     * @return 布尔，显示链接正常与否
     */
    @SneakyThrows
    public boolean connectTest(DbSources sources) {
        Class.forName(sources.getDriverClass());
        conn = DriverManager.getConnection(sources.getUrl(), sources.getUsername(), sources.getPassword());
        return conn != null && !conn.isClosed();
    }

    /**
     * 带占位符，查询记录列表
     *
     * @param sql    查询语句
     * @param tuple  参数元组
     * @param tClass 待映射类
     * @param <T>    具体映射类
     * @return 映射类列表
     */
    @SneakyThrows
    public <T> List<T> find(String sql, Tuple tuple, Class<T> tClass) {
        final PreparedStatement stat = conn.prepareStatement(sql);
        // 替换占位符
        for (int i = 0; i < tuple.size(); i++) {
            stat.setObject(i + 1, tuple.getValue(i));
        }
        // 映射
        final List<T> ts = mapToObject(tClass, stat.executeQuery());
        stat.close();
        return ts;
    }

    /**
     * 无占位符，查询记录列表
     *
     * @param sql    查询语句
     * @param tClass 待映射类
     * @param <T>    具体映射类
     * @return 映射类列表
     */
    @SneakyThrows
    public <T> List<T> find(String sql, Class<T> tClass) {
        final Statement stat = conn.createStatement();
        final List<T> ts = mapToObject(tClass, stat.executeQuery(sql));
        stat.close();
        return ts;
    }

    /**
     * 带占位符，查询单条记录
     *
     * @param sql    查询语句
     * @param tuple  参数元组
     * @param tClass 待映射类
     * @param <T>    具体映射类
     * @return 映射类
     */
    @SneakyThrows
    public <T> T findOne(String sql, Tuple tuple, Class<T> tClass) {
        final PreparedStatement stat = conn.prepareStatement(sql);
        for (int i = 0; i < tuple.size(); i++) {
            stat.setObject(i + 1, tuple.getValue(i));
        }
        final T t = mapToObject(tClass, stat.executeQuery()).get(0);
        stat.close();
        return t;
    }

    /**
     * 无占位符，查询单条记录
     *
     * @param sql    查询语句
     * @param tClass 待映射类
     * @param <T>    具体映射类
     * @return 映射类
     */
    @SneakyThrows
    public <T> T findOne(String sql, Class<T> tClass) {
        final Statement stat = conn.createStatement();
        final List<T> ts = mapToObject(tClass, stat.executeQuery(sql));
        stat.close();
        return ts.get(0);
    }

    /**
     * 批量更新
     *
     * @param sql    更新语句
     * @param tuples 多元组
     * @return 变更记录数
     */
    @SneakyThrows
    public List<Integer> batchUpdate(String sql, List<Tuple> tuples) {
        final PreparedStatement stat = conn.prepareStatement(sql);
        final List<Integer> lines = new ArrayList<>(tuples.size());
        for (Tuple tuple : tuples) {
            for (int i = 0; i < tuple.size(); i++) {
                stat.setObject(i + 1, tuple.getValue(i));
            }
            lines.add(stat.executeUpdate());
        }
        stat.close();
        return lines;
    }

    /**
     * 单行更新
     *
     * @param sql   更新语句
     * @param tuple 元组
     * @return 变更记录，一般为 1
     */
    @SneakyThrows
    public int update(String sql, Tuple tuple) {
        final List<Tuple> tuples = new ArrayList<>(1);
        tuples.add(tuple);
        return batchUpdate(sql, tuples).get(0);
    }

    /**
     * 保存
     *
     * @param t   保存实体
     * @param <T> 具体类型
     * @return 变更记录，一般为 1
     */
    @SneakyThrows
    public <T> int save(T t) {
        return save(t, tableName(t));
    }

    /**
     * 保存
     *
     * @param t         保存实体
     * @param <T>       具体类型
     * @param tableName 表名称
     * @return 变更记录，一般为 1
     */
    public <T> int save(T t, String tableName) throws Exception {
        final Statement stat = conn.createStatement();
        final int save = stat.executeUpdate(buildInsertIntoSql(t, tableName));
        stat.close();
        return save;
    }

    /**
     * 批量保存
     *
     * @param tList 保存实体列表
     * @param <T>   具体类型
     * @return 变更记录数
     */
    @SneakyThrows
    public <T> List<Integer> batchSave(List<T> tList) {
        return batchSave(tList, tableName(tList.get(0)));
    }

    /**
     * 批量保存
     *
     * @param tList     保存实体列表
     * @param <T>       具体类型
     * @param tableName 表名称
     * @return 变更记录数
     */
    @SneakyThrows
    public <T> List<Integer> batchSave(List<T> tList, String tableName) {
        final Statement stat = conn.createStatement();
        final List<Integer> saves = new ArrayList<>(tList.size());
        for (T t : tList) {
            saves.add(stat.executeUpdate(buildInsertIntoSql(t, tableName)));
        }
        stat.close();
        return saves;
    }

    /**
     * 关闭链接
     */
    @SneakyThrows
    public void close() {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    /**
     * 映射属性值
     *
     * @param propertyName 属性名称
     * @param rs           查询结果集
     * @return 属性值
     */
    @SneakyThrows
    private Object valOfResultSet(String propertyName, ResultSet rs) {
        return rs.getObject(PropertyNameMap.humpToLine(propertyName));
    }

    /**
     * 映射结果集
     *
     * @param tClass 类型
     * @param rs     结果集
     * @param <T>    具体类型
     * @return 映射类型列表
     */
    @SneakyThrows
    private <T> List<T> mapToObject(Class<T> tClass, ResultSet rs) {
        final List<T> mapBody = new ArrayList<>();
        while (rs.next()) {
            final T t = tClass.newInstance();
            // 填充
            mapBody.add(BeanUtil.filling(t, k -> valOfResultSet(k, rs)));
        }
        rs.close();
        return mapBody;
    }

    /**
     * className -> tableName
     *
     * @param t   类型实例
     * @param <T> 具体类型
     * @return 对应的表名称
     */
    private <T> String tableName(T t) {
        String className = t.getClass().getName();
        className = className.substring(className.lastIndexOf(".") + 1);
        return PropertyNameMap.humpToLine(className);
    }

    /**
     * 构造 insert into 语句
     *
     * @param t         类型实例
     * @param tableName 表名
     * @param <T>       具体类型
     * @return insert into 语句
     */
    @SneakyThrows
    private <T> String buildInsertIntoSql(T t, String tableName) {
        final List<PropertyDescriptor> pds = BeanUtil.getPropertyDescriptors(t.getClass());
        final Map<String, Object> params = new HashMap<>(pds.size() - (pds.size() % 2) + 2, 1F);
        for (PropertyDescriptor pd : pds) {
            params.put(pd.getName(), pd.getReadMethod().invoke(t));
        }
        final StringJoiner fields = new StringJoiner(", ", "INSERT INTO " + tableName + "(", ")");
        final StringJoiner values = new StringJoiner(", ", "VALUES(", ");");
        params.keySet().forEach(k -> {
            fields.add(PropertyNameMap.humpToLine(k));
            values.add("'" + String.valueOf(params.get(k)) + "'");
        });
        return fields.toString().concat(values.toString());
    }
}
