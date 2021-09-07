package mybatisPluginTenantSpringBootStarterDemo;

import io.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter.handler.TenantDataHandler;
import mybatisPluginTenantSpringBootStarterDemo.mapper.TestMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 启动类
 *
 * @author liumingming
 * @since 2021-08-19 9:32
 */
@Component
public class Runner implements CommandLineRunner {

    @Resource
    private TestMapper testMapper;

    @Override
    public void run(String... args) {
        TenantDataHandler.setTenantFieldValue(777);
        System.out.println(testMapper.add("张三"));
        System.out.println(testMapper.update("张三@", 11));
        System.out.println(testMapper.delete(11));
        System.out.println(testMapper.list());
    }
}
