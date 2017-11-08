package org.lwd.frame.ctrl.validate;

/**
 * 验证规则。
 *
 * @author lwd
 */
public interface ValidateWrapper {
    /**
     * 设置验证规则。
     *
     * @param validate 验证规则。
     * @return 当前实例。
     */
    ValidateWrapper setValidate(Validate validate);

    /**
     * 设置验证规则。
     *
     * @param validator   验证器Bean名称。
     * @param parameter   验证参数名。
     * @param failureCode 验证失败错误编码。
     * @return 当前实例。
     */
    ValidateWrapper setValidate(String validator, String parameter, int failureCode);

    /**
     * 设置验证规则。
     *
     * @param validator      验证器Bean名称。
     * @param scope          验证范围。
     * @param parameter      验证参数名。
     * @param parameters     验证参数名集；同时验证多个参数，或多个参数之间进行关联验证。
     * @param emptyable      是否允许为空。
     * @param number         整数配置值数组。用于设置验证规则需要的整数参数值。
     * @param string         字符串配置值数组。用于设置验证规则需要的字符串参数值。
     * @param failureCode    验证失败错误编码。
     * @param failureKey     验证失败错误信息资源key。
     * @param failureArgKeys 验证失败错误信息参数资源key。
     * @return 当前实例。
     */
    ValidateWrapper setValidate(String validator, Validate.Scope scope, String parameter, String[] parameters, boolean emptyable,
                                int[] number, String[] string, int failureCode, String failureKey, String[] failureArgKeys);

    /**
     * 获取验证器Bean名称。
     *
     * @return 验证器Bean名称。
     */
    String getValidator();

    /**
     * 验证范围。
     *
     * @return 验证范围。
     */
    Validate.Scope getScope();

    /**
     * 获取验证参数名。
     *
     * @return 验证参数名。
     */
    String getParameter();

    /**
     * 获取验证参数名集。
     *
     * @return 验证参数名集。
     */
    String[] getParameters();

    /**
     * 获取是否允许为空。
     *
     * @return 是否允许为空。
     */
    boolean isEmptyable();

    /**
     * 获取整数配置值数组。
     *
     * @return 整数配置值数组。
     */
    int[] getNumber();

    /**
     * 获取字符串配置值数组。
     *
     * @return 字符串配置值数组。
     */
    String[] getString();

    /**
     * 获取验证失败错误编码。
     *
     * @return 验证失败错误编码。
     */
    int getFailureCode();

    /**
     * 获取验证失败错误信息资源key。
     *
     * @return 验证失败错误信息资源key。
     */
    String getFailureKey();

    /**
     * 获取验证失败错误信息参数资源key。
     *
     * @return 验证失败错误信息参数资源key。
     */
    String[] getFailureArgKeys();
}
