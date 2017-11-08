package org.lwd.frame.carousel;

import java.util.Map;

/**
 * @author lwd
 */
public interface CarouselHelper {
    /**
     * 更新消息配置。
     *
     * @param name          配置名称。
     * @param description   配置描述。
     * @param actionBuilder Action构造器。
     * @return 配置结果；配置成功则返回true，否则返回false。
     */
    boolean config(String name, String description, ActionBuilder actionBuilder);

    /**
     * 更新消息配置。
     *
     * @param name          配置名称。
     * @param description   配置描述。
     * @param actionBuilder Action构造器。
     * @param delay         延迟时间，单位：秒。
     * @param interval      重复执行间隔，单位：秒。
     * @param times         重复执行次数。
     * @param wait          是否等待执行结果；true-等待，false-不等待。
     * @return 配置结果；配置成功则返回true，否则返回false。
     */
    boolean config(String name, String description, ActionBuilder actionBuilder, int delay, int interval, int times, boolean wait);

    /**
     * 执行消息流程。
     *
     * @param name   流程配置名称。
     * @param delay  延迟时间，单位：秒。
     * @param header 请求头信息。
     * @param data   数据。
     * @return 执行结果。
     */
    String process(String name, int delay, Map<String, String> header, String data);

    /**
     * 注册服务。
     *
     * @param key     服务key。
     * @param service 服务URI地址。
     * @return 注册结果；注册成功则返回true；否则返回false。
     */
    boolean register(String key, String service);

    /**
     * 执行服务。
     *
     * @param key       服务key。
     * @param header    头信息。
     * @param parameter 请求参数。
     * @return 执行结果。
     */
    String service(String key, Map<String, String> header, Map<String, String> parameter);

    /**
     * 执行服务。
     *
     * @param key       服务key。
     * @param header    头信息。
     * @param parameter 请求参数。
     * @param cacheTime 缓存时长，0表示不缓存，单位：分钟。
     * @return 执行结果。
     */
    String service(String key, Map<String, String> header, Map<String, String> parameter, int cacheTime);

    /**
     * 添加参数签名到参数集中。
     * 如果为目标服务为本地服务，则不添加。
     *
     * @param serviceKey 服务key。
     * @param signKey    签名key。
     * @param parameter  参数集。
     */
    void sign(String serviceKey, String signKey, Map<String, String> parameter);
}
