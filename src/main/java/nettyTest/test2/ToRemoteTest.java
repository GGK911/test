package nettyTest.test2;


import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 协同签名：将本地8079端口，代理到远程 cryptoken.tpddns.cn 60079端口
 */
public class ToRemoteTest {

    public static void main(String[] args) throws InterruptedException {
        // new ToRemoteTest().start(8079, "127.0.0.1", 8078);
        new ToRemoteTest().start(8079, "cryptoken.tpddns.cn", 60079);
    }

    public void start(int localPort, String remoteHost, int remotePort) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        EventLoopGroup clientGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpServerCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(65536));
                            ch.pipeline().addLast(new HttpRequestHandler(remoteHost, remotePort, clientGroup));
                        }
                    });

            Channel ch = b.bind(localPort).sync().channel();
            System.out.println("Proxy server started on port " + localPort);
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            clientGroup.shutdownGracefully();
        }
    }

    private static class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        private final String remoteHost;
        private final int remotePort;
        private final EventLoopGroup clientGroup;

        public HttpRequestHandler(String remoteHost, int remotePort, EventLoopGroup clientGroup) {
            this.remoteHost = remoteHost;
            this.remotePort = remotePort;
            this.clientGroup = clientGroup;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
            Bootstrap b = new Bootstrap();
            b.group(clientGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpClientCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(65536));
                            ch.pipeline().addLast(new HttpResponseHandler(ctx.channel()));
                        }
                    });

            ChannelFuture f = b.connect(remoteHost, remotePort).sync();
            f.channel().writeAndFlush(request.retain());
            f.channel().closeFuture().addListener((ChannelFutureListener) future -> ctx.channel().close());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }

    private static class HttpResponseHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

        private final Channel clientChannel;

        public HttpResponseHandler(Channel clientChannel) {
            this.clientChannel = clientChannel;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) throws Exception {
            clientChannel.writeAndFlush(response.retain());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}

