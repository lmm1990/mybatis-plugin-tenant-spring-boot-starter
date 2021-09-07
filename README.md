# mybatis-plugin-tenant-spring-boot-starter

mybatis 多租户插件

## 💿 快速开始

### 配置租户信息

application.yml
```
mybatis:
  tenant-plugin:
    # 多租户字段名，自动识别数据表别名
    field-name: a.tenantId
    # 多租户忽略表名，多个以英文逗号分割
    ignore-table-names: test,user
```

### 多租户忽略Mapper方法

```
import io.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter.annotation.IgnoreTenantField;

public interface TestMapper {

    /**
     * 添加
     */
    @IgnoreTenantField
    int add(String name);
}
```

### 设置租户值

```
import io.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter.handler.TenantDataHandler;

TenantDataHandler.setTenantFieldValue(777);
```