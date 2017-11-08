package org.lwd.frame.ctrl.validate;

/**
 * @author lwd
 */
public interface Validator {
    /**
     * 验证参数是否合法。
     *
     * @param validate  验证规则。
     * @param parameter 验证参数值。
     * @return 验证结果。如果合法则返回true；否则返回false。
     */
    boolean validate(ValidateWrapper validate, String parameter);

    /**
     * 验证参数是否合法。
     *
     * @param validate   验证规则。
     * @param parameters 验证参数值集。
     * @return 验证结果。如果合法则返回true；否则返回false。
     */
    boolean validate(ValidateWrapper validate, String[] parameters);

    /**
     * 获取验证失败错误编码。
     *
     * @param validate 验证规则。
     * @return 验证失败错误编码。
     */
    int getFailureCode(ValidateWrapper validate);

    /**
     * 获得验证失败错误信息。
     *
     * @param validate 验证规则。
     * @return 验证失败错误信息。
     */
    String getFailureMessage(ValidateWrapper validate);
}
