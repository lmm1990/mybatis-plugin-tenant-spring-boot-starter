package mybatisPluginTenantSpringBootStarterDemo.mapper;

import io.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter.annotation.IgnoreTenantField;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface TestMapper {

    /**
     * 添加
     */
    @IgnoreTenantField
    int add(String name);

    /**
     * 修改
     */
    int update(@Param("name") String name,@Param("id") int id);

    /**
     * 删除
     */
    int delete(int id);

    /**
     * 列表
     */
    @Select("SELECT * FROM test")
    List<Map> list();
}