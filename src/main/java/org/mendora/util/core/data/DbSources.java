package org.mendora.util.core.data;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DbSources {
    private String username;

    private String password;

    private String url;

    private String driverClass;

    public static final String DEFAULT_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    public static final String URL_TEMPLATE = "jdbc:mysql://[host]:[port]/[database]?cachePrepStmts=true&useUnicode=true&characterEncoding=utf8";
}
