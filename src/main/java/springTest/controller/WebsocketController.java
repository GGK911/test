package springTest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * websocket接口测试
 *
 * @author TangHaoKai
 * @version V1.0 2024/11/14 20:14
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class WebsocketController {
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 接收然后转发至客户端消息
     */
    @MessageMapping("/top")
    public String top(String message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId(); // 获取 sessionId
        log.info("某人的 Session ID： " + sessionId);
        log.info("服务器接收某人发来的信息：" + message);
        return message;
    }

    /**
     * 某人
     */
    @MessageMapping("/top2")
    @SendToUser("/topic/reply")
    public Object notifyOne(@Payload String message,
                            Principal principal,
                            SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId(); // 获取 sessionId
        log.info("某人的 Session ID： " + sessionId);
        log.info("服务器接收某人发来的信息：" + message);
        return message;
    }

    /**
     * 推送广播消息
     */
    @RequestMapping("/hello/notifyAllSocket")
    public Object notifyAllSocket(String message) {
        messagingTemplate.convertAndSend("/topic/notify", message);
        return "广播成功";
    }

    /**
     * 推送某人消息
     */
    @RequestMapping("/hello/notifyOne")
    public Object notifyOne(String message, String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        MessageHeaders messageHeaders = headerAccessor.getMessageHeaders();
        messagingTemplate.convertAndSendToUser(sessionId, "/topic/reply", message, messageHeaders);
        return "推送某人成功";
    }

}
