package springbootdubbo.listener;


import com.alibaba.dubbo.config.annotation.Reference;
import com.example.springbootdubbo.dict.ParamDict;
import com.example.springbootdubbo.po.Message;
import com.example.springbootdubbo.po.ResultObject;
import com.example.springbootdubbo.service.MessageService;
import com.example.springbootdubbo.service.ProductService;
import com.example.springbootdubbo.vo.MessageVo;
import com.example.springbootdubbo.vo.OrderVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@RabbitListener(queues = ParamDict.ORDERQUEUE)
@Component
@Slf4j
public class RabbitMqListener {

    @Reference(interfaceClass = MessageService.class,version = "1.0.0",check = false)
    private MessageService messageService;

    @Autowired
    private ProductService productService;


    @RabbitHandler
    public void handle(Map map) throws JsonProcessingException {
        log.info("==============listener message is"+map);
        String messageId = (String)map.get("messageId");
        OrderVo orderVo = new ObjectMapper().readValue(((String)map.get("messageData")),OrderVo.class)  ;
        MessageVo messageVo = new MessageVo();
        messageVo.setMessageId(messageId);
        ResultObject resultObject = messageService.queryMessage(messageVo);
        Message message = (Message) resultObject.getData();
        boolean flag=false;
        if(message!=null) {
            try{
                ResultObject resultObject1 = productService.reduceStock(orderVo);
                if(!"-1".equals(resultObject1.getCode())){
                    flag=true;
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(flag){
                    message=new Message();
                    message.setMessageId(messageId);
                    messageService.delMessage(message);
                }else{
                    message=new Message();
                    message.setMessageId(messageId);
                    message.setState(String.valueOf(ParamDict.MessageState.SENDFINISH.getCode()));
                    messageService.updateMessage(message);
                }
            }
        }else{
            log.info("==============消息已被处理");
        }
    }
}
