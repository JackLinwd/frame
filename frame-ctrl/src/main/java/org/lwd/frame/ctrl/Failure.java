package org.lwd.frame.ctrl;

/**
 * @author lwd
 */
public enum Failure {
    /**
     * 无权限。
     */
    NotPermit("frame.ctrl.not-permit"),
    /**
     * 安全隐患。
     */
    Danger("frame.ctrl.danger"),
    /**
     * 系统繁忙。
     */
    Busy("frame.ctrl.busy"),
    /**
     * 运行期异常。
     */
    Exception("frame.ctrl.exception");

    private String messageKey;

    Failure(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
