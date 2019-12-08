package org.mendora.util.core.data;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 属性名称变换类
 *
 * @author menfre
 */
@UtilityClass
public class PropertyNameMap {
    private static Pattern linePattern = Pattern.compile("_(\\w)");
    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 下划线 -> 驼峰
     *
     * @param token 字符单位
     * @return 变换后的字符串
     */
    public String lineToHump(String token) {
        token = token.toLowerCase();
        final Matcher matcher = linePattern.matcher(token);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰 -> 下划线
     *
     * @param token 字符单位
     * @return 变换后的字符串
     */
    public String humpToLine(String token) {
        final Matcher matcher = humpPattern.matcher(token);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.start() == 0 ? matcher.group(0).toLowerCase() : "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
