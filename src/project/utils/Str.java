package project.utils;

/**
 * 字符串工具类
 * 提供字符串相关的辅助方法
 */
public class Str {

    /**
     * 判断字符串是否为空
     * @param str 要检查的字符串
     * @return 如果字符串为 null 或只包含空白字符，返回 true；否则返回 false
     */
    public static boolean isEmpty(String str) {
        // 检查字符串是否为 null 或去除首尾空白后是否为空字符串
        if (str == null || "".equals(str.trim())) {
            return true;
        } else {
            return false;
        }
    }
}

