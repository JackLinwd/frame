package org.lwd.frame.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author lwd
 */
@Component("frame.nio.server")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Sharable
public class ServerImpl extends Handler implements Server {
    protected ServerListener listener;
    protected EventLoopGroup group;

    @Override
    public void listen(ServerListener listener) {
        if (listener == null || listener.getPort() < 1)
            return;

        if (logger.isDebugEnable())
            logger.debug("启动监听服务[{}]。", listener.getPort());

        this.listener = listener;
        group = new NioEventLoopGroup(listener.getMaxThread());
        new ServerBootstrap().group(group).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 2048).childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(this).bind(listener.getPort()).syncUninterruptibly();

        if (logger.isInfoEnable())
            logger.info("监听服务[{}]已启动。", listener.getPort());
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        listener.accept(nioHelper.put(context));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        logger.warn(cause, "NIO服务器监听[{}]异常！", listener.getPort());
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        listener.disconnect(nioHelper.getSessionId(context));
    }

    @Override
    public void close() {
        if (group != null && !group.isShutdown())
            group.shutdownGracefully();
    }

    @Override
    protected Listener getListener() {
        return listener;
    }
}
