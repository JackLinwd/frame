请求参数签名
通过对请求参数进行签名与验证，可以有效防止参数在传输过程中参数被篡改，并且能拒绝非法的API请求。 1、设置密钥 & 有效时长：

## 设置签名密钥文件路径。
#frame.crypto.sign.path = /WEB-INF/sign
## 设置签名有效时长，单位：毫秒。
#frame.crypto.sign.time = 10000
sign文件：

## 设置签名密钥。
## 每行一个密钥，使用【密钥名=密钥】的形式。
## 如果密钥名为空则为默认密钥，默认密钥在未提供密钥名时使用。
=
sign文件在被修改后会自动重载，无需重启服务。

2、在请求端，可通过Sign添加签名：

package org.lwd.frame.crypto;

import java.util.Map;

/**
 * 签名。
 *
 * @author lwd
 */
public interface Sign {
    /**
     * 添加签名到Map集合中。
     *
     * @param map  要添加签名的Map集合。
     * @param name 密钥名。
     */
    void put(Map<String, String> map, String name);

    /**
     * 验证签名。
     *
     * @param map  签名数据集。
     * @param name 密钥名。
     * @return 如果验证通过则返回true；否则返回false。
     */
    boolean verify(Map<String, String> map, String name);
}
将往map参数添加sign-time、sign两个参数，分别为签名时间和签名摘要。

3、在服务端，可通过Validators.SIGN验证器进行验证：

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN, string={"key name"})
    })
如果string不为空，则使用指定密钥名的密钥进行签名。

如果请求方IP在服务端的IP白名单中，则不验证签名直接认证通过。

4、签名算法：

将所有参数（包含sign-time，但不包含sign）按名称升序排列；
将参数名与参数值用等号“=”连接；
所有参数以“&”符号连接；
将连接后的参数加上“&”+密钥；
对数据进行MD5消息摘要计算，得出sign值。
如参数为：username=frame，password=hello，captcha=123456，当前时间戳为：1483067727747（精确到毫秒），密钥为：secret key，则消息签名值为：MD5(captcha=123456&password=hello&sign-time=1483067727747&username=frame&secret key)。