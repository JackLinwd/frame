package org.lwd.frame.carousel;

import java.util.Map;

/**
 * Carousel服务注册器，在系统启动时自动注册服务。
 *
 * @author lwd
 */
public interface CarouselRegister {
    /**
     * 获取注册Carousel服务集。key为服务key，value为服务URI地址。
     *
     * @return Carousel服务集；不可为null。
     */
    Map<String, String> getKeyService();
}
