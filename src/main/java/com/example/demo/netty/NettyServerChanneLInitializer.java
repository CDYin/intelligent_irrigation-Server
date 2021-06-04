package com.example.demo.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
//服务端初始化，客户端与服务器端连接一旦创建，这个类中方法就会被回调
//设置出站编码器和入站解码器
public class NettyServerChanneLInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("decoder",new StringDecoder(CharsetUtil.UTF_8));
        ch.pipeline().addLast("encoder",new StringEncoder(CharsetUtil.UTF_8));
        ch.pipeline().addLast(new NettyServerHandler());
    }
}
