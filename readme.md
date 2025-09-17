# 一、环境搭建

## 1. 搭建前端环境

这里使用的是 Vue3 Element Admin，它是一个免费开源的中后台模版。基于 `vue3` + `ElementPlus` + `Vite` 开发，是一个开箱即用的中后台系统前端解决方案。具体步骤如下所示：

```shell
# Vue3-Element-Admin 要求 Node.js 版本 >= 12 ，推荐Node.js  16.x版本

# 使用git克隆项目 或者 直接下载项目
git clone https://github.com/huzhushan/vue3-element-admin.git

# 进入项目目录
cd vue3-element-admin

# 安装依赖
npm install

# 建议不要直接使用 cnpm 安装依赖，会有各种诡异的 bug。可以通过如下操作解决 npm 下载速度慢的问题
npm install --registry=https://registry.npm.taobao.org

# 启动服务
npm start
```

> 需要注意的是，如果使用高版本的 node 可能会导致该模板中的一些数据被强行使用高版本，就会导致不兼容的情况发生，因此推荐使用 16.x 版本的 node。

部署好的前端工程的核心目录结构如下所示：

```text
mock					// 用于测试，模拟后端接口地址
public					// 存储公共的静态资源：图片
src						// 源代码目录，非常重要
    | api				// 提供用于请求后端接口的js文件
    | assets			// 存储静态资源：图片、css
    | components		// 存储公共组件,可重用的一些组件
    | directive			// 存储自定义的一些指令
    | hooks				// 存储自定义的钩子函数
    | i18n				// 存储用于国际化的js文件
    | layout			// 存储首页布局组件
    | pinia				// 用于进行全局状态管理
    | router			// 存储用于进行路由的js文件
    | utils				// 存储工具类的js文件
    | views				// 和路由绑定的组件
    | App.vue			// 根组件
    | default-settings.js // 默认设置的js文件
    | error-log.js		// 错误日志js文件
    | global-components.js // 全局组件的js文件
    | main.js			// 入口js文件(非常重要)
    | permission.js		// 权限相关的js文件(路由前置守卫、路由后置守卫)
vite.config.js			// vite的配置文件，可以在该配置文件中配置前端工程的端口号
```

****
## 2. 后端环境搭建

### 2.1 项目结构

该项目由一个总的父类模块进行管理，该模块进行项目依赖的统一管理，然后在这个模块里创建其余的子模块：

1、搭建 spzx-common 模块

该模块用来进行公共模块的管理，其中还有两个子模块：

- common-util：管理工具类模块
- common-service：管理公共服务功能模块，例如后续有些服务模块都会用到登录拦截的功能，那这个就可以写在 common-service 模块中

2、搭建 spzx-model 模块

该模块用来进行对实体类模块的管理，服务中需要用到实体类都写在这里，当远程调用别的服务时就可以引入该模块，避免创建多个相同的实体类。

3、搭建 spzx-manager 模块

该模块用来实现管理端的相关功能，例如商品的增删改查。该模块是管理端的父类模块，管理端的具体功能应该细分为不同的子模块，当然这些子模块互相调用时也算作远程调用。

一个项目中所涉及到的实体类往往有三种：

1. 封装请求参数的实体类：这种实体类在定义的时候往往会携带到 dto（数据传输对象），会定义在 dto 包中 
2. 与数据库对应的实体类：这种实体类往往和数据表名称保证一致，会定义在 domain、entity、pojo 包中 
3. 封装响应结果的实体类：这种实体类在定义的时候往往会携带到 vo（视图对象）字样，会定义在 vo 包中

****
### 2.2 模块的创建

先创建总的父模块，并添加一些模块中都会用到的依赖：

