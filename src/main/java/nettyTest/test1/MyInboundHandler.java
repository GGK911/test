package nettyTest.test1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import org.apache.commons.codec.binary.Base64;

public class MyInboundHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        System.out.println("Decoding message: " + request);
        ByteBuf content = request.content();
        byte[] reqBytes = new byte[content.readableBytes()];
        content.readBytes(reqBytes);
        String decodedMessage = new String(reqBytes, CharsetUtil.UTF_8);
        System.out.println("Inbound message: " + decodedMessage);

        // Base64 解码
        byte[] decodedBytes = Base64.decodeBase64(decodedMessage);
        String decodedString = new String(decodedBytes, CharsetUtil.UTF_8);
        System.out.println("Decoded Base64 message: " + decodedString);

        // 构建响应
        String responseContent = "Response from server: " + decodedString;
        ByteBuf responseBytes = Unpooled.copiedBuffer(responseContent, CharsetUtil.UTF_8);

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK, responseBytes);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, responseBytes.readableBytes());

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
