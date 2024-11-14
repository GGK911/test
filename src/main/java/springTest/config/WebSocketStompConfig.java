package springTest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.servlet.http.HttpSession;

/**
 * 增加一个配置类，用于定义 WebSocket 全局配置信息
 *
 * @author TangHaoKai
 * @version V1.0 2024/11/14 14:56
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 注册stomp端点
     * 将 "/stomp" 注册为一个 STOMP 端点。这个路径与之前发送和接收消息的目的地路径有所不同。这是一个端点，客户端在订阅或发布消息到目的地路径前，要连接到该端点。
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //  允许使用socketJs方式访问 即可通过http://IP:PORT/xboot/ws来和服务端websocket连接
        registry.addEndpoint("/stomp")
                .setHandshakeHandler((request, response, wsHandler, attributes) -> {
                    if (request instanceof ServletServerHttpRequest) {
                        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                        HttpSession session = servletRequest.getServletRequest().getSession();

                        attributes.put("sessionId", session.getId());
                    }
                    return true;
                })
                .setAllowedOrigins("*")
                .withSockJS();
    }

    /**
     * 配置信息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // 订阅Broker名称 user点对点 topic广播即群发
        registry.enableSimpleBroker("/queue/", "/topic/");
        // 全局(客户端)使用的消息前缀
        registry.setApplicationDestinationPrefixes("/app");
        // 点对点使用的前缀 无需配置 默认/user
        registry.setUserDestinationPrefix("/user");
    }

}
