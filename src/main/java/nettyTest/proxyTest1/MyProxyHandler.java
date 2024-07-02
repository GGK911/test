package nettyTest.proxyTest1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import org.apache.commons.codec.binary.Base64;

public class MyProxyHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String remoteHost;
    private final int remotePort;

    public MyProxyHandler(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 解析和打印请求
        System.out.println("Decoding message: " + request);
        ByteBuf content = request.content();
        byte[] reqBytes = new byte[content.readableBytes()];
        content.readBytes(reqBytes);
        String decodedMessage = new String(reqBytes, CharsetUtil.UTF_8);
        System.out.println("Inbound message: " + decodedMessage);

        // Base64 解码
        byte[] decodedBytes = Base64.decodeBase64(decodedMessage);
        String decodedString = new String(decodedBytes, CharsetUtil.UTF_8);
        // String decodedString = decodedMessage;
        System.out.println("Decoded Base64 message: " + decodedString);

        // 转发请求到目标服务器
        forwardRequest(ctx, request, decodedString);
    }

    private void forwardRequest(ChannelHandlerContext ctx, FullHttpRequest request, String decodedString) {
        Bootstrap b = new Bootstrap();
        b.group(ctx.channel().eventLoop()) // Use the same EventLoop
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new HttpClientCodec());
                        ch.pipeline().addLast(new HttpObjectAggregator(65536));
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext proxyCtx, FullHttpResponse response) throws Exception {
                                // 处理目标服务器的响应并返回给客户端
                                byte[] responseBytes = new byte[response.content().readableBytes()];
                                response.content().readBytes(responseBytes);
                                // String encodedResponse = new String(responseBytes);
                                String encodedResponse = Base64.encodeBase64String(responseBytes);
                                FullHttpResponse clientResponse = new DefaultFullHttpResponse(
                                        HttpVersion.HTTP_1_1, response.status(), Unpooled.copiedBuffer(encodedResponse, CharsetUtil.UTF_8));
                                clientResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
                                clientResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, clientResponse.content().readableBytes());
                                ctx.writeAndFlush(clientResponse).addListener(ChannelFutureListener.CLOSE);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext proxyCtx, Throwable cause) throws Exception {
                                cause.printStackTrace();
                                proxyCtx.close();
                            }
                        });
                    }
                });

        ChannelFuture f = b.connect(remoteHost, remotePort);
        f.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                FullHttpRequest proxyRequest = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1, request.method(), request.uri(), Unpooled.copiedBuffer(decodedString, CharsetUtil.UTF_8));
                proxyRequest.headers().set(request.headers());
                proxyRequest.headers().set(HttpHeaderNames.HOST, remoteHost + ":" + remotePort);
                proxyRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, proxyRequest.content().readableBytes());
                future.channel().writeAndFlush(proxyRequest);
            } else {
                future.cause().printStackTrace();
                ctx.close();
            }
        });
    }
}
