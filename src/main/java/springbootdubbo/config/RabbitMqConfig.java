package springbootdubbo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class RabbitMqConfig {

    @Bean
    public Queue testDirectQueue(){
        return new Queue("TestDirectQueue",true);
    }

    @Bean
    public Queue orderQueue(){
        return new Queue("orderQueue",false);
    }

    @Bean
    public DirectExchange orderExchange(){
        return new DirectExchange("orderExchange",true,false);
    }


    @Bean
    public DirectExchange testDirectExchange(){
        return new DirectExchange("TestDirectExchange",true,false);
    }

    @Bean
    Binding bindingDirect(){
        return BindingBuilder.bind(testDirectQueue()).to(testDirectExchange()).with("TestDirectRouting");
    }

    @Bean
    Binding bindingOrderDirect(){
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with("orderRouting");
    }

    @Bean
    public DirectExchange lonelyExchange(){
        return new DirectExchange("lonelyExchange",true,false);
    }

    @Bean
    public RabbitTemplate getRabTemp(ConnectionFactory factory){
        RabbitTemplate template = new RabbitTemplate();
        template.setConnectionFactory(factory);
        template.setMandatory(true);
        template.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                System.out.println("ConfirmCallback======data======"+correlationData);
                System.out.println("ConfirmCallback======确认情况======"+b);
                System.out.println("ConfirmCallback======原因======"+s);
            }
        });
        template.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                System.out.println("ReturnsCallback======消息======"+returnedMessage.getMessage());
                System.out.println("ReturnsCallback======回应码======"+returnedMessage.getReplyCode());
                System.out.println("ReturnsCallback======回应信息======"+returnedMessage.getReplyText());
                System.out.println("ReturnsCallback======交换机======"+returnedMessage.getExchange());
                System.out.println("ReturnsCallback======路由键======"+returnedMessage.getRoutingKey());
            }
        });
        return template;
    }



}
