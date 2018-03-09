package org.lwd.frame.ctrl.validate;

import org.lwd.frame.ctrl.template.Template;

/**
 * 验证器集。
 *
 * @author lwd
 */
public interface Validators {
    /**
     * Bean名称前缀。
     */
    String PREFIX = "frame.ctrl.validate.";

    /**
     * 不为空验证器Bean名称。如果要验证的参数值不为空则返回true，否则返回false。
     * 默认错误信息key=PREFIX+empty。
     */
    String NOT_EMPTY = PREFIX + "not-empty";

    /**
     * 最大长度验证器Bean名称。如果要验证的参数值字符串长度不超过设置值则返回true；否则返回false。
     * 默认错误信息key=PREFIX+over-max-length。
     */
    String MAX_LENGTH = PREFIX + "max-length";

    /**
     * 最小长度验证器Bean名称。如果要验证的参数值字符串长度超过设置值则返回true；否则返回false。
     * 默认错误信息key=PREFIX+less-min-length。
     */
    String MIN_LENGTH = PREFIX + "min-length";

    /**
     * 格式验证器Bean名称。如果要验证的字符串格式合法则返回true；否则返回false。
     * 默认错误信息key=PREFIX+not-match-regex。
     */
    String MATCH_REGEX = PREFIX + "match-regex";

    /**
     * Email格式验证器Bean名称。如果要验证的Email格式合法则返回true；否则返回false。
     * 默认错误信息key=PREFIX+illegal-email。
     */
    String EMAIL = PREFIX + "email";

    /**
     * 手机号格式验证器Bean名称。如果要验证的手机号格式合法则返回true；否则返回false。
     * 默认错误信息key=PREFIX+illegal-mobile。
     */
    String MOBILE = PREFIX + "mobile";

    /**
     * 相同验证器Bean名称。如果要验证的两个参数值相同则返回true；否则返回false。
     * 默认错误信息key=PREFIX+not-equals。
     */
    String EQUALS = PREFIX + "equals";

    /**
     * 不相同验证器Bean名称。如果要验证的两个参数值不相同则返回true；否则返回false。
     * 默认错误信息key=PREFIX+equals。
     */
    String NOT_EQUALS = PREFIX + "not-equals";

    /**
     * 大于验证器Bean名称。如果要验证的参数值大于设置值则返回true；否则返回false。
     * 默认错误信息key=PREFIX+not-greater-than。
     */
    String GREATER_THAN = PREFIX + "greater-than";

    /**
     * 小于验证器Bean名称。如果要验证的参数值小于设置值则返回true；否则返回false。
     * 默认错误信息key=PREFIX+not-less-than。
     */
    String LESS_THAN = PREFIX + "less-than";

    /**
     * 介于验证器Bean名称。如果要验证的参数值介于设置范围则返回true；否则返回false。
     * 默认错误信息key=PREFIX+not-between。
     */
    String BETWEEN = PREFIX + "between";

    /**
     * 日期时间格式验证器Bean名称。如果验证的参数为日期时间格式数据则返回true；否则返回false。
     * 默认错误信息key=PREFIX+date-time.illegal。
     */
    String DATE_TIME = PREFIX + "date-time";

    /**
     * 纬度验证器Bean名称。如果验证的参数为纬度值则返回true；否则返回false。
     * 默认错误信息key=PREFIX+illegal-latitude。
     */
    String LATITUDE = PREFIX + "latitude";

    /**
     * 经度验证器Bean名称。如果验证的参数为经度值则返回true；否则返回false。
     * 默认错误信息key=PREFIX+illegal-longitude。
     */
    String LONGITUDE = PREFIX + "longitude";

    /**
     * ID验证器Bean名称。如果验证的参数为ID值则返回true；否则返回false。
     * 默认错误信息key=PREFIX+illegal-id。
     */
    String ID = PREFIX + "id";

    /**
     * 参数签名验证器Bean名称。如果参数签名验证合法则返回true；否则返回false。
     * 默认错误信息key=PREFIX+illegal-sign。
     */
    String SIGN = PREFIX + "sign";

    /**
     * 可信任IP验证器Bean名称。如果请求IP在可信任IP名单内则返回true；否则返回false。
     * 默认错误信息key=PREFIX+distrust-ip。
     */
    String TRUSTFUL_IP = PREFIX + "trustful-ip";

    /**
     * 验证参数是否合法。
     *
     * @param validates 验证规则集。
     * @param template  模板实例 。
     * @return 验证不合法结果值。如果验证合法则返回null，否则返回验证失败结果。
     */
    Object validate(Validate[] validates, Template template);

    /**
     * 验证参数是否合法。
     *
     * @param validates 验证规则集。
     * @param template  模板实例 。
     * @return 验证不合法结果值。如果验证合法则返回null，否则返回验证失败结果。
     */
    Object validate(ValidateWrapper[] validates, Template template);
}