```xml
<!-- 指定父工程 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.0.5</version>
</parent>

<modules>
    <module>spzx-model</module>
    <module>spzx-common</module>
</modules>

<properties>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <mysql.verison>8.0.30</mysql.verison>
    <fastjson.version>2.0.21</fastjson.version>
    <lombok.version>1.18.20</lombok.version>
    <mybatis.version>3.0.1</mybatis.version>
    <mybatisplus.version>3.5.5</mybatisplus.version>
</properties>

<!-- 管理依赖，版本锁定 -->
<dependencyManagement>
    <dependencies>
        <!-- mybatisplus 依赖 -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatisplus.version}</version>
        </dependency>

        <!-- mysql 驱动 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>

        <!--fastjson-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>${fastjson.version}</version>
        </dependency>

        <!-- lombok依赖 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
        </dependency>

        <!-- redis的起步依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
    </dependencies>
</dependencyManagement>
```

然后让子模块继承该父模块：

```xml
<!-- 指定父工程 -->
<parent>
    <groupId>com.cell</groupId>
    <artifactId>spzx</artifactId>
    <version>1.0-SNAPSHOT</version>
</parent>
```

****
### 2.3 Docker 安装 MySQL

在虚拟机的目录下创建 /spzxData 用来存放该项目要用的所有容器挂载的数据卷，这里安装 MySQL，所以挂载 MySQL 的 data、conf 和 logs：

```shell
# 需要修改宿主机目录权限，让 MySQL 用户可写，MySQL 官方镜像默认容器内 mysql 用户 UID/GID = 999:999。
sudo chown -R 999:999 /spzxData/mysql/data
sudo chown -R 999:999 /spzxData/mysql/logs
sudo chown -R 999:999 /spzxData/mysql/conf
```

```shell
sudo docker run -d \
  --name mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=123 \
  -e TZ=Asia/Shanghai \
  -v /spzxData/mysql/data:/var/lib/mysql \
  -v /spzxData/mysql/conf:/etc/mysql/conf.d \
  -v /spzxData/mysql/logs:/var/log/mysql \
  --restart=unless-stopped \
  mysql:8.0.30 \
  --character-set-server=utf8mb4 \
  --collation-server=utf8mb4_unicode_ci
```

给数据库中添加对应的表后，可以在虚拟机看到已经挂载的目录了：

```shell
[root@192 ~]# ls /spzxData/mysql/data/
auto.cnf       binlog.000003  ca.pem           db_spzx            ib_buffer_pool  #innodb_redo  mysql.ibd           private_key.pem  server-key.pem  undo_002
binlog.000001  binlog.index   client-cert.pem  #ib_16384_0.dblwr  ibdata1         #innodb_temp  mysql.sock          public_key.pem   sys
binlog.000002  ca-key.pem     client-key.pem   #ib_16384_1.dblwr  ibtmp1          mysql         performance_schema  server-cert.pem  undo_001
```

****
### 2.4 整合 swagger

在 model 服务中添加 knife4j 的依赖，它是 swagger 的增强工具：

```xml
<!-- knife4j -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>4.4.0</version>
</dependency>
```

配置一个基础的 swagger 配置类，提供给其它服务使用：

```java
@Configuration
public class BaseKnife4jConfig {
    /**
     * 创建通用的 API 信息，各个模块可以自动注入（@Autowired）这个 Bean 并修改配置信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("尚品甑选 API 接口文档")
                        .version("1.0")
                        .description("尚品甑选 API 接口文档")
                        .contact(new Contact().name("cell")));
    }
}
```

例如每个业务模块中创建独立配置和分组：

```java
@Configuration
public class UserSwaggerConfig extends BaseSwaggerConfig { // 继承基础配置
    /**
     * 重写或自定义本模块的 OpenAPI 信息
     */
    @Bean
    public OpenAPI userOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("用户服务 API 文档")
                        .version("1.0")
                        .description("用户管理相关接口"));
    }
}
```

当然还可以配置相关的配置文件进行更细致的配置：

```yaml
server:
  port: ...

spring:
  application:
    name: user-service

springdoc:
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
    groups-order: DESC
  api-docs:
    path: /v3/api-docs
  group-configs:
    - group: 'user-service' # 这里配置默认分组
      paths-to-match: '/**'
      packages-to-scan: com.example.user.controller

knife4j:
  enable: true
  setting:
    language: zh_cn
```

****
# 二、





