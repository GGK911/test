package springTest.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.security.Principal;

/**
 * @author TangHaoKai
 * @version V1.0 2024/11/14 19:09
 */
@Slf4j
@Component
public class SocketListener {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("触发了SocketConnect监听器");
        Message<byte[]> message = event.getMessage();
        MessageHeaders headers = message.getHeaders();
        if (headers.containsKey("simpSessionId")) {
            String simpSessionId = (String) headers.get("simpSessionId");
            log.info("连接sessionId：" + simpSessionId);
        }
        // if (headers.containsKey("simpMessageType")) {
        //     SimpMessageType simpMessageType = (SimpMessageType) headers.get("simpMessageType");
        // }
        //
        // byte[] payload = message.getPayload();
        Principal user = event.getUser();
    }

}
