package springbootdubbo;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication()
@EnableDubboConfiguration //开启dubbo配置文件
@RestController
@MapperScan(value="springbootdubbo.mapper")
@Slf4j
@EnableRabbit
public class SpringbootDubboApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootDubboApplication.class, args);
    }

}
