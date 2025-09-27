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
# 二、用户登录

## 1. 配置登录环境

### 1.1 配置 Nacos

创建一个 spzx-auth-server 模块，用来管理用户的认证服务，里面包含登录注册等功能，当然也为将来配置网关提前做好基础。对于一个新的功能模块来讲，
应该让它注册进 nacos，操作流程为：

1、创建 nacos 的本地数据库

Nacos 默认自带的是嵌入式数据库 derby，内嵌的数据库无法持久化保存数据（重启后数据丢失），所以需要切换到持久化数据库，将数据保存到 MySQL 中。
在官网下载对应版本的 Nacos zip 包，然后打开 conf 文件夹，里面有个 mysql-schema.sql 文件，这个就是用来创建 Nacos 的数据库的 sql 语句。

2、创建容器并挂载数据到 MySQL

```shell
docker run -d \
  --name nacos \
  -p 8848:8848 \
  -p 9848:9848 \
  -e PREFER_HOST_MODE=hostname \
  -e MODE=standalone \
  -e SPRING_DATASOURCE_PLATFORM=mysql \
  -e MYSQL_SERVICE_HOST=192.168.149.101 \
  -e MYSQL_SERVICE_DB_NAME=nacos_config \
  -e MYSQL_SERVICE_PORT=3306 \
  -e MYSQL_SERVICE_USER=root \
  -e MYSQL_SERVICE_PASSWORD=123 \
  -e MYSQL_SERVICE_DB_PARAM="characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai" \
  nacos/nacos-server:v2.2.3
```

需要注意的是：Nacos 在 2.x 版本后引入了 gRPC 通信，尤其是用于服务注册与发现模块，gRPC 默认监听端口 9848，而原来的 8848 仍然作为 HTTP 接口（配置管理、控制台访问）。
也就是所 1.x 版本的 Nacos 只需要暴露 8848 端口就能使用，但 2.x 版本后还需要暴露一个 9848 端口。如果看到这种报错：

```text
com.alibaba.nacos.api.exception.NacosException: Client not connected, current status: STARTING
```

很可能是客户端无法连接 gRPC 端口 9848，报错原因大致分为：容器没有暴露 9848 端口、防火墙限制、客户端版本与服务端不匹配。当然配置 Nacos 一定要在本地搭建它对应的数据库，
如果不搭建的话也可能会导致以上问题的发生。

3、注册服务到 nacos 中心

```yaml
spring:
  application:
    name: spzx-auth-server
  config:
    import:
      - optional:nacos:${spring.application.name}.yaml  # 导入配置
  cloud:
    nacos:
      config:
        server-addr: 192.168.149.101:8848 # 配置中心地址
      discovery:
        server-addr: 192.168.149.101:8848  # 服务发现地址
```

在 Spring Boot 3 + Spring Cloud Alibaba Nacos 2.x 的组合里，可以不再使用 bootstrap.yaml 文件进行服务发现了，可以直接把 Nacos 配置写在 application.yaml 里。
Spring Boot 2.x 以前版本的 acos 配置中心在 Spring Boot 启动早期就需要读取配置，因此传统做法是使用 bootstrap.yaml，bootstrap.yaml 会在 application.yaml 之前加载，
用于初始化配置中心客户端；Spring Boot 3 与新版本的 Spring Cloud 已经把 bootstrap.yaml 的功能集成到了 application.yaml 中，
默认通过 spring.config.import=optional:nacos:... 就可以在应用启动时加载 Nacos 配置，也就是说 `${spring.application.name}` 会自动替换为 spzx-auth-server，
对应 Nacos 上的配置文件，客户端启动时会自动拉取配置，不需要 bootstrap.yaml。

****
### 1.2 配置 Redis

在登录操作时会将验证码和 session 信息存入缓存中，因此需要提前配置好 Redis：

```shell
sudo docker run -d \
--name redis \
-p 6379:6379 \
-v /root/spzxData/redis/data:/data \
--restart=always \
redis:8.2
```

配置好了 Redis 也可以下载一个可视化的操作工具 AnotherRedisDesktopManager，
下载地址：(https://github.com/qishibo/AnotherRedisDesktopManager/releases)[https://github.com/qishibo/AnotherRedisDesktopManager/releases]

****
### 1.3 配置 MybatisPlus

要使用 MybatisPlus 那就得引入它的依赖，而该项目的所有实体类都放在了 spzx-model 模块中，因此需要在该模块引入依赖：

```xml
<!-- mybatisplus 依赖 -->
<dependency>
  <groupId>com.baomidou</groupId>
  <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
  <version>3.5.5</version>
</dependency>
```

而其他的服务都会用到该模块，因此需要注意引入该模块作为依赖，例如 spzx-auth-server 模块就需要引入它作为依赖，这样才能使用 MybatisPlus 查询实体类对应的表。
当然，实体类上要用 @TableName 标注表名，用 @TableId(type = IdType.AUTO) 标注为自增主键；然后就是让 Mapper 继承 BaseMapper<T>，ServiceImpl 继承 ServiceImpl<T, V>。
这样才能在 Service 或 Mapper 层使用 MybatisPlus。

配置 yaml 文件：

```yaml
# 数据源配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/spzx?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123
    driver-class-name: com.mysql.cj.jdbc.Driver

# MyBatis-Plus 提供一些功能配置（可以不配）
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 驼峰映射
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL 打印日志（如果还引入了低版本的 MyBatis 可能会报错）
  global-config:
    db-config:
      id-type: auto # 主键策略，可选 auto、input、uuid 等
      logic-delete-value: 1 # 逻辑删除值
      logic-not-delete-value: 0
```

****
## 2. 登录流程

用户在浏览器的登录页面中输入账号密码并提交登录请求后，请求首先到达 spzx-auth-server 服务并验证，如果验证通过，Spring Session 创建一个新的 Session，
并生成一个唯一的 sessionId。然后把 Session 数据存入 Redis，并将 sessionId 写入 Cookie 返回给浏览器。浏览器在后续请求中携带这个 Cookie，
Spring Session 读取 Cookie 获取 sessionId，再从 Redis 中加载对应的 Session，注入到请求上下文中，其他服务或者说 Controller、Service 曾就可以获取到。
并且每次访问需要刷新 Session 的过期时间，保证用户的会话持续有效。最后需要让 Redis 根据 TTL 管理 Session 的过期和删除。当然除了 Session 的存储，
还需要提供手机验证码的存储，前端发送验证码后将该验证码存入 Redis 中并设置合理的 TTL。

```text
浏览器
   |
   |  POST /login {username, password}
   v
[spzx-auth-server]--(验证账号密码)--> [UserDetailsService / 数据库]
   |
   v
认证成功
   |
   v
[Spring Session] 创建 Session(sessionId)
   |
   v
[Redis] 存储 Session 数据 (Key=sessionId, Value=用户信息, TTL=30min)
   |
   v
[HttpServletResponse] 写入 Cookie (SESSION=sessionId)
   |
   v
浏览器保存 Cookie
   |
   |------ 后续请求携带 Cookie ------->|
   v                                
[spzx-auth-server]              [Spring Session Filter]
   |                                 |
   v                                 v
Controller / Service 获取 request.getSession()
   |
   v
业务逻辑使用 Session 数据
   |
   v
[Redis] 刷新 Session TTL，保证会话持续有效
------------------------------------------------------
验证码流程（注册/登录前验证手机）
浏览器
   |
   |  POST /sendSmsCode {phoneNumber}
   v
[spzx-auth-server]
   |
   v
生成验证码
   |
   v
[Redis] 存储验证码 (Key=phoneNumber, Value=code, TTL=5min)
   |
   v
返回发送结果给浏览器
   |
   |------ 用户输入验证码提交 ------>|
   v
[spzx-auth-server] 验证 Redis 中验证码是否匹配
   |
   v
成功 -> 允许登录或注册
失败 -> 返回错误提示
```

****
## 3. 代码实现

### 3.1 账号密码登录

账号密码登录是最原始的一种登录，用户会在前端登录页面输入他的账号与密码并发送一个 POST 请求（GET 请求会暴露参数在 URL），后端通常使用一个实体类来接受这些数据，
接着就是拿着账号密码去比对数据库中的记录，比对成功后就登录并将用户的信息保存在 session 中，方便后续别的服务使用。

Controller 层：

Controller 层就是接收前端传递过来的数据以及 session（浏览器发送请求时会在请求头中携带 cookie），然后通过 Service 层对账号密码进行判断，成功就返回一个 LoginVo 对象，
该对象是登录成功后用来响应结果的实体类，用它来封装用户登录成功后生成的 session 数据。

```java
@Data
@Schema(description = "登录成功响应结果实体类")
public class LoginVo {

    @Schema(description = "令牌")
    private String token ;

    @Schema(description = "刷新令牌,可以为空")
    private String refresh_token ;

}
```

如果要用接口文档进行测试的话，就需要让 Controller 层配置上 swagger 并配置好配置文件：

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
    groups-order: DESC
  api-docs:
    path: /v3/api-docs
  group-configs:
    - group: 'auth-server' # 这里配置默认分组
      paths-to-match: '/**'
      packages-to-scan: com.cell.spzx.auth_server.controller

knife4j:
  enable: true
  setting:
    language: zh_cn
```

```java
@RestController("/auth-server")
@Tag(name = "LoginController", description = "登录相关接口")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Operation(summary = "用户登录", description = "使用用户名和密码登录系统")
    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody UserLoginDto userLoginDto,  HttpSession session) {
        LoginVo loginVo = loginService.login(userLoginDto, session);
        if (loginVo != null) {
            // 登录成功，将数据封装到结果集中并携带操作成功信息
            return Result.build(loginVo, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
        } else {
            // 登录失败，给出错误信息
            return Result.build(null, ResultCodeEnum.LOGIN_ERROR.getCode(), ResultCodeEnum.LOGIN_ERROR.getMessage());
        }
    }

}
```

Service 层：

在 Service 层就是进行查询数据库比对账号密码是否一致，如果一致就把用户的 id 存入 session 中，后续服务可以通过 session 拿到这个 id，也就可以通过它来判断当前登录的用户是谁。
接着再用 sessionId 作为 LoginVo 封装的 session 数据返回给 Controller 层。这里使用的 session 是 SpringSession，它可以解决跨服务丢失 cookie 的问题，
只要配置了 SpringSession 和对应的 Domain 属性，这样子域服务也能带上同一个 sessionId。

```java
@Service("loginService")
public class LoginServiceImpl extends ServiceImpl<LoginMapper, UserInfo> implements LoginService {
    @Override
    public LoginVo login(UserLoginDto userLoginDto, HttpSession session) {
        UserInfo userInfo = getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUsername, userLoginDto.getUsername()));
        if (userInfo == null) {
            return null;
        } else {
            String password = userLoginDto.getPassword();
            if (password.equals(userInfo.getPassword())) {
                // 账号密码正确，把用户 id 存入 session
                session.setAttribute("userId", userInfo.getId());
                // 获取 sessionId 作为 token
                String token = session.getId();
                LoginVo loginVo = new LoginVo();
                loginVo.setToken(token);
                return loginVo;
            } else {
                return null;
            }
        }
    }
}
```

Service 层涉及到三个对象，分别是 LoginDto、UserInfo 和 LoginVo，LoginVo 上面记录了，UserLoginDto 则是专门用来接收前端发送来的数据的对象:

```java
@Data
@Schema(description = "用户登录请求参数")
public class UserLoginDto {

    @Schema(description = "用户名")
    private String username ;

    @Schema(description = "密码")
    private String password ;
}
```

UserInfo 则是数据库表对应的实体类，它包含了登录用户的详细信息，也是 MybatisPlus 和数据库表进行连接的桥梁：

```java
@Data
@TableName("user_info")
@Schema(description = "用户实体类")
public class UserInfo extends BaseEntity {

   private static final long serialVersionUID = 1L;

   @Schema(description = "用户名")
   private String username;

   @Schema(description = "密码")
   private String password;

   @Schema(description = "昵称")
   private String nickName;

   @Schema(description = "头像")
   private String avatar;

   @Schema(description = "性别")
   private Integer sex;

   @Schema(description = "电话号码")
   private String phone;

   @Schema(description = "备注")
   private String memo;

   @Schema(description = "微信open id")
   private String openId;

   @Schema(description = "微信开放平台unionID")
   private String unionId;

   @Schema(description = "最后一次登录ip")
   private String lastLoginIp;

   @Schema(description = "最后一次登录时间")
   private Date lastLoginTime;

   @Schema(description = "状态：1为正常，0为禁止")
   private Integer status;

}
```

这里的 BaseEntity 则是抽取出来的一些实体类基本都会用到的字段，让这些实体类继承它：

```java
@Data
public class BaseEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    @Schema(description = "唯一标识")
    private Long id;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @TableLogic // 逻辑删除，拦截 deleteById(id)、delete(...) 等操作，不执行物理删除，而是执行 update 操作
    @Schema(description = "是否删除")
    private Integer isDeleted;

}
```

在 spzx-common 模块中创建 SpringSession 的配置文件，它可以解决不同服务之间 cookie 丢失的问题，不过需要配置下域名（#TODO）。

```java
@Configuration
public class SessionConfig {
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setCookieName("SPZX-SESSION"); // 修改默认的 cookie 名
        // TODO 配置 domain 解决不同服务之间的 cookie 丢失问题
        return defaultCookieSerializer;
    }

    @Bean
    public RedisSerializer<Object> redisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
```

通过接口文档的测试，输入正确的账号密码可以得预期的数据：

```json
{
  "username": "15579155071",
  "password": "123456"
}
```

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "d916762c-cc87-4b87-bf0e-8dfedd56096b",
    "refresh_token": null
  }
}
```

输入错误的账号密码也能返回错误信息：

```json
{
  "code": 201,
  "message": "用户名或者密码错误",
  "data": null
}
```

****
### 2.2 手机验证码登录

#### 2.2.1 生成验证码

使用手机验证码进行登录，首先就得要生成了验证码才行，将生成的验证码保存在一个地方，然后再比对用户在登录页面输入的验证码，如果它们比对一致则认为登录成功。当然，
验证码要设置过期时间，不能让它一直存在，时间一到就应该失效；并且在一定的时间内同个手机号不能连续生成验证码，因此需要对生成验证码的时间进行计算是否过了过期时间，
只有超过了过期时间才生成新的验证码。

Controller 层：

Controller 层接收前端传递来的手机号并生成验证码，Service 层生成验证码的方法的返回值是 Boolean，通过它来判断是否生成了新的验证码，如果是新的，那就返回成功信息；
反之返回验证码已存在的信息。

```java
@GetMapping("/generatePhoneCode")
@Operation(summary = "生成手机验证码", description = "前端发送请求可以获取到随机手机验证码")
public Result<String> generatePhoneCode(@RequestParam String phone) {
    Boolean result = phoneCodeService.generatePhoneCode(phone);
    if (result) {
        return Result.build("success", ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
    } else {
        return Result.build("fail", ResultCodeEnum.PHONE_CODE_IS_EXISTS.getCode(), ResultCodeEnum.PHONE_CODE_IS_EXISTS.getMessage());
    }
}
```

Service 层：

这里选择将生成的验证码保存进 Redis，因为它可以设置过期时间，并且获取验证码的操作较为频繁，从缓存中拿取可以减轻数据库的压力。这里使用了工具类生成一个随机六位的数字，
并让它拼接上当前的时间戳，用于判断下次该方法被调用时上一次生成的验证码是否过期。不过在生成验证码前肯定要先查找 Redis，从里面获取对应的手机号的验证码，
如果连作为 key 的手机号都没有，那就可以直接生成，如果有，那就要判断它的时间戳和现在调用方法的时间戳是否超过了规定的时间（当然这里的 TTL 和规定时间都是 60s，通常 TTL 应该更长），
如果超过了，那就满足条件，可以生成新的验证码，否则不生成，让用户继续使用旧的。

```java
@Service("phoneCodeService")
public class PhoneCodeServiceImpl implements PhoneCodeService {

    // 配置 log
    private static final Logger log = Logger.getLogger(PhoneCodeServiceImpl.class.getName());

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Boolean generatePhoneCode(String phone) {
        // 先从 Redis 中查找是否有该号码的验证码
        String redisPhoneCode = stringRedisTemplate.opsForValue().get(PhoneConstant.LOGIN_PHONE_CODE_KEY + phone);
        if (StrUtil.isNotEmpty(redisPhoneCode)) {
            String[] split = redisPhoneCode.split("_");
            if (split.length == 2) {
                long time = Long.parseLong(split[1]);
                // 当前时间减去生成验证码的时间，如果小于设置的验证码过期时间，那么就不再生成新的验证码
                if (System.currentTimeMillis() - time < TimeUnit.SECONDS.toMillis(PhoneConstant.LOGIN_PHONE_CODE_TTL)) {
                    log.info("还未过期的手机验证码:" + split[0]);
                    return false;
                }
            }
        }
        // 让生成的验证码携带当前时间戳，用来后续判断是否需要继续生成新的验证码
        String phoneCode = RandomUtil.randomNumbers(6);
        String code = phoneCode + "_" + System.currentTimeMillis();
        // 将验证码存入 Redis 中，并设置过期时间为 60 s
        stringRedisTemplate.opsForValue().set(PhoneConstant.LOGIN_PHONE_CODE_KEY + phone, code,
                PhoneConstant.LOGIN_PHONE_CODE_TTL, TimeUnit.SECONDS);
        log.info("新的手机验证码:" + phoneCode);
        return true;
    }
}
```

测试接口，它的返回值如下，测试成功：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": "success"
}
```

```json
{
  "code": 203,
  "message": "验证码已发送，请勿重复点击获取",
  "data": "fail"
}
```

Redis 中的数据：

```text
640230_1758203021543
```

需要注意的是：本项目使用的 jdk 为 21，而 Lombok 的 @Slf4j 注解处理器 与 Java21 的 javac 内部实现不兼容，也就是说 Lombok 的 @Slf4j + JDK 21 使用时，
可能会产生错误：

```text
java.lang.NoSuchFieldError: Class com.sun.tools.javac.tree.JCTree$JCImport does not have member field 'com.sun.tools.javac.tree.JCTree qualid'
```

解决方法就是将 jdk 版本降低，或者手动声明 Logger，这里就是使用的手动声明 Logger：

```java
// 配置 log
private static final Logger log = Logger.getLogger(PhoneCodeServiceImpl.class.getName());
```

****
### 2.2.2 检验手机验证码

检验手机验证码就是用户在用手机号登录页面输入手机号后并点击发送验证码后，用户根据自己手机上接收到的验证码填写到登录页面，后端就会接收到它，然后对比存入 Redis 中的验证码，
相同则让它通过登录。

Controller 层：

Controller 层接收前端传递来的数据，主要数据就是用户的手机号和该用户输入的验证码，因此需要扩展一下该对象，让它新增两个字段（手机号和输入的验证码）：

```java
@Data
@Schema(description = "用户登录请求参数")
public class UserLoginDto {

    @Schema(description = "用户名")
    private String username ;

    @Schema(description = "密码")
    private String password ;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "手机号验证码")
    private String phoneCode;
}
```

接着就是把该登录对象传递给 Service 层进行判断。

```java
@PostMapping("/loginWithPhoneCode")
@Operation(summary = "用户手机验证码登录", description = "使用户手机号和手机验证码登录系统")
public Result<LoginVo> loginWithCode(@RequestBody UserLoginDto userLoginDto, HttpSession session) {
    Boolean loginCondition = loginService.checkLoginCount(userLoginDto);
    // 进行登录请求
    LoginVo loginVo = loginService.loginWithPhoneCode(userLoginDto, session);
    if (loginVo != null) {
        return Result.build(loginVo, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
    } else {
        return Result.build(null, ResultCodeEnum.VALIDATE_CODE_ERROR.getCode(), ResultCodeEnum.VALIDATE_CODE_ERROR.getMessage());
    }
}
```

Service 层：

这里先查询该用户是否存在数据库中（即是否完成注册），如果不存在那就不需要进行后续的登录操作，直接返回错误信息即可。接着就是根据手机号从 Redis 中查找对应的验证码，
让它和用户输入的验证码进行对比，对比一致后则删除 Redis 中的数据，并生成 session。

```java
@Override
public LoginVo loginWithPhoneCode(UserLoginDto userLoginDto, HttpSession session) {
    String phone = userLoginDto.getPhone();
    String phoneCode = userLoginDto.getPhoneCode();
    // 通过手机号查询是否存在该用户
    UserInfo userInfo = getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getPhone, userLoginDto.getPhone()));
    log.info("当前用户：" + userInfo);
    // 只有该用户存在时（即完成了注册）再进行验证码的检验
    if (userInfo != null) {
        String redisPhoneCode = stringRedisTemplate.opsForValue().get(LoginConstant.LOGIN_PHONE_CODE_KEY + phone);
        if (StrUtil.isNotEmpty(redisPhoneCode)) {
            String[] split = redisPhoneCode.split("_");
            // 用户输入的验证码和存入 Redis 的验证码一致
            if (phoneCode.equals(split[0])) {
                // 删除存在 Redis 中的验证码并生成 session
                stringRedisTemplate.delete(LoginConstant.LOGIN_PHONE_CODE_KEY + phone);
                session.setAttribute("userId", userInfo.getId());
                String token = session.getId();
                LoginVo loginVo = new LoginVo();
                loginVo.setToken(token);
                return loginVo;
            }
        }
    }
    return null;
}
```

****
### 2.3 登录次数限制

为了防止一个用户恶意进行登录操作，应该限制该用户的尝试登录的次数，也就是规定一个时间，然后在这段时间内只允许进行 3 次的登录，超过这个次数直接返回错误信息，
不执行登录的代码，以此达到限制登录的操作。

Controller 层：

这里的逻辑是在 Controller 层增加一个方法用来判断当前用户是否允许登录（规定时间内的尝试登录次数没超过限制），只有允许的才可以往下执行登录的代码逻辑，
否则返回一个错误信息：登录过于频繁。

```java
@PostMapping("/loginWithPhoneCode")
@Operation(summary = "用户手机验证码登录", description = "使用户手机号和手机验证码登录系统")
public Result<LoginVo> loginWithCode(@RequestBody UserLoginDto userLoginDto, HttpSession session) {
    Boolean loginCondition = loginService.checkLoginCount(userLoginDto);
    // 规定时间内登录次数小于限制，可以登录
    if (loginCondition) {
        // 进行登录请求
        LoginVo loginVo = loginService.loginWithPhoneCode(userLoginDto, session);
        if (loginVo != null) {
            return Result.build(loginVo, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
        } else {
            return Result.build(null, ResultCodeEnum.VALIDATE_CODE_ERROR.getCode(), ResultCodeEnum.VALIDATE_CODE_ERROR.getMessage());
        }
    } else { // 规定时间内登录次数小大于限制，拒绝登录，返回登录过于频繁的提示
        return Result.build(null, ResultCodeEnum.LOGIN_TOO_FREQUENTLY.getCode(), ResultCodeEnum.LOGIN_TOO_FREQUENTLY.getMessage());
    }
}
```

Service 层：

该方法需要接收用户的登录信息，因为需要通过这些信息查询数据库中是否存在该用户，只有存在该用户时才进行登录次数的限制，否则也不会进入登录代码。因为当前的登录分为输入账号密码和手机号，
而这些数据都封装在同一个实体类中，因此查询数据库时会把用户名和手机号都作为查询条件，但是以 “或” 的形式，查到数据后就可以往下执行力。把该用户的 id 作为 key 存入 Redis，
利用 Redis 的一个自增语法，一个用户每调用一次 checkLoginCount() 方法就会执行一次自增，当自增到 3 次后，就不允许登录了，当然这里要规定时间，用 TTL 来代替，
第一次进行自增时，也就是从 0 -> 1，此时就给该键值对设置一个过期时间，其余情况不设置。最后让存在 Redis 中的登录次数和规定的登录次数进行大小比对，小于规定次数才返回 true，
让代码执行登录逻辑。

```java
@Override
public Boolean checkLoginCount(UserLoginDto userLoginDto) {
    // 根据用户名或手机号查用户信息
    UserInfo userInfo = getOne(new LambdaQueryWrapper<UserInfo>()
            // 当用户名不为空时作为查询条件
            .eq(StrUtil.isNotEmpty(userLoginDto.getUsername()), UserInfo::getUsername, userLoginDto.getUsername())
            // 当手机号不为空时作为查询条件
            .or(StrUtil.isNotEmpty(userLoginDto.getPhone()),
                    wrapper -> wrapper.eq(UserInfo::getPhone, userLoginDto.getPhone())));
    if (userInfo != null) {
        Long userId = userInfo.getId();
        if (userId != null) {
            String key = LoginConstant.LOGIN_LIMIT_KEY + userId;
            // 第一次 incr 时设置过期时间
            Long loginCount = stringRedisTemplate.opsForValue().increment(key);
            if (loginCount != null && loginCount == 1L) {
                // 第一次访问，设置 TTL
                stringRedisTemplate.expire(key, LoginConstant.LOGIN_COUNT_SURVIVE_TTL, TimeUnit.SECONDS);
            }
            // 小于限制次数，允许继续尝试登录
            return loginCount != null && loginCount <= LoginConstant.LOGIN_COUNT_LIMIT;
        }
    }
    return false;
}
```

通过接口文档进行测试，返回数据：

```json
{
  "code": 204,
  "message": "登录过于频繁，请稍后重试",
  "data": null
}
```

****
### 2.4 登录页面的随机图片验证码

#### 2.4.1 生成随机图片验证码

在登录页面一般都会放一个随机验证码，该验证码是以图片的形式存在的，不过图片中会显示验证码的信息，也就是几个字符串。这个验证码一般是由后端生成并传递给前端的，
因为前端代码是公开的，在浏览器可以通过开发者工具看到当前页面的代码详情，如果由前端生成的话就不够安全了，而且还得把这个验证码传递给后端，由后端进行存储，这反而更麻烦了，
因此选择由后端直接生成并保存在 Redis 中，后续用它和用户输入的验证码进行比对。

Controller 层：

在用户进入登录页面时，前端会发送一个请求用来生成随机图片验证码。而上面记录了，生成的随机验证码需要存入 Redis 中，而存入 Redis 中就需要有 key，但生成随机图片验证码时，
是用户还未进行登录的情况，也就是说不能让用户的 id 作为 key 存入 Redis 中了，只能生成一个随机字符串让它作为 key，最终返回数据时顺便该这个 key 一起返回。
前端拿到了 key 后，在下一次的请求生成验证码时也需要携带这个 key，因为验证码的更新则必须把上一次存入的验证码删掉，只有传入了上一次验证码的 key 才能准确删除，
避免在验证码还未超过 TTL 时的刷新会无限存入 Redis。而返回的数据除了这个 key，当然还需要把整个验证码发送给前端，因此需要封装一个实体类进行数据的返回：

```java
@Data
@Schema(description = "验证码响应结果实体类")
public class ValidateCodeVo {

    @Schema(description = "验证码key")
    private String codeKey ; // 验证码的key

    @Schema(description = "验证码value")
    private String codeValue ; // 图片验证码对应的字符串数据

}
```

```java
@GetMapping("/generateRandomCode")
@Operation(summary = "生成登录页面的随机图片验证码", description = "进入登录页面时会发送一个请求，该请求让后端生成一个随机的图片验证码")
public Result<ValidateCodeVo> generateRandomCode(@RequestParam String randomCodeKey) {
    ValidateCodeVo validateCodeVo = randomCodeService.generateRandomCode(randomCodeKey);
    if (validateCodeVo != null) {
        return Result.build(validateCodeVo, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
    } else {
        return Result.build(null, ResultCodeEnum.DATA_ERROR.getCode(), ResultCodeEnum.DATA_ERROR.getMessage());
    }
}
```

Service 层：

在生成验证码之前需要先进行旧验证码的清除（如果前端传递了正确的 key），然后在使用 UUID 生成随机唯一的字符串作为存入 Redis 的 key。这里使用了 Hutool 来生成图片验证码，
它是一个图片中包含一些字符串，真实需要的就是这些字符串，只不过以图片的形式会更安全，而生成图片验证码后，还可以通过 captcha.getCode() 获取到这个图片中的那些随机字符串，
也就是把它存入 Redis 中。最后将 Redis 的 key 和整个图片验证码封装进对象传递给前端。

```java
@Service("randomCodeService")
public class RandomCodeServiceImpl implements RandomCodeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ValidateCodeVo generateRandomCode(String randomCodeKey) {
        if (randomCodeKey != null) {
            stringRedisTemplate.delete(LoginConstant.RANDOM_CODE_KEY +  randomCodeKey);
        }
        // 生成唯一标识 UUID，用来充当存入 Redis 的 key
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String key = LoginConstant.RANDOM_CODE_KEY + uuid;
        // 生成验证码图片
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(150, 48, 4, 2); // 创建线型验证码对象（图片宽度、图片高度、验证码字符数量、干扰线数量）
        String randomCode = captcha.getCode();// 4 位验证码的值
        String imageBase64 = captcha.getImageBase64(); // 返回图片验证码，base64 编码方式
        stringRedisTemplate.opsForValue().set(key, randomCode, LoginConstant.RANDOM_CODE_TTL, TimeUnit.SECONDS);
        ValidateCodeVo validateCodeVo = new ValidateCodeVo();
        validateCodeVo.setCodeKey(uuid);
        validateCodeVo.setCodeValue("data:image/png;base64," + imageBase64);
        return  validateCodeVo;
    }
}
```

通过接口文档进行测试，成功返回结果：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "codeKey": "8c012ca37f3749a8b0d53158ef56fe51",
    "codeValue": "data:image/png;base64,iVBORw..."
  }
}
```

****
#### 2.4.2 图片验证码的校验

图片验证码是在用户使用账号密码登录时才存在的，因此校验功能是在用户使用账号密码的那个接口中执行的，但该验证码也属于用户在表单中输入的数据，因此越需要把它封装进对象中，
在原有的 UserLoginDto 中新增两个字段，一个用户输入的验证码，一个后端生成验证码时返回的 Redis 的 key：

```java
@Data
@Schema(description = "用户登录请求参数")
public class UserLoginDto {
    // ...
    
    @Schema(description = "用户输入的随机验证码")
    private String randomCode;

    @Schema(description = "生成随机验证码时存入 Redis 中的 key")
    private String codeKey;
}
```

校验逻辑较为简单，直接通过 key 查找 Redis，然后拿用户输入的和存入 Redis 中的对比，对比一致才与奴性执行后续的登录操作：

```java
@Override
public LoginVo login(UserLoginDto userLoginDto, HttpSession session) {
    UserInfo userInfo = getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUsername, userLoginDto.getUsername()));
    if (userInfo != null) {
        // 校验用户输入的验证码和图片验证码是否一致
        String redisRandomCode = stringRedisTemplate.opsForValue().get(LoginConstant.RANDOM_CODE_KEY + userLoginDto.getCodeKey());
        if (StrUtil.isNotEmpty(redisRandomCode) && redisRandomCode.equals(userLoginDto.getRandomCode())) {
            // 删除存入 Redis 中的图片验证码
            stringRedisTemplate.delete(LoginConstant.RANDOM_CODE_KEY + userLoginDto.getCodeKey());
            String password = userLoginDto.getPassword();
            if (password.equals(userInfo.getPassword())) {
                // 账号密码正确，把用户 id 存入 session
                session.setAttribute("userId", userInfo.getId());
                // 获取 sessionId 作为 token
                String token = session.getId();
                LoginVo loginVo = new LoginVo();
                loginVo.setToken(token);
                return loginVo;
            }
        }
    }
    return null;
}
```

****
### 2.5 登录校验

在用户登录后，肯定需要把当前登录的用户信息保存起来，这里使用的是 ThreadLocal，只要当前操作处在同一请求中（线程内），就可以通过 ThreadLocal 获取到当前的用户信息。
这里选择保存的数据是 userId，因为当时存储用户信息到 session 中时，保存的也是 userId。

```java
public class AuthContextUtil {
    // 创建一个 ThreadLocal 对象，用来存储用户 id
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    // 定义存储数据的静态方法
    public static void set(Long userId) {
        threadLocal.set(userId);
    }

    // 定义获取数据的方法
    public static Long get() {
        return threadLocal.get() ;
    }

    // 删除数据的方法
    public static void remove() {
        threadLocal.remove();
    }
}
```

当然，要使用 ThreadLocal 存储用户信息，那就得设置请求拦截，要判断当前的请求是否携带了 session，如果没携带就不使用 ThreadLocal 存储信息，并让其跳转到登录页面，
如果获取到了 session，那就从 session 中取出 userId，然后把它存入 ThreadLocal 中。

这里需要注意的是：不需要手动为 session 更新 TTL。当前使用的是 SpringSession，当用户登录成功后会把 session 存入 Redis 中，默认的 session 的 TTL 为 30min，
可以在配置文件中进行更改，当然 session 是支持滑动过期的，也就是说只要在 session 还未过期前访问了 session，那么它就会自动把 TTL 设置成默认时间（或手动设置的），
而这里的登录拦截器中，在请求执行前就会先访问 session 获取到存在浏览器中的数据，也就是说，这个操作就会更新存在 Redis 中的 session。那么，只要是被设置成需要拦截的请求，
只要访问了这些，就会触发 Spring Session 的滑动更新操作，也就不需要手动获取 sessionId 去 redis 中修改 TTL。

```java
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    // 配置 log
    private static final Logger log = Logger.getLogger(LoginUserInterceptor.class.getName());

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false); // 不创建新 session
        Long userId = session == null ? null : (Long) session.getAttribute("userId");
        if (userId != null) {
            log.info("获取到用户 id：" + userId);
            AuthContextUtil.set(userId);
            return true;
        } else {
            log.log(Level.SEVERE,"未登录，请先完成登录操作！");
            response.sendRedirect("http://192.168.149.101:11000/login.html");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AuthContextUtil.remove();  // 当请求执行完后移除 ThreadLocal 中的数据
    }
}
```

需要注意的是：getSession() 会在 session 不存在时创建 session，也就是说，只要是被拦截到的请求，如果直接调用 getSession() 它就会生成一个 session 保存到 Redis 中，
这是不合理的，因此需要使用 request.getSession(false)，它在没获取到 session 时不会创建新的 session，这样可以保证被拦截的请求不会产生另外的 session。

既然配置了拦截器，那就要把它注入到 Spring 中，然后再设置需要进行拦截的请求路径，当然登录请求的路径是不能拦截的，不然连登录操作都不能进行就会进入死循环。

```java
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private LoginUserInterceptor loginUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor)
                .excludePathPatterns(
                        "/doc.html",            // Knife4j doc 页面
                        "/swagger-ui.html",     // SpringDoc UI 页面
                        "/v3/api-docs/**",      // JSON 文档接口
                        "/swagger-ui/**",       // SpringDoc 静态资源
                        "/webjars/**",          // webjars 静态资源
                        "/auth-server/loginWithPhoneCode",
                        "/auth-server/generatePhoneCode",
                        "/auth-server/login",
                        "/auth-server/generateRandomCode"
                )
                .addPathPatterns("/**");
    }
}
```

当然可以把这些不需要进行拦截的路径写入配置文件中，让这个配置类动态的扫描，通过 @ConfigurationProperties 把配置文件里以 spzx.auth 开头的配置，
自动绑定到 Java 对象的属性上，这里 noAuthUrls 是 List<String>，所以配置文件里的多行 url 会自动转换成列表，不过需要配合 @EnableConfigurationProperties() 使用，
Spring 会在启动时扫描配置文件，扫到了 @EnableConfigurationProperties 标注的那个类，就会加载那个类，然后把对应前缀的值注入到 Bean 的属性里。

```java
@Data
@ConfigurationProperties(prefix = "spzx.auth")
public class AuthUrlProperties {
    private List<String> noAuthUrls;
}
```

```yaml
spzx:
  auth:
    noAuthUrls:
      - /doc.html            # Knife4j doc 页面
      - /swagger-ui.html     # SpringDoc UI 页面
      - /v3/api-docs/**      # JSON 文档接口
      - /swagger-ui/**       # SpringDoc 静态资源
      - /webjars/**          # webjars 静态资源
      - /auth-server/loginWithPhoneCode
      - /auth-server/generatePhoneCode
      - /auth-server/login
      - /auth-server/generateRandomCode
```

```java
@Configuration
@EnableConfigurationProperties(value = {AuthUrlProperties.class})
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private LoginUserInterceptor loginUserInterceptor;
    @Autowired
    private AuthUrlProperties authUrlProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor)
                .excludePathPatterns(authUrlProperties.getNoAuthUrls())
                .addPathPatterns("/**");
    }
}
```

以上配置都是写在 spzx-common 模块里的，而本项目是多模块分开写的，也就是说，这些 Bean 想要在项目加载时就被扫描到并加载到 Spring 中，就需要能被 Spring 扫描到，
而 spzx-common 本身是个不带启动类的配置模块，要让它被 Spring 扫描到，就需要添加启动类，有了启动类就可以在项目启动时自动让 Spring 扫描到这些 Bean（默认扫描启动类所在包及其子包），
但是在 spzx-common 中添加启动类是不合适的，因此只能从需要使用它的那些模块中入手，在它们的启动类上扫描到 spzx-common 中的 Bean，可是它们不在一个模块，
怎么能扫描到？因此需要引入 spzx-common 作为这些模块的依赖，当启动类上添加了 @SpringBootApplication(scanBasePackages = {"com.cell"})，
它就能扫描到了。因为 Maven 会把 spzx-common 编译后的 classes 加入到 auth-server 的 classpath，当规定的扫描包为 com.cell 时，
它就会扫描 com.cell 下的所有子模块，包括 com.cell.spzx.common 包下的 @Configuration、@Component 等 Bean。当然这是包名一致的情况下才可以这样写，
确保 common 和 auth-server 都能被扫描到，如果 common 包的包名为 com.lasdfjlk.aslf.common，那么就得单独写上这个包名才能被扫描到。

```text
spzx-common // 公共配置模块，不带启动类
    └─ com.cell.spzx.common
        ├─ config
        │   └─ WebMvcConfiguration.java
        └─ interceptor
            └─ LoginUserInterceptor.java

auth-server // 实际启动服务模块
    └─ com.cell.spzx.auth_server
        └─ AuthServerApplication.java
```

1. 启动类所在模块：spzx-auth-server
2. 默认扫描包：com.cell.auth（启动类所在包及其子包）
3. 扩展扫描：scanBasePackages = {"com.cell"} -> 扫描整个com.cell包
4. 发现：com.cell.common.properties.AuthUrlProperties

```text
spzx-auth-server 依赖 spzx-common
编译时：common 模块的 class 文件会被打包到 auth-server 中
运行时：JVM 能看到 common 模块中的所有类
扫描时：Spring 能扫描到 com.cell 包下的所有类
```

经过测试，发现在 http://localhost:11000/doc.html 接口文档页面，只要点击了任意一个接口（未点击测试按钮），它在页面会展示该接口的相关信息，但此时后端也会打印 “未登录”，
也就是说这个查看接口详情被拦截器拦截到了，很奇怪，明明上面已经配置好了 swagger 相关的页面都进行放行操作。于是在拦截器中添加打印被拦截的请求的代码：

```java
String requestUri = request.getRequestURI();
log.info("拦截到请求: " + requestUri);
```

再经过测试发现，控制台打印的 “拦截到请求：/favicon.ico”，也就是说在接口文档页面会自动把图标也作为请求进行发送，那么只要在放行请求中把这个路径也添加进去即可。
但是后续又出现类似的请求，只不过这次打印的拦截请求为 /error，因为当浏览器的请求被拦截到后，会进行 session 的校验，如果校验不通过就会跳转到登录页面，
而当前登录页面没有完成，它就会触发 Spring 内部的错误处理，导致发送一个 /error 请求。

当然，不同网页之间的请求可能存在跨域问题，因此需要添加一个配置文件，让本项目的所有请求都允许进行跨域访问：

```java
@Configuration
@EnableConfigurationProperties(value = {AuthUrlProperties.class})
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")              // 添加路径规则
                .allowCredentials(true)         // 是否允许在跨域的情况下传递 Cookie
                .allowedOriginPatterns("*")     // 允许请求来源的域规则
                .allowedMethods("*")
                .allowedHeaders("*") ;          // 允许所有的请求头
    }
}
```

****
### 2.8 用户注册

注册功能就是，用户在前端页面会有个表单，里面由用户填写注册的账号信息，一般包括用户名、密码、手机号等，由后端接收到这些数据后会先查询数据库该账号是否允许注册，
即是否存在相同的用户名或手机号的用户，满足条件了才允许执行注册，也就是在数据库中插入一条用户信息。

Controller 层：

Controller 层接收到前端页面传递的用户填写的注册账号信息，因此需要有个传输对象来接受：

```java
@Data
@Schema(description="注册对象")
public class UserRegisterDto {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "电话号码")
    private String phone;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "手机验证码")
    private String code ;
}
```

该对象内部封装了一些字段：用户名、密码、手机号、昵称和手机验证码，这个手机验证码和登录时的手机验证码的功能一致，也是起到一个校验的功能，因此在进行注册时还需要对验证码进行判断是否一致。

```java
@RestController
@RequestMapping("/auth-server/register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PutMapping("/register")
    @Operation(summary = "用户注册", description = "输入信息执行注册")
    public Result<Boolean> register(@RequestBody UserRegisterDto UserRegisterDto) {
        return registerService.register(UserRegisterDto);
    }
}
```

Service 层：

用来进行注册操作的实体类肯定不是 UserRegisterDto，因为注册实际上是个插入数据到数据库的操作，而用户的信息统一保存在 user_info 表中，也就是说，要用对应的实体类来执行插入操作，
并且注册时填写的数据只是一小部分，还有很多数据需要用户在登录后进行手动更新，因此插入时使用的实体类是 UserInfo，它是表 user_info 的对应实体类。
不过 UserRegisterDto 对象中有些字段是与 UserInfo 一样的，所以可以直接使用 BeanUtils 进行拷贝，至于不一样的字段，这里只有一个手机验证码，它不需要存入数据库，
因此不用考虑赋值给 UserInfo 的情况。

在执行注册前，需要对该用户填写的账号的用户名和手机进行唯一性的判断，也就是查找数据库中是否有一样的用户名和手机号，如果有就返回对应的错误信息，这样前端获取到后也可以在页面显示。
成功通过唯一性校验后再对手机验证码的一致性进行判断，当条件全部满足时就可以执行插入操作了，把用户提交的数据保存进表 user_info 中。

```java
@Override
public Result<Boolean> register(UserRegisterDto userRegisterDto) {
    UserInfo userInfo = new UserInfo();
    BeanUtils.copyProperties(userRegisterDto, userInfo);

    // 查询是否存在相同用户名或手机号的用户
    LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
    if (StrUtil.isNotEmpty(userInfo.getUsername())) {
        wrapper.eq(UserInfo::getUsername, userInfo.getUsername());
    }
    if (StrUtil.isNotEmpty(userInfo.getPhone())) {
        wrapper.or(w -> w.eq(UserInfo::getPhone, userInfo.getPhone()));
    }

    UserInfo checkExist = getOne(wrapper);
    if (checkExist != null) {
        return Result.build(false, ResultCodeEnum.USER_ALREADY_EXIST.getCode(), ResultCodeEnum.USER_ALREADY_EXIST.getMessage());
    }

    // 验证手机验证码
    String value = stringRedisTemplate.opsForValue().get(LoginConstant.LOGIN_PHONE_CODE_KEY + userInfo.getPhone());
    if (StrUtil.isEmpty(value)) {
        return Result.build(false, ResultCodeEnum.VALIDATE_CODE_ERROR.getCode(), ResultCodeEnum.VALIDATE_CODE_ERROR.getMessage());
    }

    String code = value.split("_")[0];
    if (StrUtil.isEmpty(code) || !code.equals(userRegisterDto.getCode())) {
        return Result.build(false, ResultCodeEnum.VALIDATE_CODE_ERROR.getCode(), ResultCodeEnum.VALIDATE_CODE_ERROR.getMessage());
    }

    // 满足条件，进行注册
    save(userInfo);
    stringRedisTemplate.delete(LoginConstant.LOGIN_PHONE_CODE_KEY + userInfo.getPhone());
    return Result.build(true, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
}
```

****
### 2.7 获取用户信息

当登录成功以后，此时前端会自动调用后端接口来获取登录成功以后的用户信息，然后在首页面展示。

Controller 层：

因为获取当前登录用户的信息是在用户登录成功后再执行的操作，也就是说，它可以通过获取到当前用户的 session 来查询用户信息，不需要由前端进行判断后传递用户 id 之类的数据，
因为前端在发送请求时，请求头中会自动携带 session，只要能获取到这个 session 就可以获取到用户信息。

```java
@RestController
@RequestMapping("/auth-server/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/userInfo")
    @Operation(summary = "获取用户信息", description = "通过 session 拿到用户 id 再查询数据库")
        public Result<UserInfo> getUserInfo(HttpServletRequest request) {
        UserInfo userInfo = userService.getUserInfo(request);
        if (userInfo != null) {
            return Result.build(userInfo, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
        } else {
            return Result.build(null, ResultCodeEnum.LOGIN_AUTH.getCode(), ResultCodeEnum.LOGIN_AUTH.getMessage());
        }
    }
}
```

Service 层：

这里直接通过 HttpServletRequest 获取到 session，然后取出当时用户登录时存入 session 的 userId，接着就可以通过它查询数据库了。

```java
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserInfo> implements UserService {
    @Override
    public UserInfo getUserInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                return getOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getId, userId));
            }
        }
        return null;
    }
}
```

****
### 2.8 用户退出

用户退出就是清除保存在 Redis 中的 session 数据，这样在访问页面功能时就会被自动拦截到，因为 session 已经被清除了。

Controller 层：

Controller 层要接收前端传递的 HttpServletRequest，因为 HttpSession 是绑定到当前 HTTP 请求的，每个用户访问后台接口时，浏览器会携带 Cookie，
后端通过 HttpServletRequest.getSession() 才能获取到对应用户的 session，所以，如果想操作当前用户的 session（比如退出登录），
必须知道哪个请求对应哪个 session，这就需要 HttpServletRequest。

```java
@GetMapping("/loginOut")
@Operation(summary = "用户退出登录", description = "删除用户登录时生成的 session")
public Result loginOut(HttpServletRequest request) {
    loginService.loginOut(request);
    return Result.build(null , ResultCodeEnum.SUCCESS) ;
}
```

Service 层：

Service 层先通过 Controller 层传递来的 HttpServletRequest 获取到 session，这里不能在未获取到 session 时创建新的，所以要加上 false，
获取到后就执行 session.invalidate() 让 session 失效，Spring Session 会自动删除 Redis 的 session 记录，清理所有 session 属性，包括存入的 userId 等。

```java
@Override
public void loginOut(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
        log.info("用户退出，sessionId：" +  session.getId());
        session.invalidate(); // 触发 SpringSession 删除 Redis 中的记录
    }
}
```

当然也可以手动获取 sessionId 去 Redis 中进行删除操作，但是当启用了 Spring Session Redis 之后，每个 session 在 Redis 中并不是简单的一条键值对，
而是一个完整的数据结构，它的 key 为 spring:session:sessions:e0029048-13e3-48d1-995e-f291e6f1f80b，value 为：

session 创建时间戳：

```json
{
    "fields": [
        {
            "value": 1758334983383
        }
    ],
    "annotations": [],
    "className": "java.lang.Long",
    "serialVersionUid": 4290774380558885855
}
```

session TTL：

```json
{
    "fields": [
        {
            "value": 1800
        }
    ],
    "annotations": [],
    "className": "java.lang.Integer",
    "serialVersionUid": 1360826667806852920
}
```

session 中存储的用户 id：

```json
{
    "fields": [
        {
            "value": 33
        }
    ],
    "annotations": [],
    "className": "java.lang.Long",
    "serialVersionUid": 4290774380558885855
}
```

上次访问时间戳：

```json
{
    "fields": [
        {
            "value": 1758334983383
        }
    ],
    "annotations": [],
    "className": "java.lang.Long",
    "serialVersionUid": 4290774380558885855
}
```

Spring Session 默认会把 HTTP session 里的所有属性都序列化存储，每个 session 属性（比如 userId、登录时间、验证码、过期时间等）都会单独作为一个对象存入 Redis。
如果进行手动删除可能导致这些属性删除不干净，让 SpringSession 仍认为用户处于登录状态，所以还是推荐使用官方的 API 进行 session 的清除操作。

****
### 2.9 获取登录用户的 IP

在 Web 应用程序中，可以通过 HttpServletRequest 对象来获取调用者的 IP 地址，HttpServletRequest 对象代表客户端的请求，其中包含了客户端的 IP 地址：

```java
@GetMapping("/ip")
@Operation(summary = "获取用户 ip")
public String getCallerIp(HttpServletRequest request) {
    String ip = request.getRemoteAddr();
    return ip;
}
```

但这个方法获取 IP 的前提是客户端直接访问后端服务，如果项目没有经过 Nginx、负载均衡、反向代理，那么它返回的确实就是用户的真实 IP，但真是项目中肯定不是让使用者直接访问后端服务，
中间肯定会经过各种中间件的处理，例如：

```text
用户 -> 运营商 -> CDN -> Nginx/负载均衡 -> 网关 -> 后端服务
```

这样通过 request.getRemoteAddr() 拿到的 ip 可能是代理服务的 IP，比如 Nginx 的，而不是用户的真实 IP。不过代理服务会在 HTTP Header 里加上真实的客户端 IP，比如：

- X-Forwarded-For：常见的头，可能有多个 IP（经过多个代理时，每一层代理都会追加 IP），一般取第一个就是用户的真实 IP。 
- Proxy-Client-IP、WL-Proxy-Client-IP：早期 WebLogic、Apache 一些插件写入的头。 
- HTTP_CLIENT_IP、HTTP_X_FORWARDED_FOR：部分代理或旧系统写的。 
- 最后才是通过 request.getRemoteAddr() 获取。

所以，为了保证在各种代理或部署场景下都能拿到真实 IP，就需要写大堆兜底逻辑来获取真实的 IP：

```java
/**
 * 获取客户端真实IP地址
 */
public static String getIpAddress(HttpServletRequest request) {
    if (request == null) {
        return null;
    }
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
        // X-Forwarded-For 可能有多个IP，格式：client, proxy1, proxy2，取第一个才是真实 IP
        int index = ip.indexOf(',');
        if (index != -1) {
            return ip.substring(0, index).trim();
        } else {
            return ip.trim();
        }
    }

    ip = request.getHeader("Proxy-Client-IP");
    // 先判断这个 IP 是否有效
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) return ip;

    ip = request.getHeader("WL-Proxy-Client-IP");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) return ip;

    ip = request.getHeader("HTTP_CLIENT_IP");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) return ip;

    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) return ip;

    // 最后执行
    ip = request.getRemoteAddr();
    return ip;
}
```

在返回 IP 前需要对从请求头中取出的 IP 判断其是否有效，因为很多时候从 request.getHeader("X-Forwarded-For") 这些地方取出来的值可能是：

- null：表示根本没有设置这个头
- ""（空字符串）：有的代理服务器会加一个空的 header
- "unknown"：某些代理或中间件会返回这个字符串来代表“未知 IP”

而这些情况都不能直接当成真实 IP 来用，所以要先过滤掉，只有通过了这几个条件，才说明这个 IP 有可能是真实的，可以使用。在用户登录时就可以加上这段逻辑，
用来记录用户登录的 IP 是什么。

在用户登录的代码中更新数据库，将当前登录的 IP 更新到数据库：

```java
// 记录当前登录用户的 IP
String ipAddress = IpUtil.getIpAddress(request);
// 更新数据库中的 user_info 表，记录登录 IP
userInfo.setLastLoginIp(ipAddress);
updateById(userInfo);
```

既然能够获取到用户的登录 IP 了，那就可以对 IP 地址进行登录操作的次数限制了，这个限制操作和之前的逻辑一样，设置一个自增，第一次就设置过期时间，后续每次访问都自增，
只不过这里要分情况了，一个是登录用户的用户名（或手机号），一个是登录用户的 IP 地址，也就是要使用两个 key。这里不再查询数据库使用用户的 id 作为 key 了，
感觉没什么必要，只是一个限制登录次数的逻辑还查询数据库的话性能就不行了，因此直接使用前端传递的用户名或手机号作为 key（取决于使用的哪种登录方式）。

```java
@Override
public Boolean checkLoginCount(UserLoginDto userLoginDto, HttpServletRequest request) {
    // 根据用户名或手机号查用户信息
    String keyForUser = LoginConstant.LOGIN_LIMIT_KEY + (StrUtil.isEmpty(userLoginDto.getUsername()) ? userLoginDto.getPhone() : userLoginDto.getUsername());
    Boolean resultForUser = checkLoginCount(keyForUser);
    Boolean resultForIp = false;
    String ipAddress = IpUtil.getIpAddress(request);
    if (StrUtil.isNotEmpty(ipAddress)) {
        String keyForUserIp = LoginConstant.LOGIN_LIMIT_IP_KEY + ipAddress;
        resultForIp = checkLoginCount(keyForUserIp);
    }
    return resultForUser && resultForIp;
}
```

```java
public Boolean checkLoginCount(String key) {
    Long loginCount = stringRedisTemplate.opsForValue().increment(key);
    if (loginCount != null && loginCount == 1) {
        stringRedisTemplate.expire(key, LoginConstant.LOGIN_COUNT_SURVIVE_TTL, TimeUnit.SECONDS);
    }
    return loginCount != null && loginCount <= LoginConstant.LOGIN_COUNT_LIMIT;
}
```

****
### 2.10 session 的自动创建

Session 的自动创建通常发生在以下情况之一：

- 首次调用 request.getSession() 或 request.getSession(true)
- 应用程序首次尝试向 session 中存储属性 
- 通过 JSESSIONID cookie 识别到已有 session

在 Spring MVC 中，只要你在 Controller 方法中声明了 HttpSession 或 HttpServletRequest，就可能触发自动创建：

```java
@PostMapping("/login")
public String login(UserLoginDto dto, HttpSession session) {
    // session 会在这里自动创建（如果之前没有的话）
}
```

SpringMVC 在程序运行时会解析 Controller 方法的参数，如果参数是 HttpSession，那默认就会调用 request.getSession()，
而 getSession() 默认参数是 true，所以会创建 session。如果使用 HttpServletRequest，并且手动调用 request.getSession()，也会自动创建 session，
但仅声明 HttpServletRequest 并不会自动创建 session，只有你主动调用 getSession() 才会。

也就是说，之前的那种写法，会导致每次调用都自动生成一个 session 保存到 Redis，只不过这个 session 没有保存 userId，因此在 Redis 中总能看到 session 但在拦截器中却始终拦截到这条请求。
所以以后使用 HttpServletRequest 来手动创建 session，避免和自动创建的 session 产生混淆。

****
# 三、权限管理

在后台管理系统中，权限管理是指为了保证系统操作的安全性和可控性。对用户的操作权限进行限制和管理，简单的来说就是某一个用户可以使用系统的哪些功能，比如：
管理员可以使用后台管理系统中的所有功能，普通业务人员只能使用系统中的一部分的功能。

一般来说，权限管理包括以下几个方面：

1. 用户管理：通过对用户进行账号、密码、角色等信息的管理。 
2. 角色管理：将多个用户分组，并根据所属角色的权限区分用户的访问权限。 
3. 菜单管理：对系统的菜单进行管理，根据用户或角色的权限动态生成可访问的菜单列表。 
4. 日志管理：记录系统的操作日志，方便用户或管理员查看系统运行情况，以及对不当操作进行追踪和处理。

****
## 1. 角色管理

### 1.1 新增角色

新增角色就是在 sys_role 中新增一条数据，可以直接利用 MybatisPlus 也可以使用 Mybatis，前端一般都是给一个表单页面，在表单中输入要新增的角色的信息，后端接收到后，
执行插入操作即可。

Controller 层：

后端接收到前端表单提交的数据，通常都是要用一个实体类进行接收的，而这个插入操作一般不涉及其他的一些复杂修改操作，因此直接用数据库中 sys_role 表对应的实体类进行接收即可：

```java
@Data
@Schema(description = "角色实体类")
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "描述")
    private String description;

}
```

```java
@PutMapping("/addRole")
@Operation(summary = "新增角色", description = "在 sys_role 表中插入一条数据")
public Result addSysRole(@RequestBody SysRole sysRole) {
    roleManageService.addSysRole(sysRole);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

Service 层:

```java
@Override
public void addSysRole(SysRole sysRole) {
    save(sysRole);
}
```

或者使用普通的 Mybatis 插入数据：

```xml
<insert id="saveSysRole">
    insert into sys_role (
    id,
    role_name,
    role_code,
    description
    ) values (
    #{roleName},
    #{roleCode},
    #{description}
    )
</insert>
```

通过接口文档进行测试：

```json
{
  "id": 0,
  "createTime": "",
  "updateTime": "",
  "isDeleted": 0,
  "roleName": "普通用户",
  "roleCode": "user",
  "description": ""
}
```

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

****
### 1.2 查询角色

#### 1.2.1 使用 PageHelper

查询操作一般都是用的分页查询，可以使用 MyBatis 的分页查询，该方法需要引入 Mybatis 的分页查询的依赖 PageHelper：

```xml
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.4.6</version>
</dependency>
```

Controller 层：

查询操作前端一般会输入一些查询条件，而这里的查询角色，则是可以输入角色名或者角色编码进行查询，而除了这些条件，后端还需要接收前端传递的分页查询条件，例如查询页码、
每页的最大记录数，因此这些都需要用一个实体类进行接收：

```java
@Data
@Schema(description = "查询系统角色请求参数")
public class RoleQueryDto {
    @Schema(description = "查询页码")
    private Integer page;

    @Schema(description = "每页数据条数")
    private Integer size;

    @Schema(description = "角色名")
    private String roleName;

    @Schema(description = "角色编码")
    private String roleCode;

    public Integer getPage() {
        return (page == null || page <= 0) ? 1 : page;
    }

    public Integer getSize() {
        return (size == null || size <= 0) ? 10 : size;
    }
}
```

为了防止前端传递查询条件时没有输入页码和记录条数，所以在后端需要给它们设置一下默认值，这里设置的是第一页和每页 10 条记录。

当然，后端查询出数据后不建议直接把 SysRole 对象集合直接返回给前端，如果直接返回 List<SysRole>，前端无法判断总条数，也就无法渲染分页控件。
前端还需要判断满足条件的总共有多少条记录，可以分为多少页等信息，因此返回的数据也需要用一个实体类进行封装：

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    private long total;
    private long pages;
    private List<T> records;
}
```

这个实体类封装了满足条件的总共条数，可以分为多少页以及从数据库中查询出满足条件的数据集合。

```java
@PostMapping("/listRoleByPageHelper")
@Operation(summary = "用 PageHelper 查询所有角色", description = "分页查询所有角色")
public Result listSysRoleByPageHelper(@RequestBody RoleQueryDto  roleQueryDto) {
    PageResult<SysRole> sysRoleByPage = roleManageService.getSysRoleByPageHelper(roleQueryDto);
    return Result.build(sysRoleByPage, ResultCodeEnum.SUCCESS);
}
```

Service 层：

PageHelper 的使用就三步：

1. 开启分页，PageHelper 会拦截后续的数据库查询，为 sql 语句自动添加分页参数，例如 LIMIT offset, size
2. 执行查询，调用 Mapper 方法执行查询，PageHelper 会自动对查询结果进行分页。
3. 将查到的结果进行封装，查询结果是一个 List<SysRole>，用 PageInfo 对象封装
   - sysRolePageInfo.getList()：当前页数据 
   - sysRolePageInfo.getTotal()：满足条件的总记录数 
   - sysRolePageInfo.getPages()：总页数

```java
@Override
public PageResult<SysRole> getSysRoleByPageHelper(RoleQueryDto roleQueryDto) {
    // 开启分页
    PageHelper.startPage(roleQueryDto.getPage(), roleQueryDto.getSize());

    // 执行查询
    List<SysRole> sysRoleList = roleManageMapper.selectAll(roleQueryDto.getRoleName(), roleQueryDto.getRoleCode());

    // 封装结果
    PageInfo<SysRole> sysRolePageInfo = new PageInfo<>(sysRoleList);
    PageResult<SysRole> sysRolePageResult = new PageResult<>();
    sysRolePageResult.setRecords(sysRolePageInfo.getList());
    sysRolePageResult.setTotal(sysRolePageInfo.getTotal());
    sysRolePageResult.setPages(sysRolePageInfo.getPages());
    
    return sysRolePageResult;
}
```

Mapper 层：

```java
public interface RoleManageMapper extends BaseMapper<SysRole>{
    List<SysRole> selectAll(@Param("roleName") String roleName, @Param("roleCode") String roleCode);
}
```

```xml
<mapper namespace="com.cell.spzx.role_manage.mapper.RoleManageMapper">

    <!-- MyBatis 映射结果集的配置，用来告诉 MyBatis 数据库查询结果该如何映射到 Java 对象 -->
    <resultMap type="com.cell.model.entity.system.SysRole" id="sysRoleMap">
        <result property="id" column="id"/>
        <result property="roleName" column="role_name"/>
        <result property="roleCode" column="role_code"/>
        <result property="description" column="description"/>
        <result property="createTime" column="create_time"/>
        <result property="updateTime" column="update_time"/>
        <result property="isDeleted" column="is_deleted"/>
    </resultMap>

    <select id="selectAll" resultMap="sysRoleMap">
        SELECT id, role_name, role_code, description, create_time, update_time, is_deleted
        FROM sys_role
        <where>
            <if test="roleName != null and roleName != ''">
                AND role_name LIKE CONCAT('%', #{roleName}, '%')
            </if>
            <if test="roleCode != null and roleCode != ''">
                AND role_code LIKE CONCAT('%', #{roleCode}, '%')
            </if>
            AND is_deleted = 0
        </where>
    </select>

</mapper>
```

当然，查询出的时间信息需要设置正确的时区：

```yaml
spring:
  jackson:
    time-zone: Asia/Shanghai # 时区
```

****
#### 1.2.2 使用 MyBatisPlus 的分页插件

在 SpringBoot 项目中，想要使用 MyBatisPlus 的分页功能，就必须注册分页拦截器 PaginationInnerInterceptor。

```java
@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        pagination.setOverflow(false); // 溢出页的处理：true/false，true 表示请求溢出页时返回第一页数据；false 表示返回空数据
        pagination.setMaxLimit(500L); // 单页最大条数（-1 不限制）
        // 添加分页拦截器
        interceptor.addInnerInterceptor(pagination);
        return interceptor;
    }
}
```

Controller 层：

```java
@PostMapping("/listRole")
@Operation(summary = "查询所有角色", description = "分页查询所有角色")
public Result listSysRole(@RequestBody RoleQueryDto  roleQueryDto) {
    PageResult<SysRole> sysRoleByPage = roleManageService.getSysRoleByPage(roleQueryDto);
    return Result.build(sysRoleByPage, ResultCodeEnum.SUCCESS);
}
```

Service 层：

MaBatisPlus 的分页查询其实和 MyBatis 的类似：

1. 构造查询条件
2. 构造分页对象，这一步与 MyBatis 的开启分页类似，也是传递查询页码和每页数据条数
3. 封装结果，将分页对象和查询条件一起传递给 MyBatisPlus 自带的 page() 方法，内部会自动执行查询当前页数据 SQL（带 LIMIT）与查询总记录数 SQL（COUNT）

```java
@Override
public PageResult<SysRole> getSysRoleByPage(RoleQueryDto roleQueryDto) {
    LambdaQueryWrapper<SysRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
    // 多个条件默认就是 AND 关系
    if (roleQueryDto.getRoleName() != null) {
        lambdaQueryWrapper.like(SysRole::getRoleName, roleQueryDto.getRoleName());
    }
    if (roleQueryDto.getRoleCode() != null) {
        lambdaQueryWrapper.like(SysRole::getRoleCode, roleQueryDto.getRoleCode());
    }
    // 构造分页对象
    Page<SysRole> page = new Page<>(roleQueryDto.getPage(), roleQueryDto.getSize());
    // 查询后的结果
    Page<SysRole> sysRolePage = page(page, lambdaQueryWrapper);
    return new PageResult<SysRole>(sysRolePage.getPages(), sysRolePage.getTotal(), sysRolePage.getRecords());
}
```

通过接口文档进行测试：

```json
{
  "page": 0,
  "size": 0,
  "roleName": "",
  "roleCode": ""
}
```

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 5,
    "pages": 1,
    "records": [
      {
        "id": 2,
        "createTime": "2023-09-03 13:23:41",
        "updateTime": "2023-09-03 13:23:59",
        "isDeleted": 0,
        "roleName": "test02",
        "roleCode": "test02",
        "description": "test02"
      },
      {
        "id": 9,
        "createTime": "2023-05-04 02:36:06",
        "updateTime": "2023-06-02 01:03:31",
        "isDeleted": 0,
        "roleName": "平台管理员",
        "roleCode": "ptgly",
        "description": "平台管理员"
      },
      {
        "id": 10,
        "createTime": "2023-05-04 02:36:22",
        "updateTime": "2023-07-18 00:40:56",
        "isDeleted": 0,
        "roleName": "用户管理员",
        "roleCode": "yhgly",
        "description": "用户管理员"
      },
      {
        "id": 36,
        "createTime": "2023-09-03 15:23:04",
        "updateTime": "2023-09-03 15:23:04",
        "isDeleted": 0,
        "roleName": "销售人员",
        "roleCode": "销售",
        "description": null
      },
      {
        "id": 37,
        "createTime": "2023-09-03 15:24:26",
        "updateTime": "2023-09-04 02:04:17",
        "isDeleted": 0,
        "roleName": "测试人员",
        "roleCode": "test",
        "description": null
      }
    ]
  }
}
```

****
### 1.3 删除角色

#### 1.3.1 使用 MyBatis 进行删除

在前端，操作者可以点击查询出的角色的删除按钮进行单个删除，也可以选中多个角色后点击批量删除，这两者的区别就是一个传递的是单个 roleId，一个传递的是 roleId 集合，
但这两者可以写成一个方法，只要删除条件为 in(roleIds) 即可，不管是删一个还是多个，都能正常执行。

Controller 层：

```java
@DeleteMapping("/deleteRoleByMB")
@Operation(summary = "使用 MyBatis 删除角色", description = "可以选择删除单个数据或批量删除")
public Result deleteSysRoleByMB(@RequestBody List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
        return Result.build(null, ResultCodeEnum.DATA_ERROR);
    }
    roleManageService.deleteSysRoleByMB(ids);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

Service 层：

```java
@Override
public void deleteSysRoleByMB(List<Long> ids) {
    roleManageMapper.deleteSysRoleByMB(ids);
}
```

Mapper 层：

```java
void deleteSysRoleByMB(@Param("ids") List<Long> ids);
```

```xml
<delete id="deleteSysRoleByMB">
    DELETE FROM sys_role WHERE id in
    <foreach collection="list" item="id" open="(" separator="," close=")">
        #{id}
    </foreach>
</delete>
```

不能使用 WHERE role_id in (#{ids})，这样 MyBatis 会将其作为一个整体字符串处理（WHERE role_id in ('[1,2,3]')），虽然可以使用 ${} 进行字符串拼接，
但这种操作是不安全的（sql 注入风险），因此还是推荐使用 `<foreach>` 标签。不过本项目采取的是逻辑删除，所以真实的删除操作实际为修改操作：

```xml
<update id="deleteSysRoleByMB" parameterType="java.util.List">
    update sys_role set is_deleted = 1 where id in
    <foreach collection="ids" item="id" open="(" separator="," close=")">
        #{id}
    </foreach>
</update> 
```

****
#### 1.3.2 使用 MyBatisPlus 进行删除

Controller 层：

```java
@DeleteMapping("/deleteRole")
@Operation(summary = "删除角色", description = "可以选择删除单个数据或批量删除")
public Result deleteSysRole(@RequestBody List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
        return Result.build(null, ResultCodeEnum.DATA_ERROR);
    }
    roleManageService.deleteSysRole(ids);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

Service 层：

Service 层直接调用 MyBatisPlus 自带的批量删除方法，因为在逻辑删除字段上使用了 @TableLogic，底层会拼接 UPDATE ... WHERE id IN (...)

```java
@TableLogic
@Schema(description = "是否删除")
private Integer isDeleted;
```

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void deleteSysRole(List<Long> ids) {
    removeBatchByIds(ids);
}
```

****
### 1.4 修改角色

#### 1.4.1 使用 MyBatis 修改

```java
@PutMapping("/updateSysRoleByMB")
@Operation(summary = "使用 MyBatis 修改角色", description = "根据传递的数据进行选择性修改")
public Result updateSysRoleByMB(@RequestBody SysRole sysRole) {
    roleManageService.updateSysRoleByMB(sysRole);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

```java
@Override
public void updateSysRoleByMB(SysRole sysRole) {
    roleManageMapper.updateSysRoleByMB(sysRole);
}
```

Mapper 层：

```java
void updateSysRoleByMB(SysRole sysRole);
```

```xml
<update id="updateSysRoleByMB" parameterType="com.cell.model.entity.system.SysRole">
    update sys_role
    <set>
        <if test="roleName != null">role_name = #{roleName},</if>
        <if test="roleCode != null">role_code = #{roleCode},</if>
        <if test="description != null">description = #{description},</if>
    </set>
    where id = #{id}
</update>
```

****
#### 1.4.2 使用 MyBatisPlus 修改

在前端点击某个角色的修改按钮时会弹出一个表格，该表中会回显该角色的所有相关信息，因此，这里会涉及到一个查询操作：

```java
@GetMapping("/selectById/{id}")
@Operation(summary = "根据 Id 查询角色", description = "修改角色信息时需要回显原有的数据，也就是一次查询操作")
public Result selectById(@PathVariable Long id) {
    SysRole sysRole = roleManageService.selectById(id);
    return Result.build(sysRole, ResultCodeEnum.SUCCESS);
}

@Override
public SysRole selectById(Long id) {
    return getById(id);
}
```

Controller 层：

```java
@PutMapping("/updateSysRole")
@Operation(summary = "修改角色", description = "根据传递的数据进行选择性修改")
public Result updateSysRole(@RequestBody SysRole sysRole) {
    roleManageService.updateSysRole(sysRole);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

Service 层：

注意：这里不能直接调用 MyBatisPlus 的 updateById(Object obj) 方法，因为这个方法它底层会自动根据实体类字段生成 UPDATE 语句，也就是说，如果这里传入 SysRole 对象，
那么就会生成：

```sql
UPDATE sys_role
SET role_name = ?, role_code = ?, description = ?, update_time = ?, is_deleted = ?
WHERE id = ?
```

也就是说，它不会进行非空的判断，所以得使用普通的 update() 方法，传入手动构造的更新语句条件。

```java
@Override
public void updateSysRole(SysRole sysRole) {
    LambdaUpdateWrapper<SysRole> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    lambdaUpdateWrapper.eq(SysRole::getId, sysRole.getId());
    if (sysRole.getRoleName() != null && !sysRole.getRoleName().isEmpty()) {
        lambdaUpdateWrapper.set(SysRole::getRoleName, sysRole.getRoleName());
    }
    if (sysRole.getRoleCode() != null && !sysRole.getRoleCode().isEmpty()) {
        lambdaUpdateWrapper.set(SysRole::getRoleCode, sysRole.getRoleCode());
    }
    if (sysRole.getDescription() != null && !sysRole.getDescription().isEmpty()) {
        lambdaUpdateWrapper.set(SysRole::getDescription, sysRole.getDescription());
    }
    update(lambdaUpdateWrapper);
}
```

****
## 2. 系统用户管理

该功能模块主要是针对系统用户的，也就是面向系统管理层，是后台系统用户，例如给管理员、运维人员、客服等新增账号，因此需要新增一个数据库表 sys_user，它和 user_info 不一样，
user_info 是面向业务层的用户信息表，比如普通用户、客户、会员等，用户登录校验时也是从这张表中查询数据，本质上是业务用户表，和系统权限、角色、登录功能无关；
而 sys_user 主要存储系统管理相关数据，例如用户名、密码、状态、角色 ID、权限信息等，用于权限控制和角色分配的操作。与其对应的实体类为：

```java
@Data
@TableName("sys_user")
@Schema(description = "系统用户实体类")
public class SysUser extends BaseEntity {

	@Schema(description = "用户名")
	private String userName;

	@Schema(description = "密码")
	private String password;

	@Schema(description = "昵称")
	private String name;

	@Schema(description = "手机号码")
	private String phone;

	@Schema(description = "头像")
	private String avatar;

	@Schema(description = "描述")
	private String description;

	@Schema(description = "状态（1：正常 0：停用）")
	private Integer status;

}
```

****
### 2.1 新增系统用户

```java
@PostMapping("/addSysUser")
@Operation(summary = "新增系统用户", description = "在 sys_user 表中插入数据")
public Result addSysUser(@RequestBody SysUser sysUser) {
    sysUserService.addSysUser(sysUser);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

```java
@Override
public void addSysUser(SysUser sysUser) {
    save(sysUser);
}
```

****
### 2.2 查询系统用户

Controller 层：

```java
@PostMapping("/selectSysUserPage")
@Operation(summary = "查询所有系统用户", description = "分页查询所有系统用户")
public Result selectSysUserPage(@RequestBody SysUserQueryDto sysUserQueryDto) {
    PageResult<SysUser> sysUserPageResult = sysUserService.selectSysUserPage(sysUserQueryDto);
    return Result.build(sysUserPageResult, ResultCodeEnum.SUCCESS);
}
```

Service 层：

```java
@Override
public PageResult<SysUser> selectSysUserPage(SysUserQueryDto sysUserQueryDto) {
    // 构造查询条件
    LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
    if (sysUserQueryDto.getUsername() != null && !sysUserQueryDto.getUsername().isEmpty()) {
        lambdaQueryWrapper.like(SysUser::getUsername, sysUserQueryDto.getUsername());
    }
    if (sysUserQueryDto.getName() != null && !sysUserQueryDto.getName().isEmpty()) {
        lambdaQueryWrapper.like(SysUser::getName, sysUserQueryDto.getName());
    }
    if (sysUserQueryDto.getPhone() != null && !sysUserQueryDto.getPhone().isEmpty()) {
        lambdaQueryWrapper.eq(SysUser::getPhone, sysUserQueryDto.getPhone());
    }
    // 构造分页对象
    Page<SysUser> page = new Page<>(sysUserQueryDto.getPage(), sysUserQueryDto.getSize());
    // 查询后的结果
    Page<SysUser> sysRolePage = page(page, lambdaQueryWrapper);

    return new PageResult<SysUser>(sysRolePage.getTotal(), sysUserQueryDto.getPage(), sysRolePage.getRecords());
}
```

****
### 2.3 系统用户头像

当在进行用户添加的时候，此时可以在添加表单页面点击 "+" 号，然后选择要上传的用户图像。选择完毕以后，那么此时就会请求后端上传文件接口，将图片的二进制数据传递到后端。
后端需要将数据图片存储起来，然后给前端返回图片的访问地址，然后前端需要将图片的访问地址设置给 sysUser 数据模型，当用户点击提交按钮的时候，那么此时就会将表单进行提交，
后端将数据保存起来即可。

****
#### 2.3.1 Minio

目前可用于文件存储的网络服务选择也有不少，比如阿里云 OSS、七牛云、腾讯云等等，可是收费。为了节约成本，很多公司使用 MinIO 做为文件服务器。
官网：[https://www.minio.org.cn/](https://www.minio.org.cn/)。

安装地址：[https://www.minio.org.cn/docs/cn/minio/container/index.html](https://www.minio.org.cn/docs/cn/minio/container/index.html)。
也可以使用 Docker 安装（如果设置账号密码，要求账号不小于 3 位，密码不小于 8 位）：

```shell
docker run -d \
  --name minio \
  -p 9001:9000 \
  -p 9090:9090 \
  -v /root/minio/data:/data \
  -e "MINIO_ROOT_USER=admin" \
  -e "MINIO_ROOT_PASSWORD=admin123" \
  minio/minio server /data --console-address ":9090"
```

需要注意的是，MinIO 默认端口：

- API 端口：9000 
- 控制台端口：9001

Portainer 默认端口：

- Web 界面：9000（新版有时是 9443 用于 https）

如果在同一台服务器上都使用默认端口运行，那 Portainer 的 9000 和 MinIO 的 9000 会冲突，因为同一台机器同一时间同一个端口只能绑定一次。所以在创建 MinIO 的时候要修改一下端口的绑定，
按照上面的命令创建 MinIO，宿主机访问 9001 就相当于访问容器内部的 API 9000，-p 9090:9090 + --console-address ":9090" 代表宿主机访问 9090 就可以打开 MinIO Web 控制台。
安装成功后，访问 192.168.149.101/9090 即可进入，

```shell
# 设置别名代替 MinIO 链接，设置时需要设置本地绑定的端口号，而不是可视化界面的端口号
mc alias set myminio http://192.168.149.101:9001 admin admin123

# 测试连接
mc ls myminio
```

1、创建 Bucket

```shell
mc mb myminio/testbucket
```

Web 控制台：点击 “+ Create bucket” -> 填写名称 -> 创建

2、上传文件

```shell
mc cp /root/file.txt myminio/testbucket/
```

Web 控制台：进入 Bucket -> 点击 Upload -> 选择文件 -> 上传

Web 控制台单节点模式不显示策略管理菜单，所以单节点只能用 CLI 来删除 Bucket 和设置访问策略。

3、删除

```shell
# 空 Bucket
mc rb myminio/spzx-bucket

# 非空 Bucket
mc rb --force myminio/spzx-bucket

# 删除桶中文件
mc rm myminio/spzx-bucket/file.txt
```

4、设置访问策略（公开读取）

```shell
# 设置 Bucket 允许匿名读取（公开访问）
mc anonymous set download myminio/spzx-bucket
``` 

测试上传文件：

添加 MinIO 的依赖：

```xml
<!-- MinIO -->
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.5.7</version>
</dependency>
```

编写测试类：

```java
@Test
void testMinIOFileUpload() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
    // 创建一个Minio的客户端对象
    MinioClient minioClient = MinioClient.builder()
            .endpoint("http://192.168.149.101:9001")
            .credentials("admin", "admin123")
            .build();

    boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("spzx-bucket").build());

    // 如果不存在，那么此时就创建一个新的桶
    if (!found) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket("spzx-bucket").build());
    } else {  // 如果存在打印信息
        System.out.println("Bucket 'spzx-bucket' already exists.");
    }

    FileInputStream fis = new FileInputStream("E://001.jpg") ;
    PutObjectArgs putObjectArgs = PutObjectArgs.builder()
            .bucket("spzx-bucket") // 选择桶
            .stream(fis, fis.available(), -1) // 设置要上传的文件，文件大小，以及分块大小（-1 表示自动处理分块大小）
            .object("001.jpg") // 设置上传文件后存储在桶中的文件名
            .build();
    minioClient.putObject(putObjectArgs) ;

}
```

****
#### 2.3.2 将头像上传到 Minio

在初始化 Minio 客户端的时候需要提供 Minio 初始化容器时提供的账号密码（账号密码默认值为 adminminio），还需要提供访问的 URL 和需要使用的桶的名字，
为了方便后期修改，就把它们写进 application 配置文件中，因此需要使用一个配置类来接收这些数据：

```java
@Data
@Component
@ConfigurationProperties(prefix = "spzx.minio")
public class MinioProperties {
    private String accessKey;
    private String secretKey;
    private String endpointUrl;
    private String bucketName;
}
```

```yaml
spzx:
  minio:
    access-key: admin
    secret-key: admin123
    endpoint-url: http://192.168.149.101:9001
    bucket-name: spzx-bucket
```

Minio 客户端的初始化可以用一个配置类来完成，当 Spring 加载时就会自动完成初始化，只需要提供上面的数据即可：

```java
@Configuration
public class MinioConfig {

    private final MinioProperties minioProperties;

    // 构造器注入
    public MinioConfig(MinioProperties minioProperties) {
        this.minioProperties = minioProperties;
    }

    // 初始化 MinioClient
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpointUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

    // 创建 ApplicationRunner Bean，接收 MinioClient 作为参数，如果没有创建桶则自动进行创建
    @Bean
    public ApplicationRunner minioInitializer(MinioClient minioClient) {
        return args -> {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build()
            );
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build());
            }
            // 设置桶策略为公开读取（download）
            String policyJson = "{\n" +
                    "  \"Version\":\"2012-10-17\",\n" +
                    "  \"Statement\":[{\n" +
                    "    \"Effect\":\"Allow\",\n" +
                    "    \"Principal\":\"*\",\n" +
                    "    \"Action\":[\"s3:GetObject\"],\n" +
                    "    \"Resource\":[\"arn:aws:s3:::" + minioProperties.getBucketName() + "/*\"]\n" +
                    "  }]\n" +
                    "}";
            minioClient.setBucketPolicy(
                    io.minio.SetBucketPolicyArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .config(policyJson)
                            .build()
            );
        };
    }
}
```

Controller 层：

浏览器上传文件通常是一个 multipart/form-data 类型的 POST 或 PUT 请求，文件内容在请求体中分片传输，每一片称为 “part”。
SpringMVC 提供了 MultipartResolver（默认是 StandardServletMultipartResolver），会把请求体里的每个文件 part 封装成一个 MultipartFile 对象。
所以 Controller 层使用 MultipartFile 是 SpringMVC 对上传文件的抽象，用 MultipartFile 就可以直接操作文件流，不用手动解析 HTTP 请求。
把接收到的上传文件传递给 Service 层处理后会返回一个 URL　路径，该路径就是存储在　Minio 中的路径，只要 Minio 中的桶的访问权限公开，就可以通过该路径看到具体的图片。

而前端的新增用户和上传头像的功能一般是分为两个请求来完成的，这样就不会因为上传头像的失败而导致整个新增用户功能也失效。当前端点击上传头像按钮后，
后端处理完请求则会把这个上传成功的头像的存储路径返回给前端，前端拿着这个数据和表单中填写的新增用户的其它数据会一起封装进 SysUser 实体类由后端处理并存入数据库中，
因此一个新增操作是分为两步完成的，当然修改操作同理。

```java
@PutMapping("/uploadAvatar")
@Operation(summary = "上传头像", description = "上传头像到 Minio")
public Result uploadAvatar(@RequestParam("file") MultipartFile file) {
    String fileUrl = sysUserService.uploadAvatar(file);
    return Result.build(fileUrl, ResultCodeEnum.SUCCESS);
}
```

Service 层：

在执行上传头像操作前，需要先对头像文件的大小和类型进行限制，这里设置的文件最大规格为 2MB，支持的类型为 png、jpeg 等常见图片格式。当然 SpringBoot 默认限制的文件大小为 1MB，
因此需要手动进行修改一下：

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 50MB      # 单个文件最大大小
      max-request-size: 100MB  # 单次请求最大大小（多个文件一起上传时）
```

接着就是设置文件存储在 Minio 中的路径，通常都是以上传文件的日期作为目录，时间戳和随机字符串作为文件名的，最后返回文件路径时，只需要加上 Minio 的访问地址和桶名即可。

```java
private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
private static final Set<String> ALLOWED = Set.of("image/png", "image/jpeg", "image/jpg", "image/gif");

@Override
public String uploadAvatar(MultipartFile file) {

    if (file == null || file.isEmpty()) {
        throw new IllegalArgumentException("文件为空");
    }
    if (file.getSize() > MAX_FILE_SIZE) {
        throw new IllegalArgumentException("文件太大，最大 " + (MAX_FILE_SIZE / 1024 / 1024) + " MB");
    }
    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED.contains(contentType.toLowerCase())) {
        throw new IllegalArgumentException("不支持的文件类型: " + contentType);
    }

    // 扩展名
    String original = file.getOriginalFilename();
    String ext = "png";
    // 当原始文件没有后缀时，则默认使用 png，否则使用原始图片的类型
    if (original != null && original.contains(".")) {
        ext = original.substring(original.lastIndexOf('.') + 1);
    }

    // 设置日期目录
    String datePath = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";
    // 时间戳 + 随机数，避免重复
    String dir = datePath + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 6).replace("_", "") + "." + ext;
    // 存入桶中的文件最终路径
    String avatarName = "avatars/" + dir;
    try (InputStream is = file.getInputStream()) {
        PutObjectArgs putArgs = PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(avatarName)
                .stream(is, file.getSize(), -1) // partSize(-1 表示 sdk 自己决定)
                .contentType(file.getContentType()) // 文件类型
                .build();
        minioClient.putObject(putArgs);
    } catch (Exception  e) {
        throw new RuntimeException(e);
    }

    // 去掉掉 MinIO 访问地址末尾的 "/"
    String endpoint = minioProperties.getEndpointUrl().replaceAll("/$", "");
    String publicUrl = endpoint + "/" + minioProperties.getBucketName() + "/" + avatarName;
    return publicUrl;
}
```

****
### 2.4 清除 Minio 中的旧头像

毕竟是上传头像到一个存储空间，如果不加以管理删除不再使用的文件，迟早会导致容器爆满无法使用，因此需要定时进行删除操作。而头像这个东西，只要进行了一次更换，
就可以看作以前的那个头像已经是不再需要的文件了，也就可以进行删除操作了。实现的思路是：当调用上传头像的接口时会把用户当时选择的头像文件保存到 Minio，
此时给这个头像文件保存在一个临时目录，只有当用户点击保存信息时才把这个头像的 URL 移到最终的目录，并且，点击保存信息按钮时也应该触发删除上一次使用的头像的操作，
也就是得进行一次查询数据库的操作获取上一次该用户使用的头像的 URL，然后执行 Minio 的 API 进行删除。

修改原始的新增代码，在执行插入数据到数据库前需要检查前端是否发送了头像的 URL 过来，即是否在点击新增按钮前上传了头像到 Minio，如果没有上传头像，那么调用 copyMinioTempToCurrent() 方法返回的就是 null，
此时就不需要更新前端传递来的 SysUser 中的 avatar 字段，让它保持空的即可；如果调用 copyMinioTempToCurrent() 方法后不为空并接收到了返回的一个新的头像的 URL，
那么就证明在点击新增保存按钮前就进行了头像的上传操作，此时就需要更新 SysUser 中的 avatar 字段，也就是保存头像的 URL 到数据库。

```java
@Override
public void addSysUser(SysUser sysUser) {
    // 检查传递来的 SysUser 中是否包含头像 URL
    String currentAvatarUrl = sysUser.getAvatar();
    String newAvatarUrl = copyMinioTempToCurrent(currentAvatarUrl);
    if (currentAvatarUrl != null) {
        sysUser.setAvatar(newAvatarUrl);
    }
    // 当 newAvatarUrl 为空时，说明没有进行拷贝，也就是没有进行上传头像的操作，那么存入数据库时使用空的头像 URL　即可（前端未点击上传时即为空）
    save(sysUser);
}
```

```java
/**
 * 只有当前用户的头像 URL 是临时路径（也就是刚刚上传了头像），才进行 Minio 中的路径拷贝
 */
private String copyMinioTempToCurrent(String currentAvatarUrl) {
    // 只有新增用户时点击了上传头像再进行路径的转移
    if (StringUtils.isNotBlank(currentAvatarUrl) && currentAvatarUrl.contains("avatars/temp")) {
        // 把存储在 Minio 临时目录的头像 URL 保存到固定目录下，同时把固定目录的头像 URL 保存到数据库中
        String newAvatarUrl = currentAvatarUrl.replace("avatars/temp/", "avatars/use/");
        String newMinioUrl = getMinioObjectName(newAvatarUrl); // 使用中目录对象
        String tempMinioUrl = getMinioObjectName(currentAvatarUrl); // 临时目录对象
        // 进行拷贝操作
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(newMinioUrl)
                            .source(CopySource.builder()
                                    .bucket(minioProperties.getBucketName())
                                    .object(tempMinioUrl)
                                    .build())
                            .build()
            );
            log.info("成功将临时目录中的头像拷贝到使用中目录：" + newMinioUrl);
            return newAvatarUrl;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    return null;
}
```

当然这里传递给 Minio API 的头像的 URL 不能是完整的 HTTP，应该是　Minio 中的文件对象，所以获取到前端传递的或者是从数据库中查出来的头像的 URL 都要进行截串操作，
确保 Minio 能够正常进行拷贝或删除操作。

```text
http://192.168.149.101:9001/spzx-bucket/avatars/temp/2025-09-23/1758622041077_df4f48.jpg
endpoint = http://192.168.149.101:9001
bucketName = spzx-bucket
oldAvatarUrl = http://192.168.149.101:9001/spzx-bucket/avatars/2025-09-23/123456.png
```

```java
/**
 * 因为删除和拷贝 Minio 中的文件时使用的是对象名，也就是桶中该文件的路径，因此需要从完整的 URL 中进行截串操作
 *   桶名: spzx-bucket
 *   对象名: avatars/use/2025-09-23/123456.png
 */
@NotNull
private String getMinioObjectName(String avatarUrl) {
    // 替换掉 Minio 的前缀地址和桶名，后面的头像存放文件路径即为对象名
    return avatarUrl.substring(
            avatarUrl.indexOf(minioProperties.getBucketName())
                    + minioProperties.getBucketName().length() + 1 // 最后加 1 是用来去掉桶名后的斜杠 "/"
    );
}
```

同理，修改系统用户信息也需要这么操作。不过修改系统用户信息的操作是建立在已经完成添加的用户的基础上的，也就是说当前回显的用户信息可能是包含一个头像 URL 的，
因此需要判断该 URL 是临时目录下的还是使用中目录下的，为什么这么说呢？因为在点击上传头像按钮调用的那个接口，是把每个上传的头像都直接放在了临时目录下的，
所以后端接收到头像的 URL 的时候只要判断这个 URL 中是否包含临时目录的字符串即可。如果当前传递给后端的头像的 URL 是临时目录的，那就证明该用户的头像刚刚修改了，
但此时并没有将数据保存到数据库，因为只有在点击修改或者新增按钮后弹出的表单中点击了保存按钮才会触发数据库的更新操作，因此上传头像到 Minio 的操作不会涉及数据库的修改。

回到正题，在修改系统用户信息时，将前端传递来的头像的 URL 传递给 copyMinioTempToCurrent() 方法，根据该方法的返回值判断是否进行了拷贝操作（即是否更新了头像），
如果拷贝成功，那么就需要更新 SysUser 的 avatar 字段并更新数据库信息，接着就要执行删除旧头像的操作了。旧头像是保存在使用中目录的，因为按照当前代码的设计来看，
只要是点击了上传头像并保存信息到数据库，那么头像就会从 Minio 的临时目录拷贝到使用中目录，因此现在的删除操作就是删除使用中目录的头像，那么如何确定要删除哪个呢？
那就是通过查询数据库中该用户的 avatar 字段是什么了，所以在该方法的一开始就进行了这个查询操作，目的就是为了后续可以通过该 URL 获取到 Minio 中文件的对象名，
然后进行删除。

```java
@Override
@Transactional
public void updateSysUse(SysUser sysUser) {

    // 先查询旧头像信息
    SysUser existingUser = getById(sysUser.getId());
    String oldAvatarUrl = existingUser != null ? existingUser.getAvatar() : null;
    
    // 检查传递来的 SysUser 中是否包含头像 URL
    String currentAvatarUrl = sysUser.getAvatar();
    String newAvatarUrl = copyMinioTempToCurrent(currentAvatarUrl);
    
    // 拷贝成功，证明修改信息时也同时修改了头像（此时传递的头像 URL 就是最新的 URL），那么就要删除旧的头像
    if (newAvatarUrl != null) {
        // 更新头像信息
        sysUser.setAvatar(newAvatarUrl);
        // 更新数据库
        updateById(sysUser);
        // 删除旧的头像
        if (oldAvatarUrl != null && !oldAvatarUrl.contains("avatars/temp")) {
            String deleteAvatarUrl = getMinioObjectName(oldAvatarUrl);
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(deleteAvatarUrl) // 因为此时是已经点击了上传新的头像，所以必须查询数据库获取旧的头像的 URL
                        .build());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
```

除了在修改用户头像时会触发删除旧头像的操作，还需要完成删除临时目录的操作，当然该操作不是每次调用完上传头像的接口就开始执行的，因为临时目录中的头像只有在用户完成保存信息后才不具备有效性，
所以可以设置一个定时任务，在每天的 03:00 进行删除临时目录的操作。这里直接调用 Minio 的 API　获取到指定桶下的指定目录的所有文件，它会被封装成一个迭代器对象，
里面的每一个元素就是一个文件对象，不过 Minio 的 API 不需要整个文件对象，它只需要桶名和文件对象名即可，所以传值时传递的是 item.objectName()。

```java
@Service
public class MinioTempCleaner {

    // 配置 log
    private static final Logger log = Logger.getLogger(SysUserServiceImpl.class.getName());

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioProperties minioProperties;

    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadSeckillSkuLatest3Days() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        log.info("开始扫描 Minio 临时目录！");
        String bucketName = minioProperties.getBucketName();
        String tempPrefix = "avatars/temp/";
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(tempPrefix)
                        .recursive(true) // 递归遍历子目录
                        .build()
        );
        for (Result<Item> result : results) {
            Item item = result.get();
            String objectName = item.objectName();
            log.info("当前文件：" + objectName);
            log.info("执行删除 Minio 临时目录！");
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        }
    }
}
```

****
### 2.5 删除系统用户

删除系统用户很简单，就是调用一下 MybatisPlus 的 remove 方法，在执行删除前查询一下数据库该用户的头像 URL，然后执行删除 Minio 中的文件即可。

Controller 层：

```java
@DeleteMapping("/deleteSysUser/{id}")
@Operation(summary = "删除系统用户", description = "根据传递的用户 id 进行删除操作，同时删除保存在 Minio 中的头像")
public Result deleteSysUser(@PathVariable("id") Long id) {
    sysUserService.deleteSysUser(id);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

Service 层：

```java
@Override
@Transactional
public void deleteSysUser(Long id) {
    // 根据 id 查询出当前用户的头像 URL，然后删除　Minio　中对应的文件
    String avatarUrl = getById(id).getAvatar();
    deleteMinioOldAvatar(avatarUrl);
    // 删除数据库中的用户信息
    removeById(id);
}
```

因为删除 Minio 中的头像是个重复的代码，因此可以提取出来封装成一个方法：

```java
private void deleteMinioOldAvatar(String avatarUrl) {
    if (avatarUrl != null && !avatarUrl.contains("avatars/temp")) {
        String deleteMinioUrl = getMinioObjectName(avatarUrl);
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(deleteMinioUrl)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

****
### 2.6 给系统用户分配角色

#### 2.6.1 查询所有角色

给系统用户分配角色就是在系统用户的边上有个分配角色的按钮，当用户点击"分配角色"按钮的时候，此时就会弹出一个对话框，在该对话框中会展示出来系统中所有的角色信息。
用户此时就可以选择对应的角色，选择完毕以后，点击确定按钮，此时就需要请求后端接口，将选中的角色数据保存保存到 sys_user_role 表中。因此，在执行分配角色的功能前，
需要将系统中的所有角色都查询出来，这样才能在对话框中选择给用户分配哪个角色。

Controller 层：

这里没有直接返回一个 List<SysRole> 集合，而是把这个集合封装进 Map 集合，这样前端接收到数据后可以较为直观的知道当前获取或使用的数据是什么。

```java
@PostMapping("/listAllRole")
@Operation(summary = "查询所有角色", description = "将查询出的所有角色封装成集合")
public Result listAllSysRole() {
    Map<String, List<SysRole>> allSysRole = roleManageService.listAllSysRole();
    return Result.build(allSysRole, ResultCodeEnum.SUCCESS);
}
```

Service 层：

```java
@Override
public Map<String, List<SysRole>> listAllSysRole() {
    List<SysRole> sysRoleList = list();
    Map<String, List<SysRole>> listAllSysRole = new HashMap<>();
    listAllSysRole.put("allSysRole", sysRoleList);
    return listAllSysRole;
}
```

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "allSysRole": [
      {
        "id": 9,
        "createTime": "2023-05-04 10:36:06",
        "updateTime": "2023-06-02 09:03:31",
        "isDeleted": 0,
        "roleName": "平台管理员",
        "roleCode": "ptgly",
        "description": "平台管理员"
      },
      {
        "id": 10,
        "createTime": "2023-05-04 10:36:22",
        "updateTime": "2023-07-18 08:40:56",
        "isDeleted": 0,
        "roleName": "用户管理员",
        "roleCode": "yhgly",
        "description": "用户管理员"
      },
      ...
    ]
  }
}
```

****
#### 2.6.2 关联系统用户和角色

关联系统用户和角色其实就是新增数据到表 sys_user_role 中，该表主要存储 role_id 和 user_id，因此插入数据时也只需要插入这两个。

Controller 层：

前端选中某个用户给他赋予角色时可以选择多个角色，所以后端要接收的数据是一个用户 id 和多个角色 id，那么封装成实体类则为：

```java
@Data
@Schema(description = "请求参数实体类")
public class SysUserAssoRoleDto {

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "角色id的List集合")
    private List<Long> roleIdList;

}
```

```java
@PutMapping("/assignRoles")
@Operation(summary = "给系统用户分配角色", description = "一个系统用户可以选择多个角色，把它们的关系保存进 sys_user_role 表")
public Result assignRoles(@RequestBody SysUserAssoRoleDto sysUserAssoRoleDto) {
    roleUserService.assignRoles(sysUserAssoRoleDto);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

Service 层：

该关联操作本质上就是个插入数据的行为，而具体操作的表的对应实体类为：

```java
@Data
@TableName("sys_user_role")
public class SysRoleUser extends BaseEntity {
    private Long roleId;       // 角色id
    private Long userId;       // 用户id
}
```

不过前端可能传递多个角色 id 过来，所以需要依次遍历并创建多个 SysRoleUser 实体类，最后批量进行插入，但插入前还得删除之前该用户关联的所有角色。

```java
@Override
@Transactional
public void assignRoles(SysUserAssoRoleDto sysUserAssoRoleDto) {
    // 删除之前关联的所有角色
    remove(new LambdaQueryWrapper<SysRoleUser>().eq(SysRoleUser::getUserId, sysUserAssoRoleDto.getUserId()));
    // 再新增新的关联角色
    List<Long> roleIdList = sysUserAssoRoleDto.getRoleIdList();
    ArrayList<SysRoleUser> sysRoleUserList = new ArrayList<>();
    roleIdList.forEach(roleId->{
        SysRoleUser sysRoleUser = new SysRoleUser();
        sysRoleUser.setUserId(sysUserAssoRoleDto.getUserId());
        sysRoleUser.setRoleId(roleId);
        sysRoleUserList.add(sysRoleUser);
    });
    saveBatch(sysRoleUserList);
}
```

****
#### 2.6.3 用户管理角色数据回显

当点击分配角色按钮的时候，除了需要将系统中所有的角色数据查询处理以外，还需要将当前登录用户所对应的角色数据查询出来，
在进行展示的时候需要用户所具有的角色数据需要是选中的状态。

Controller 层：

当点击用户的分配角色按钮时会传递该用户的 id 给后端，后端就利用这个用户 id 查询 sys_user_role 获取到该用户关联的所有角色的 id 集合，拿到集合后再查询所有的角色信息。

```java
@GetMapping("/getUserRoleData/{id}")
@Operation(summary = "回显当前系统用户所属的角色", description = "点击分配按钮时应该展示该用户当前拥有的所有角色")
public Result getUserRoleData(@PathVariable("id") Integer id) {
    Map<String, List<SysRole>> userHasRole =  roleUserService.getUserRoleData(id);
    return Result.build(userHasRole, ResultCodeEnum.SUCCESS);
}
```

Service 层：

```java
@Override
public Map<String, List<SysRole>> getUserRoleData(Integer id) {
    List<SysRoleUser> sysRoleUserList = list(new LambdaQueryWrapper<SysRoleUser>().eq(SysRoleUser::getUserId, id));
    // 把当前用户的角色 id 封装成集合
    List<Long> roleIds = sysRoleUserList.stream().map(SysRoleUser::getRoleId).collect(Collectors.toList());
    // 执行批量查询
    List<SysRole> sysRoleList = roleManageService.listByIds(roleIds);
    HashMap<String, List<SysRole>> userHasRole = new HashMap<>();
    userHasRole.put("userHasRole", sysRoleList);
    return userHasRole;
}
```

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userHasRole": [
      {
        "id": 36,
        "createTime": "2023-09-03 23:23:04",
        "updateTime": "2023-09-03 23:23:04",
        "isDeleted": 0,
        "roleName": "销售人员",
        "roleCode": "销售",
        "description": null
      },
      {
        "id": 37,
        "createTime": "2023-09-03 23:24:26",
        "updateTime": "2023-09-04 10:04:17",
        "isDeleted": 0,
        "roleName": "测试人员",
        "roleCode": "test",
        "description": null
      },
      {
        "id": 38,
        "createTime": "2023-09-03 23:24:32",
        "updateTime": "2025-09-22 16:08:17",
        "isDeleted": 0,
        "roleName": "开发人员",
        "roleCode": "dev",
        "description": null
      }
    ]
  }
}
```

****
## 3. 菜单管理

菜单管理就是对系统中首页中的左侧菜单进行维护，系统菜单的数据表结构如下，可以看到菜单是一种分级结构的，一个父菜单下可能会有多个子菜单，这里默认规定只存在两级，
即一个父菜单可以有多个单级的子菜单：


| 名称 | 类型 | 长度 | 小数点 | 不是 null | 虚拟 | 键 | 注释 |
|------|------|------|--------|-----------|------|----|------|
| id | bigint | ✅ | □ | ✅ | □ | 1 | 编号 |
| parent_id | bigint | ✅ | □ | ✅ | □ | 1 | 所属上级 |
| title | varchar | 20 | □ | ✅ | □ | 1 | 菜单标题 |
| component | varchar | 100 | □ | ✅ | □ | 1 | 组件名称 |
| sort_value | int | ✅ | □ | □ | □ | 1 | 排序 |
| status | tinyint | ✅ | □ | □ | □ | 1 | 状态(0.禁止,1:正常) |
| create_time | timestamp | ✅ | □ | □ | □ | 1 | 创建时间 |
| update_time | timestamp | ✅ | □ | □ | □ | 1 | 更新时间 |
| is_deleted | tinyint | ✅ | □ | □ | □ | 1 | 删除标记（0:不可用 1:可用） |

创建与数据库表相对应的实体类：

```java
@Data
@TableName("sys_menu")
@Schema(description = "系统菜单实体类")
public class SysMenu extends BaseEntity {

	@Schema(description = "父节点id")
	private Long parentId;

	@Schema(description = "节点标题")
	private String title;

	@Schema(description = "组件名称")
	private String component;

	@Schema(description = "排序值")
	private Integer sortValue;

	@Schema(description = "状态(0:禁止,1:正常)")
	private Integer status;

	// 下级列表
    @TableField(exist = false)
	@Schema(description = "菜单子节点")
	private List<SysMenu> children;

}
```

****
### 3.1 获取菜单列表

Controller 层：

获取菜单列表是在用户进入系统时就会自动触发的，因此不需要传递参数，直接获取所有的菜单信息即可。通过调用 Service 层的方法，将最终的数据封装成一个 List 集合，
集合中的每个元素都是一个菜单，该元素可能包含子菜单，而子菜单又可能包含下一个子菜单，因此需要用到递归。

```java
@GetMapping("/selectMenu")
@Operation(summary = "查询菜单", description = "展示所有的菜单列表")
public Result getMenu() {
    List<SysMenu> sysMenuList = sysMenuService.getMenu();
    return Result.build(sysMenuList, ResultCodeEnum.SUCCESS);
}
```

Service 层：

这里先查询出 parentId 为 0 的数据，这些数据就是顶级的父菜单，因为它们没有父亲，所以 parentId 设置为 0。接着遍历这些顶级父菜单，依次调用 getChildMenu() 方法，
这是个递归方法，最终会返回一个封装好 children 字段的顶级父菜单 SysMenu 实体类，接着把这些实体类封装为 List 集合返回给前端即可。

```java
@Override
public List<SysMenu> getMenu() {
    List<SysMenu> sysMenuList = new ArrayList<>();
    // 查询 parentId 为 0 的数据，这些就是父菜单
    List<SysMenu> parentMenuList = list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, 0L));
    for (SysMenu parentMenu : parentMenuList) {
        SysMenu sysMenu = getChildMenu(parentMenu);
        sysMenuList.add(sysMenu);
    }
    return sysMenuList;
}
```

该递归方法就是接收一个父类菜单，然后查询该节点是否有子节点，所以会先查询以传入的节点的 id 作为查询 parentId 条件的数据，如果有这些数据，那就证明传入的该节点是有子节点的，
那么就可以把查询到的子节点列表赋值给当前传入的节点的 children 字段；如果上一步的赋值操作成功，那么就可以遍历上一步赋值的那些子节点了，也就是依次传入该递归方法，
判断这些节点是否也有子节点，也就是以它们的 id 作为查询 parentID 的条件，如果能够查到数据，就证明还有，反之则没有，
直接返回该节点对象（如果有孩子节点，那么返回的就是进行了 parentMenu.setChildren(childrenMenuList) 操作的节点对象）。

```java
// 父节点传入子节点，再遍历每个子节点获取它的子节点，直到某个节点不再拥有子节点
private SysMenu getChildMenu(SysMenu parentMenu) {
    // 获取到父菜单的子菜单，查询那些 parenId 为父菜单 id 的数据
    // select * from sys_menu where parentId = ?
    List<SysMenu> childrenMenuList = list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, parentMenu.getId()));
    if (childrenMenuList != null && !childrenMenuList.isEmpty()) {
        parentMenu.setChildren(childrenMenuList);
    }
    // 如果该节点有孩子节点，那就继续查询该节点的孩子节点，看该节点的孩子节点是否也有孩子节点
    if (parentMenu.getChildren() != null && !parentMenu.getChildren().isEmpty()) {
        // 遍历当前节点的子节点是否有子节点，递归调用本方法，只要没有数据的 parentId 为该节点的字节点的 id，那么就结束递归
        parentMenu.getChildren().forEach(this::getChildMenu);
    }
    return parentMenu;
}
```

通过接口文档进行测试：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "createTime": "2023-05-04 10:46:47",
      "updateTime": "2023-05-06 17:33:53",
      "isDeleted": 0,
      "parentId": 0,
      "title": "系统管理",
      "component": "system",
      "sortValue": 1,
      "status": 1,
      "children": [
        {
          "id": 2,
          "createTime": "2023-05-04 10:47:13",
          "updateTime": "2023-05-06 17:33:57",
          "isDeleted": 0,
          "parentId": 1,
          "title": "用户管理",
          "component": "sysUser",
          "sortValue": 1,
          "status": 1,
          "children": [
            {
              "id": 31,
              "createTime": "2023-05-04 11:42:44",
              "updateTime": "2023-05-04 11:43:34",
              "isDeleted": 0,
              "parentId": 2,
              "title": "我是用户管理的子菜单，系统管理的三级菜单",
              "component": null,
              "sortValue": 1,
              "status": 1,
              "children": []
            }
          ]
        },
        {
          "id": 4,
          "createTime": "2023-05-04 10:47:26",
          "updateTime": "2023-05-06 17:33:58",
          "isDeleted": 0,
          "parentId": 1,
          "title": "菜单管理",
          "component": "sysMenu",
          "sortValue": 3,
          "status": 1,
          "children": []
        },
        ...
      ]
    }
  ]
}
```

****

上述代码的写法虽然能获取到正确的数据，但是这种写法有种缺陷，那就是会造成 N + 1 查询问题，什么是 N + 1 查询问题？N + 1 问题就是：1 次查询获取主实体列表，
N 次查询分别获取每个主实体的关联数据，例如上面的代码写法，先查询一次获得所有顶级父类节点，接着遍历每个父类节点再进行查询以该父类节点的 id 为 parentId 的数据，
执行几次循环就会执行几次数据库的查询操作，这就大大增加了数据库的开销，因此需要进行一下修改。

这里的修改思路就是，先查询出所有的菜单数据，把它们封装成一个 Map 集合，以每个菜单实体的 parentId 作为 Map 的 key。要想实现这样的一个 Map，就要用到 Stream 的功能，
它可以将数据进行分组并封装成一个 Map 集合，这里采用的分组条件就是 SysMenu 实体类的 parentId 字段，这样，一个 parentId 就可以代表所有该 id 下的子节点。
接着把 parentId 和 这个 Map 集合作为条件传递给递归方法，这里传递的 parentId 是 0，也就是先获取出顶级父节点，接着再递归遍历顶级父节点的子节点，
因此传递的 parentId 为 0 而不是从每个实体类中获取出 id。

至于为什么这样设计，因为如果用 parentId 作为 key，单个 SysMenu 作为 value 的话，那么一个 parentId 就只能代表一个子节点，因为 Map 集合的特性就是 key 不能重复，
添加重复的 key 就会直接数据覆盖，因此只能封装完整的相同 parentId 的子节点集合；不过，如果用 SysMenu 的 id 字段作为 key 呢？那么就可以一个 id 代表一个 SysMenu，
但是这中做法就没必要了，因为本身 id 就不可能是一个公共的字段，每个 SysMenu 的 id 都不一样，这样设计和直接封装 SysMenu 为一个 List 没什么区别，
主要还是利用 parentId 可以代表多个子节点的特性，通过传递一个 parentId 就能获取到所有符合条件的子节点，效率更高。

```java
@Override
public List<SysMenu> getMenu() {
    List<SysMenu> sysMenuList = list();
    // 让这些菜单集合根据 parenId 进行分组
    Map<Long, List<SysMenu>> map = sysMenuList.stream().collect(Collectors.groupingBy(SysMenu::getParentId));
    // 从顶层菜单开始递归，因此先传入的 parentId 为 0
    return getChildMenu(0L, map);
}
```

该递归方法接收一个 parentId 和拥有所有菜单数据的 Map 集合，通过传递来的 parentId 查询该 Map 集合中的 List<SysMenu>，只要获取到的 List<SysMenu> 不为空，
那么就证明以 parentId 为 id 的 SysMenu 节点是有子节点的，例如第一次传递的 0L，如果查询出了，证明表中是有一级菜单的。接着就是遍历这些节点，获取这些节点的 id，
把它们的 id 作为下一次递归传递的 parentId，也就是查询这些节点是否有子节点，因为每次递归最后都会返回从 Map 集合中查找的 List<SysMenu>，所以再遍历这些节点的时候，
可以给它们的 child 字段进行赋值，不管这次递归返回的集合是否有数据都可以进行赋值，如果没数据那么就是一个空集合，代表该节点的递归结束，轮到下一个节点了。

```java
// 父节点传入子节点，再遍历每个子节点获取它的子节点，直到某个节点不再拥有子节点
private List<SysMenu> getChildMenu(Long parentId, Map<Long, List<SysMenu>> map) {
    // 获取父节点下的子节点列表
    List<SysMenu> childMenuList = map.get(parentId);
    if (childMenuList != null) {
        // 获取到父菜单的子菜单，查询那些 parenId 为父菜单 id 的数据
        for (SysMenu sysMenu : childMenuList) {
            List<SysMenu> nextChildMenuList = getChildMenu(sysMenu.getId(), map);
            // 当返回的孩子节点集合不为空，证明当前子节点是有孩子的，所以给它的 children 字段赋值
            sysMenu.setChildren(nextChildMenuList);
        }
    } else {
        // 当指定的 parentId 下的子节点为空，那么就返回一个空集合，让 menuId 为该 parentId 的 SysMenu 实体类的 children 字段赋值为空集合，
        // 即代表没有孩子
        return Collections.emptyList();
    }
    // 当孩子节点为空，证明当前 parentId 指向的那个 menuId 就是最小子节点
    // 因为根据 parentId 查询出的菜单列表为空，所以上一次调用该方法的那个节点的子节点为空，那么就会返回一个空集合
    return childMenuList;
}
```

****
### 3.2 新增菜单

当用户点击添加按钮的时候，那么此时就会弹出一个对话框，在该对话框中需要展示添加菜单表单。当用户在该表单中点击提交按钮的时候那么此时就需要将表单进行提交，
在后端需要将提交过来的表单数据保存到数据库中即可。如果添加的是一级菜单，那么传入的 parentId 应该为 0，如果是其余菜单，那么传入的 parentId 应该根据当前操作的那个菜单的 id 为准，
把它作为 parentId 传递给后端。

Controller 层：

```java
@PostMapping("/addMenu")
@Operation(summary = "新增菜单", description = "在 sys_menu 表中插入数据，如果插入的是子菜单，则需要指定父菜单 id")
public Result addSysMenu(@RequestBody SysMenu sysMenu) {
    sysMenuService.addSysMenu(sysMenu);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

Service 层：

这里判断一下 parentId 是否为空，因为可能新增的菜单为一级菜单，前端不传递数据，那么就需要手动给 SysMenu 的 parentId 字段赋值为 0L，因为该字段的类型为 Long，
它是一个包装类，默认值为 null。

```java
@Override
public void addSysMenu(SysMenu sysMenu) {
    Long parentId = sysMenu.getParentId();
    if (parentId == null) {
        sysMenu.setParentId(0L);
    }
    save(sysMenu);
}
```

****
### 3.3 修改菜单

当用户点击修改按钮的时候，那么此时就弹出对话框，在该对话框中需要将当前行所对应的菜单数据在该表单页面进行展示。当用户在该表单中点击提交按钮的时候，
此时就需要将表单进行提交，在后端需要提交过来的表单数据修改数据库中的即可。

#### 3.3.1 回显数据

Controller 层：

```java
@GetMapping("/selectMenuById/{id}")
@Operation(summary = "回显单个菜单数据", description = "修改菜单时需要先回显数据")
public Result getMenuById(@PathVariable("id") Long id) {
    SysMenu sysMenu = sysMenuService.getMenuById(id);
    return Result.build(sysMenu, ResultCodeEnum.SUCCESS);
}
```

Service 层：

```java
@Override
public SysMenu getMenuById(Long id) {
    return getById(id);
}
```

****
#### 3.3.2 修改菜单

Controller 层：

修改菜单则需要新建一个实体类来封装请求参数，因为前端在修改菜单时有些数据并不会进行修改，例如 parentId 和 children 字段，因此完全可以用一个更简单的实体类来接收数据：

```java
@Data
@Schema(description = "封装系统菜单请求参数实体类")
public class SysMenuDto {

    @Schema(description = "菜单 id")
    private Long id;

    @Schema(description = "节点标题")
    private String title;

    @Schema(description = "组件名称")
    private String component;

    @Schema(description = "排序值")
    private Integer sortValue;

    @Schema(description = "状态(0:禁止,1:正常)")
    private Integer status;

}
```

```java
@PutMapping("/updateMenu")
@Operation(summary = "修改菜单", description = "前端表单填写数据后，后端直接进行更新数据库操作")
public Result updateSysMenu(@RequestBody SysMenuDto sysMenuDto) {
    sysMenuService.updateSysMenu(sysMenuDto);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

Service 层：

MyBatisPlus 的 updateById(id) 方法只会更新那些非空字段，如果字段为空，就不会进行更新，而是保持原有的数据，
前提是实体类字段没被特殊配置过 @TableField(updateStrategy = FieldStrategy.IGNORED)，当然默认值就是 FieldStrategy.NOT_NULL，就是非空才更新。
也就是说即使不再字段上标志该注解也能使用。

```java
@Override
public void updateSysMenu(SysMenuDto sysMenudto) {
    SysMenu sysMenu = new SysMenu();
    BeanUtils.copyProperties(sysMenudto, sysMenu);
    updateById(sysMenu);
}
```

****
### 3.4 删除菜单

当点击删除按钮的时候此时需要弹出一个提示框，询问是否需要删除数据？如果用户点击是，那么此时向后端发送请求传递 id 参数，后端接收 id 参数进行逻辑删除。

Controller 层：

```java
@DeleteMapping("/deleteMenu/{id}")
@Operation(summary = "删除菜单", description = "前端点击删除按钮后，后端接收到该菜单的 id 后对数据库进行修改")
public Result deleteSysMenu(@PathVariable("id") Long id) {
    sysMenuService.deleteSysMenu(id);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

Service 层：

这里删除的设计是不允许删除具有子节点的菜单，因为这样可以避免一次性丢失太多数据。而这个条件的判断则是查询数据库中以该菜单 id 为 parentId 的数据的条数，
如果大于 0，证明存在子节点那就不能进行删除。当然还有另一种判断方法，就是通过该 id 查询数据库中的数据获取到该子节点对象，如果不为空，那么就证明有子节点，
但这种操作性能不够好，因为可能涉及批量查询的操作，并且还要创建一个对象来接收，所以还是采用判断记录条数的方法。

```java
@Override
public void deleteSysMenu(Long id) {
    long count = count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id));
    // 如果当前要删除的菜单有子菜单，那么就不能进行删除
    if (count > 0) {
        throw new RuntimeException(ResultCodeEnum.NODE_ERROR.getMessage());
    }
    removeById(id);
}
```

****
### 3.5 分配菜单

在角色列表页面，当用户点击分配菜单按钮的时候，此时就会弹出一个对话框，在该对话框中会将系统中所涉及到的所有的菜单都展示出来。用户选择对应的菜单以后，点击提交按钮，
此时请求后端接口，后端将选中的菜单数据保存到 sys_role_menu 表中。前端请求后端接口的时候需要将角色的 id 和用户所选中的菜单 id 传递到后端，
后端则需要先根据角色的 id 从 sys_role_menu 表中删除该角色之前分配的菜单，然后再新增分配的菜单到 sys_role_menu 表。

数据库表对应实体类：

```java
@Data
@TableName("sys_role_menu")
public class SysRoleMenu extends BaseEntity {

    private Long roleId;       // 角色id
    private Long menuId;       // 菜单id

}
```

****
#### 3.5.1 回显数据

在给某个角色分配菜单时需要先展示该角色上次被分配的那些菜单数据，而后端只需要返回当前角色关联的菜单的 id 即可，前端获取到这些菜单 id 会自动在菜单列表上展示勾选状态。

Controller 层：

```java
@GetMapping("/getRoleHasMenu/{id}")
@Operation(summary = "展示当前操作角色当前选中的所有菜单")
public Result getRoleHasMenu(@PathVariable("id") Long roleId) {
    List<Long> menuIdList = sysRoleMenuService.getRoleHasMenu(roleId);
    return Result.build(menuIdList, ResultCodeEnum.SUCCESS);
}
```

Service 层：

```java
@Override
public List<Long> getRoleHasMenu(Long roleId) {
    List<SysRoleMenu> sysRoleMenuList = list(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
    List<Long> menuIdList = sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    return menuIdList;
}
```

****
#### 3.5.2 分配菜单

Controller 层：

前端会传递角色的 id 和选中的菜单的 id 集合，因此封装成一个实体类进行接收：

```java
@Data
@Schema(description = "封装前端分配菜单请求参数")
public class SysRoleMenuDto {

    @Schema(description = "角色 id")
    private Long roleId;

    @Schema(description = "菜单 id 集合")
    private List<Long> menuIdList;

}
```

```java
@PostMapping("/menuAllocation")
@Operation(summary = "给某个角色分配菜单", description = "先展示所有的菜单，当选中后将数据添加进数据库并删除旧数据")
public Result menuAllocation(@RequestBody SysRoleMenuDto sysRoleMenuDto) {
    sysRoleMenuService.menuAllocation(sysRoleMenuDto);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

Service 层：

与给用户分配角色类似，也需要在进行分配的时候删除原来该角色拥有的菜单，然后再插入新的菜单 id 到 sys_role_menu 表。

```java
@Override
@Transactional
public void menuAllocation(SysRoleMenuDto sysRoleMenuDto) {
    // 先删除数据库中该角色之前被分配的菜单
    Long roleId = sysRoleMenuDto.getRoleId();
    remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
    // 遍历 menuId，把 roleId 与它进行关联
    List<SysRoleMenu> sysRoleMenuList = new ArrayList<>();
    sysRoleMenuDto.getMenuIdList().forEach(menuId -> {
        SysRoleMenu sysRoleMenu = new SysRoleMenu();
        sysRoleMenu.setRoleId(roleId);
        sysRoleMenu.setMenuId(menuId);
        sysRoleMenuList.add(sysRoleMenu);
    });
    saveBatch(sysRoleMenuList);
}
```

```text
系统管理
 ├── 用户管理
 ├── 菜单管理
 └── 角色管理
```

- 用户只勾选了 用户管理：
  - 用户管理 = 全选 
  - 系统管理 = 半选

- 用户勾选了 用户管理 + 菜单管理：
  - 用户管理 = 全选 
  - 菜单管理 = 全选
  - 系统管理 = 半选

- 用户勾选了 用户管理 + 菜单管理 + 角色管理：
  - 用户管理 = 全选
  - 菜单管理 = 全选 
  - 角色管理 = 全选 
  - 系统管理 = 全选

树控件本身支持 “父节点勾选 -> 子节点全选”，勾选结果直接就是叶子节点，因此后端可以只存叶子节点到数据库，而数据回显时，前端树控件根据叶子节点自动计算父节点半选状态。

****
### 3.6 动态菜单

当前获取到的菜单列表是所有菜单，但这些菜单数据应该根据当前登录的用户动态展示在左侧，因此需要封装一个对象用来展示当前登录用户的菜单数据：

#### 3.6.1 动态展示 List<SysMenu>

先返回 List<SysMenu> 作为测试，看功能是否能正常返回结果，完成后再编写方法将 List<SysMenu> 转为 List<SysMenuVo>。

Controller 层：

```java
@GetMapping("/usableMenuWithSySMenu/{id}")
@Operation(summary = "展示某个角色可以使用的菜单(返回值为 SysMenu 集合)", description = "不同角色有不同的功能，因此他们能操控的菜单也不同，需要限制查询条件")
public Result getUsableMenuWithSySMenu(@PathVariable("id") Long roleId) {
    List<SysMenu> usableMenuList = sysRoleMenuService.getUsableMenuWithSySMenu(roleId);
    return Result.build(usableMenuList, ResultCodeEnum.SUCCESS);
}
```

Service 层：

首先需要根据当前的角色 id 查询 sys_role_menu 表获取到关联的所有叶子节点菜单 id（因为前端只会传递叶子节点），然后根据这些菜单 id 查询书对应的菜单实体，
并让它们根据自己的 parentId 进行分组，这样就可以简单的模拟一个节点下的多个子节点的情况。而本接口的主要目的就是通过这些叶子节点菜单构建出一个完整的树形结构，
也就是从叶子节点逆推出一棵树。所以需要先判断这些叶子节点是否为单独的一棵树，也就是它们没有父也没有子的情况，这种情况下则可以直接让这些叶子节点作为一个完整的 SysMenu 实体。
其余的情况就需要调用递归方法来逆向构建了。当然，每当从存储这些叶子节点的 Map 集合中拿取数据时，都要把这些数据删掉，这样就可以让它的长度作为停止递归的条件。

```java
@Override
public List<SysMenu> getUsableMenuWithSySMenu(Long roleId) {
    // 查询 sys_role_menu，获取该角色所有的菜单 id
    List<Long> roleMenuIdList = list(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId)).stream().map(SysRoleMenu::getMenuId).toList();
    List<SysMenu> sysMenuList = sysMenuService.list(new LambdaQueryWrapper<SysMenu>().in(SysMenu::getId, roleMenuIdList));
    Map<Long, List<SysMenu>> sameParentIdMenu = sysMenuList.stream().collect(Collectors.groupingBy(SysMenu::getParentId));
    System.out.println("getUsableMenuWithSySMenu's sameParentIdMenu：" + sameParentIdMenu);
    List<SysMenu> usableMenuList = new ArrayList<>();
    // 如果这些子节点的 parentId 为 0，证明这些叶子节点就是一级节点，可以作为一级父节点
    if (sameParentIdMenu.containsKey(0L)) {
        usableMenuList.addAll(sameParentIdMenu.get(0L));
        // 删除一级节点
        sameParentIdMenu.remove(0L);
    }
    List<SysMenu> allMenu = sysMenuService.getAllMenu();
    usableMenuList.addAll(getParentMenu(allMenu, sameParentIdMenu));
    return usableMenuList;
}
```

这个递归方法以整个菜单和叶子节点为传递参数进行的，从完整的菜单结构开始向下查找它的子节点是否为前端传递来的子节点。因为上面封装 Map 集合时用的是当前叶子节点的 parentId 作为 key 的，
所以可以用它来判断当前遍历的节点的 id 是否为叶子节点的 parentId，如果是的话，那证明该 key 对应的 value 就是当前遍历的节点的孩子节点，
当然这种情况是当前节点恰好是叶子节点的上一层节点才行，如果不是的话，还需要继续向下遍历，因此遍历一个节点的时候，还需要判断它有没有孩子节点，如果有孩子节点，
那么就再判断该孩子节点是否为这些叶子节点的父节点，以此进行递归操作。在进入递归方法的一开始就会创建一个集合，这个集合用来存放找到了叶子节点的那个节点，以此类推，
最终存放的就是封装好叶子节点的一级节点。当然，在遍历这些节点的时候还需要创建一个集合，它用来存放满足条件的叶子节点，如果该集合为空，证明当前节点不存在前端传递来的叶子节点，
那么就不需要将该节点返回给前端，也就是不需要把该节点放入递归方法初始时创建的那个集合。

```java
public List<SysMenu> getParentMenu(List<SysMenu> parentMenuList, Map<Long, List<SysMenu>> sameParentIdMenu) {
    List<SysMenu> newSysMenuList = new ArrayList<>(); // 新当前节点集合
    // 新当前节点的孩子节点集合
    for (SysMenu sysMenu : parentMenuList) {
        List<SysMenu> newChildSysMenuList = new ArrayList<>();
        if (sameParentIdMenu.isEmpty()) {
            return newSysMenuList;
        }
        SysMenu newSysMenu = new SysMenu();
        // 拷贝当前节点主要是为了给新节点赋值新的孩子节点
        BeanUtils.copyProperties(sysMenu, newSysMenu);
        Long key = sysMenu.getId();
        // 如果 map 集合中包含父节点的 Id，那么证明 value 就是这些父节点的孩子节点
        if (sameParentIdMenu.containsKey(key)) {
            newChildSysMenuList.addAll(sameParentIdMenu.get(key));
            sameParentIdMenu.remove(key);
        }
        // 判断当前节点是否有孩子节点
        if (sysMenu.getChildren() != null && !sysMenu.getChildren().isEmpty()) {
            // 把当前节点的孩子节点传递过去，判断下一层的节点的 id 是否为叶子节点的 parentId
            List<SysMenu> childMenuList = getParentMenu(sysMenu.getChildren(), sameParentIdMenu);
            if (childMenuList != null && !childMenuList.isEmpty()) {
                newChildSysMenuList.addAll(childMenuList);
            }
        } else {
            continue;
        }
        if (!newChildSysMenuList.isEmpty()) {
            // 给当前新节点添加孩子节点
            newSysMenu.setChildren(newChildSysMenuList);
            // 把当前新节点添加进集合，准备作为其它节点的孩子节点
            newSysMenuList.add(newSysMenu);
        }
    }
    return newSysMenuList;
}
```

****
#### 3.6.2 动态展示 List<SysMenuVo>

在上面的 Service 层的方法的最后将 List<SysMenu> 转换成 List<SysMenuVo>，具体的转换则变成成一个方法来处理。

```java
@Override
public List<SysMenuVo> getUsableMenu(Long roleId) {
    ...
    // 将 List<SysMenu> 转换成 List<SysMenuVo>
    return usableMenuList.stream().map(this::convertToVo).collect(Collectors.toList());
}
```

可以看到当前 SysMenuVo 的 children 字段的类型是 List<SysMenuVo>，而 SysMenu 的 children 字段的类型是 List<SysMenu>，也就是说这次的类型转换是一种嵌套类型的转换，
因此需要先递归到最底层，将叶子节点转换成 SysMenuVo，等某个节点的孩子节点全部转换成 SysMenuVo 后，也就是该节点的 children 字段变成 List<SysMenuVo> 后就可以结束递归了，
以此类推，最终所有的节点全部转换成功。

```java
@Data
@Schema(description = "系统菜单响应结果实体类")
public class SysMenuVo {

    @Schema(description = "系统菜单标题")
    private String title;

    @Schema(description = "系统菜单组件")
    private String component;

    @Schema(description = "系统菜单子菜单列表")
    private List<SysMenuVo> children;
    
}
```

```java
private SysMenuVo convertToVo(SysMenu sysMenu) {
    SysMenuVo sysMenuVo = new SysMenuVo();
    BeanUtils.copyProperties(sysMenu, sysMenuVo);
    // 子节点判空处理
    if (sysMenu.getChildren() != null && !sysMenu.getChildren().isEmpty()) {
        sysMenuVo.setChildren(
                sysMenu.getChildren().stream()
                        .map(this::convertToVo)
                        .collect(Collectors.toList())
        );
    } else {
        // 子节点为空则存入空集合，避免 stream 遇到 null 报错
        sysMenuVo.setChildren(new ArrayList<>());
    }
    return sysMenuVo;
}
```

****
#### 3.6.3 根据当前登录用户动态展示菜单

因为当前项目引入了 Minio，并且设置了 MinioConfig Bean（放在 common 包），也就是说只要有服务启动就会自动加载这个 Bean，但是认证服务目前不需要用到 Minio，
并且也没有设置 Minio 的相关配置文件，这就会导致启动认证服务的时候会报错：

```text
Caused by: java.lang.IllegalArgumentException: endpoint must not be null.
```

也就是说启动服务时会自动加载 Minio 配置类并尝试初始化 MinioClient，因此需要让认证服务启动时不会加载 Minio 的配置类才行。这里在 Minio 的配置类上加了一个注解，
使用了 @ConditionalOnProperty 注解的配置类只有当某个配置项满足条件时，这个配置类才会生效，而该配置项的名字是 spzx.minio.enabled，
它会尝试去读取配置文件中的 spzx.minio.enabled 这个属性，当该属性是 true 时才加载这个配置类，
而 matchIfMissing = false 表示如果配置文件里根本没有 spzx.minio.enabled 这个属性，就当作条件不成立，不会加载。这就解决了认证服务启动报错的问题，
不过其它服务要想使用的话，就得在配置文件中写上：spzx.minio.enabled=true。

```java
@ConditionalOnProperty(name = "spzx.minio.enabled", havingValue = "true", matchIfMissing = false)
public class MinioConfig {
}
```

****

因为用户注册登录时用的表和系统用户管理角色用的表不是同一个，但它们的数据是可以一样的，所以需要在用户注册时将当前用户添加进 sys_user 这张表，而这个操作需要调用权限管理模块，
因此要用到 OpenFeign 组件进行远程调用，引入相关依赖、确保服务注册到 nacos 并在启动类上添加 @EnableFeignClients：

```xml
<!-- openfeign -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<!-- loadbalancer -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

```java
@FeignClient(name = "spzx-authority-manage")
public interface SysUserFeignClient {
    @PostMapping("/sys_user/add")
    @Operation(summary = "在用户注册时新增系统用户", description = "当用户注册时在 sys_user 表中插入数据")
    Result add(@RequestBody SysUser sysUser);
}
```

接着就是在注册的方法中添加一下远程方法，将该用户的信息保存在 sys_user 表中：

```java
@Override
public Result<Boolean> register(UserRegisterDto userRegisterDto) {
    ...
    // 插入数据到 user_info 表的同时插入一条数据到 sys_user
    SysUser sysUser = new SysUser();
    BeanUtils.copyProperties(userInfo, sysUser);
    sysUserFeignClient.add(sysUser);
    return Result.build(true, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
}
```

Controller 层：

要根据当前登录用户来动态展示菜单，首先就要获取到用户才行，而在认证模块就做了登录功能，当用户登录成功会生成一个 session 并把 userId 存进 session 中，
而该 session 通过 SpringSession 存放在 Redis 中，可以通过获取到当前请求的 session 后拿到。

```java
@GetMapping("/getDynamicMenu")
@Operation(summary = "根据当前登录用户动态展示数据", description = "通过登录用户查找该用户对应的角色再动态展示菜单")
public Result getDynamicMenu(HttpServletRequest request) {
    List<SysMenuVo> dynamicMenu = sysRoleMenuService.getDynamicMenu(request);
    return Result.build(dynamicMenu, ResultCodeEnum.SUCCESS);
}
```

Service 层：

首先需要通过远程调用认证模块中的 UserController 获取到当前登录用户的 UserInfo 实体，通过它获取到当前用户的用户名和密码。

```java
@Service
@FeignClient(name = "spzx-auth-server")
public interface UserInfoFeignClient {
    @GetMapping("/auth-server/user/userInfo")
    @Operation(summary = "获取用户信息", description = "通过 session 拿到用户 id 再查询数据库")
    Result<UserInfo> getUserInfo(HttpServletRequest request);
}
```

为什么要获取到当前用户的用户名和密码呢？因为当前用户的信息是保存在两张表中的，一个 user_info，一个 sys_user，它们虽然有同一个有互信息，
但是该用户在这两张表中的 id 是不同的，而具有唯一性的字段就是用户的用户名和手机号，因为在注册时会对这两个字段的唯一性进行校验，校验通过才会保存进数据库，
所以也可以利用这两个字段去查找 sys_user 表，获取到该用户在 sys_user 表中的 id 是什么，然后再根据该 id 去查找 sys_user_role 表当前用户关联的角色是什么，
获取到 roleId 后就可以利用上面写的根据 roleId 获取到菜单的方法来动态展示菜单了。

```java
@Override
public List<SysMenuVo> getDynamicMenu(HttpServletRequest request) {
    // 根据 userId 查找该用户的用户名或手机号，然后再查询 sys_user 获取到 SysUserId
    Result<UserInfo> result = userInfoFeignClient.getUserInfo(request);
    UserInfo userInfo = result.getData();
    if (userInfo != null) {
        String username = userInfo.getUsername();
        String phone = userInfo.getPhone();
        SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username).eq(SysUser::getPhone, phone));
        if (sysUser != null) {
            // 获取到 SysUserId 再查询 sys_user_role 表获取到该用户关联的 roleId
            Long sysUserId = sysUser.getId();
            SysRoleUser sysRoleUser = roleUserService.getOne(new LambdaQueryWrapper<SysRoleUser>().eq(SysRoleUser::getRoleId, sysUserId));
            if (sysRoleUser != null) {
                Long roleId = sysRoleUser.getRoleId();
                return getUsableMenu(roleId);
            }
        }
    }
    return Collections.emptyList();
}
```

需要注意的是：浏览器在请求不同端口时，默认不会带上同一个 Session Cookie，如果 SESSION Cookie 不一样，那后端从 HttpSession 里取 userId 也会是空的，
虽然利用了 SpringSession 将数据存储在了 Redis 中，但是也需要配置了域名共享才能发挥作用，而配置域名就要设置网关让它进行负载均衡，因此目前的代码是不完整的。

****
### 4. 配置 nginx

#### 4.1 安装

1、创建宿主机目录，准备将 Nginx 容器里的配置文件、静态资源、日志，全部挂载到宿主机目录

```shell
# 创建宿主机目录
mkdir -p /root/spzxData/nginx/conf
mkdir -p /root/spzxData/nginx/conf.d
mkdir -p /root/spzxData/nginx/html
mkdir -p /root/spzxData/nginx/logs
```

2、拷贝 Nginx 容器的默认配置出来，相当于把容器里的 nginx.conf 和 default.conf 拷贝到宿主机，作为初始配置

```shell
# 拷贝主配置
docker run --rm nginx cat /etc/nginx/nginx.conf > /root/spzxData/nginx/conf/nginx.conf

# 如果有 default.conf 就拷贝出来
docker run --rm nginx cat /etc/nginx/conf.d/default.conf > /root/spzxData/nginx/conf.d/default.conf
```

3、运行时挂载目录

```shell
docker run -d \
  -p 80:80 \
  -v /root/spzxData/nginx/conf/nginx.conf:/etc/nginx/nginx.conf \
  -v /root/spzxData/nginx/conf.d:/etc/nginx/conf.d \
  -v /root/spzxData/nginx/html:/usr/share/nginx/html \
  -v /root/spzxData/nginx/logs:/var/log/nginx \
  --name nginx \
  nginx
```

****
#### 4.2 自定义域名搭配 nginx 反向代理

Hosts 文件可以把域名映射到某个 IP，通常在 C:\Windows\System32\drivers\etc\hosts，不过需要用管理员身份打开：

```text
192.168.149.101 auth.spzx.com
192.168.149.101 authority.spzx.com
```

例如像这样配置，将虚拟机的 ip 映射为 xxx.spzx.com，当访问 http://xxx.spzx.com 时就相当于访问虚拟机。此时再配置 nginx 的反向代理，
就可以通过 xxx.spzx.com 访问到本机的 88 端口，也就是配置的网关的端口，然后再由网关转发到各个服务：

```nginx
server {
    listen       80;
    listen  [::]:80;
    server_name  *.spzx.com;

    #access_log  /var/log/nginx/host.access.log  main;

    location / {
        # root   /usr/share/nginx/html;
        # index  index.html index.htm;
        proxy_set_header Host $host;
        proxy_pass http://192.168.149.1:88;
    }
}
```

注意：这里配置的 88 端口的地址为 http://192.168.149.1:88，也就是网关运行的机器的 IP + 端口号，Nginx 运行在 Linux 虚拟机的 Docker 容器内，
192.168.149.1 通常不是 Linux 虚拟机本身的 IP，而是 Windows 本机在虚拟机网络中的 “网关 IP” 或 “宿主机映射 IP”。因此这里写的是这个 ip，
而不是 192.168.149.101 或者 127.0.0.1，因为这两个 ip 都无法正确连接到 Windows。

****
### 5. 配置网关

通常前端项目和后端项目是运行在不同端口的，例如：

- 前端项目运行在 http://localhost:8001
- 网关（后端服务）运行在 http://localhost:88

它们端口不同，因此被视为不同源，浏览器会默认禁止这种跨域请求。但是在过去的项目中，因为使用了 nginx 所以没有出现这种问题，nginx 作为一种反向代理服务器，可以通过配置统一处理跨域请求，
让浏览器认为请求是同源的：

```nginx
server {
    listen       80;
    listen  [::]:80;
    server_name  *.spzx.com;
   
    location / {
        # root   /usr/share/nginx/html;
        # index  index.html index.htm;
        proxy_set_header Host $host;
        proxy_pass http://192.168.149.1:88;
    }
    # 反向代理 API 请求
    location /api/ {
        proxy_pass http://api-server:8080/;  # 转发到真实后端
    }
}
```

而当前的项目并没有使用 nginx，所以必须手动开启全局跨域配置，也就是编写一个配置文件，允许哪些来源、哪些请求头、哪些请求方法可以被放行，在网关中编写：

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 配置跨域
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*"); // 允许前端发送的请求
        corsConfiguration.setAllowCredentials(true); // 允许携带 cookie 跨域
        source.registerCorsConfiguration("/**", corsConfiguration); // 放行所有路径
        return new CorsWebFilter(source);
    }
}
```

需要注意的是：并不是所有的非同源请求都会被拦截，跨域请求如果使用了：

- GET、HEAD 或 POST
- POST 的 Content-Type 是：
- application/x-www-form-urlencoded
- multipart/form-data text/plain
- 没有自定义请求头（如 Authorization、Token）

这种情况下，浏览器不会先发 OPTIONS 请求，而是直接发目标请求。其余的请求则会先发一个 OPTIONS 请求（称为预检请求 preflight），然后由服务端返回是否允许跨域，
若允许，浏览器才会真正发起实际请求（比如 POST）。所以在 gulimall_gateway 中配置 CorsConfig 就是用来处理 OPTIONS 请求的。

```text
Request URL http://localhost:88/api/sys/login
Request Method OPTIONS
Status Code 200 OK
Remote Address [::1]:88
Referrer Policy strict-origin-when-cross-origin
```

因为所有的请求都会先经过网关，所以优先在网关配置全局 CORS 处理。

****
### 6. 测试根据登录用户自动展示菜单

再测试时发现控制台报错：

```java
feign.codec.EncodeException: Could not write request: no suitable HttpMessageConverter found for request type [org.apache.catalina.connector.RequestFacade]
Caused by: java.lang.IllegalArgumentException: No converter found for return value of type: class org.apache.catalina.connector.RequestFacade
Caused by: java.lang.IllegalStateException: Cannot serialize object of type org.apache.catalina.connector.RequestFacade
```

它丢出了三个错误，序列化失败、消息转换器找不到和 Feign 编码错误，而导致这些错误的原因就是之前写的那个远程查询当前用户信息的代码：

```java
@Service
@FeignClient(name = "spzx-auth-server")
public interface UserInfoFeignClient {
    @GetMapping("/auth-server/user/userInfo")
    @Operation(summary = "根据用户 id 获取用户信息")
    Result<UserInfo> getUserInfo(HttpServlet request);
}
```

在远程调用时直接把 HttpServlet 作为请求参数传递过去了，但 Java 是不允许这么做的，HttpServletRequest 包含连接、会话等本地状态信息，它与当前请求线程绑定，
无法在远程调用中传输，所以才会报错导致根本查不到用户信息。那就只能通过 session 获取到当前 userId 后再传递 userId 查询 userInfo 了：

```java
@Service
@FeignClient(name = "spzx-auth-server")
public interface UserInfoFeignClient {
    @GetMapping("/auth-server/user/userInfo/{id}")
    @Operation(summary = "根据用户 id 获取用户信息")
    Result<UserInfo> getUserInfoById(@PathVariable("id") Long id);
}
```

```java
@GetMapping("/userInfo/{id}")
@Operation(summary = "根据用户 id 获取用户信息")
public Result<UserInfo> getUserInfoById(@PathVariable("id") Long id) {
    UserInfo userInfo = userService.getUserInfoById(id);
    if (userInfo != null) {
        return Result.build(userInfo, ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
    } else {
        return Result.build(null, ResultCodeEnum.LOGIN_AUTH.getCode(), ResultCodeEnum.LOGIN_AUTH.getMessage());
    }
}
```

通过访问 http://auth.spzx.com/doc.html 和 http://authority.spzx.com/doc.html 进入两个服务的接口文档，
现在从这两个接口文档中调试的接口发送的请求不再是 localhost:11000/... 的形式了，而是 authority.spzx.com/...，此时搭配上 SpringSession 的配置：

```java
@Bean
public CookieSerializer cookieSerializer() {
    DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
    defaultCookieSerializer.setCookieName("SPZX-SESSION"); // 修改默认的 cookie 名
    // 配置 domain 解决不同服务之间的 cookie 丢失问题
    defaultCookieSerializer.setDomainName("spzx.com");
    return defaultCookieSerializer;
}
```

就能解决不同端口之间 session 无法共享的问题。但是经过 debug 调试发现，该远程调用的请求会被拦截器拦截，并在控制台打印 "未登录"，说明此时的 session 并没有共享过去，
通过查看浏览器的请求，发现 session 是空的，这就是之前记录的远程调用丢失请求头的问题，因为使用远程调用时，Spring 会创建一个 Feign 的代理对象来调用远程服务的方法，
而此时它就是一个新的 HTTP 请求，自然不会携带当前保存在 Redis 中的 session 数据，为了解决这个问题，可以提供一个配置类：

```java
@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            public void apply(RequestTemplate requestTemplate) {
                // 1. 使用 RequestContextHolder 拿到刚进来的请求数据
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = requestAttributes.getRequest();
                // 2. 同步请求头信息
                String cookie = request.getHeader("Cookie");
                // 3. 给新请求同步老请求的 cookie
                requestTemplate.header("Cookie", cookie);
            }
        };
    }
}
```

先通过认证服务完成登录生成 session，接着再使用权限管理服务文档调用动态展示菜单的接口，成功得到数据：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "title": "test",
      "component": "test",
      "children": []
    },
    {
      "title": "系统管理",
      "component": "system",
      "children": [
        {
          "title": "菜单管理",
          "component": "sysMenu",
          "children": []
        },
        {
          "title": "用户管理",
          "component": "sysUser",
          "children": [
            {
              "title": "测试菜单",
              "component": "哈哈哈，我是用户管理的子菜单，系统管理的三级菜单",
              "children": []
            }
          ]
        },
        {
          "title": "角色管理",
          "component": "sysRole",
          "children": [
            {
              "title": "我是角色管理的子菜单，系统管理的三级菜单",
              "component": null,
              "children": []
            }
          ]
        }
      ]
    },
    {
      "title": "商品管理",
      "component": "product",
      "children": [
        {
          "title": "商品规格",
          "component": "productSpec",
          "children": []
        },
        {
          "title": "商品列表",
          "component": "product",
          "children": []
        }
      ]
    },
    {
      "title": "会员管理",
      "component": "users",
      "children": [
        {
          "title": "会员列表",
          "component": "userInfo",
          "children": []
        }
      ]
    },
    {
      "title": "订单管理",
      "component": "order",
      "children": [
        {
          "title": "订单列表",
          "component": "orderInfo",
          "children": []
        },
        {
          "title": "订单统计",
          "component": "orderStatistics",
          "children": []
        }
      ]
    },
    {
      "title": "8",
      "component": "8",
      "children": [
        {
          "title": "90",
          "component": "7",
          "children": [
            {
              "title": "9999",
              "component": "88",
              "children": []
            }
          ]
        }
      ]
    }
  ]
}
```

****
# 四、分类管理

分类管理就是对商品的分类数据进行维护。常见的分类数据：电脑办公、玩具乐器、家居家装、汽车用品...分类数据所对应的表结构如下所示：

| 名称       | 类型      | 长度 | 小数点 | 不是 null | 虚拟 | 键 | 注释                |
|------------|-----------|------|--------|-----------|------|----|-------------------|
| id         | bigint    |      |        | ✔️         |      | 🔑 1 | 分类id              |
| name       | varchar   | 50   |        |           |      |    | 分类名称              |
| image_url  | varchar   | 200  |        |           |      |    | 图片 URL            |
| parent_id  | bigint    |      |        |           |      |    | 父分类id             |
| status     | tinyint   |      |        |           |      |    | 是否显示(0-不显示，1显示)   |
| order_num  | int       |      |        |           |      |    | 排序                |
| create_time| timestamp |      |        | ✔️         |      |    | 创建时间              |
| update_time| timestamp |      |        | ✔️         |      |    | 更新时间              |
| is_deleted | tinyint   |      |        | ✔️         |      |    | 删除标记 (0:不可用 1:可用) |

需要注意的是：分类数据是具有层级结构的，因此在进行数据展示的时候也可以考虑使用树形结构进行展示，整体与显示菜单列表类似。

****
## 1. 查询商品三级分类

根据如上数据表结构来看，分类表其实就是对所有的商品进行分类管理，每个商品实体都有一个 parentId 来指向父亲，而一级分类数据的 parentId 为 0，
通过以 parentId 作为查询条件递归查询数据即可获取到完整的三级分类结构。与之对应的实体类如下，需要注意的是，如果需要存入缓存中，那么就要让该实体类实现序列化，
否则会因为无法序列化而无法存入 Redis 中。

```java
@Data
@TableName("category")
@Schema(description = "分类实体类")
public class Category extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

	@Schema(description = "分类名称")
	private String name;

	@Schema(description = "分类图片url")
	private String imageUrl;

	@Schema(description = "父节点id")
	private Long parentId;

	@Schema(description = "分类状态: 是否显示[0-不显示，1显示]")
	private Integer status;

	@Schema(description = "排序字段")
	private Integer orderNum;

    @TableField(exist = false)
	@Schema(description = "子节点List集合")
	private List<Category> children;

}
```

Controller 层：

```java
@GetMapping("/getCategory")
@Operation(summary = "获取商品三级分类")
public Result getCategory() {
    List<Category> categoryList = categoryService.getCategory();
    return Result.build(categoryList, ResultCodeEnum.SUCCESS);
}
```

Service 层：

先从数据库中查出所有的分类数据，让它们以 parentId 进行分组，得到一个 Map 集合，可以通过一个 key 代表 parentId，value 则代表该 id 的孩子节点。

```java
@Override
@Cacheable(value = "category", key = "'Level1Categories'")
public List<Category> getCategory() {
    Map<Long, List<Category>> allCategories = list().stream().collect(Collectors.groupingBy(Category::getParentId));
    return buildTreeCategory(0L, allCategories);
}
```

先传入 0L，也就是从一级节点开始查找，然后找它的孩子节点，再找孩子节点的孩子节点，以此类推，递归地进行查找，直到遍历完从数据库中查出的所有分类数据。

```java
private List<Category> buildTreeCategory(Long parentId, Map<Long, List<Category>> allCategories) {
    List<Category> categoryList = new ArrayList<>();
    // 获取 parentId 下的子节点
    List<Category> childCategoryList = allCategories.get(parentId);
    if (childCategoryList != null && !childCategoryList.isEmpty()) {
        for (Category childCategory : childCategoryList) {
            // 查找是否有以本节点的 id 作为 parentId 的节点
            List<Category> nextChildCategoryList = buildTreeCategory(childCategory.getId(), allCategories);
            childCategory.setChildren(nextChildCategoryList);
            categoryList.add(childCategory);
        }
    }
    return categoryList;
}
```

本方法使用了 SpringCache 将数据存入 Redis 中，因此需要配置 SpringCache 的相关配置：

```yaml
spring:
  cache:
    type: redis

  data:
    redis:
      host: 192.168.149.101
      port: 6379
```

当然，还需要在启动类上添加 @EnableCaching 注解，确保 SpringCache 生效，如果需要确保存入 Redis 的序列化格式为 Json，那么也需要配置一下配置类：

```java
@Bean
public RedisCacheConfiguration redisCacheConfiguration() {
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
    config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
    config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

    // 让配置文件中的数据生效
    CacheProperties.Redis redisProperties = cacheProperties.getRedis();
    if (redisProperties.getTimeToLive() != null) {
        config = config.entryTtl(redisProperties.getTimeToLive());
    }

    if (redisProperties.getKeyPrefix() != null) {
        config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
    }

    if (!redisProperties.isCacheNullValues()) {
        config = config.disableCachingNullValues();
    }

    if (!redisProperties.isUseKeyPrefix()) {
        config = config.disableKeyPrefix();
    }
    return config;
}
```

****
### 2. EasyExcel

后台管理系统是管理、处理企业业务数据的重要工具，在这样的系统中，数据的导入和导出功能是非常重要的，其主要意义包括以下几个方面：

1. 提高数据操作效率：手动逐条添加或修改数据不仅费时费力，而且容易出错，此时就可以将大量数据从 Excel 等表格软件中导入到系统中时，通过数据导入功能，可以直接将表格中的数据批量导入到系统中，提高了数据操作的效率。 
2. 实现数据备份与迁移：通过数据导出功能，管理员可以将系统中的数据导出为 Excel 或其他格式的文件，以实现数据备份，避免数据丢失。同时，也可以将导出的数据文件用于数据迁移或其他用途。
3. 方便企业内部协作：不同部门可能会使用不同的系统或工具进行数据处理，在这种情况下，通过数据导入和导出功能，可以方便地转换和共享数据，促进企业内部协作。

EasyExcel 的使用：

1、添加依赖

```xml
<!--excel-->
<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>easyexcel</artifactId>
  <version>3.3.3</version>
</dependency>
```

2、定义一个实体类来封装每一行的数据：

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryExcelVo {
    // index = 0 代表 Excel 第 1 列
	@ExcelProperty(value = "名称" ,index = 0)
	private String name;

	@ExcelProperty(value = "图片url" ,index = 1)
	private String imageUrl ;

	@ExcelProperty(value = "上级id" ,index = 2)
	private Long parentId;

	@ExcelProperty(value = "状态" ,index = 3)
	private Integer status;

	@ExcelProperty(value = "排序" ,index = 4)
	private Integer orderNum;

}
```

3、定义一个监听器，监听解析到的数据

```java
public class ExcelListener<T> extends AnalysisEventListener<T> {

    private List<T> list = new ArrayList<>();

    // 从第二行开始读取 excel 内容，把每行数据封装到 T 对象中
    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        list.add(t);
    }

    public List<T> getList() {
        return list;
    }

    // excel解析完毕以后需要执行的代码
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
```

4、编写测试方法

```java
public class ExcelTest {
    public static void main(String[] args) {
        // read();
        write();
    }

    public static void read() {
        // 1. 定义读取 Excel 文件的位置
        String fileName = "E://01.xlsx";
        // 2. 调用方法
        ExcelListener<CategoryExcelVo> excelListener = new ExcelListener();
        EasyExcel.read(fileName, CategoryExcelVo.class, excelListener).sheet().doRead();
        List<CategoryExcelVo> list = excelListener.getList();
        System.out.println(list);
    }

    public static void write() {
        List<CategoryExcelVo> list = new ArrayList<>();
        list.add(new CategoryExcelVo("数码办公", "", 0L, 1, 1));
        list.add(new CategoryExcelVo("华为手机", "", 1L, 1, 2));
        EasyExcel.write("E://02.xlsx", CategoryExcelVo.class).sheet("分类数据").doWrite(list);
    }
}
```

****
#### 2.1 数据导出功能

当用户点击导出按钮的时候，此时将数据库中的所有的分类的数据导出到一个 Excel 文件中，也就是对应的写操作。

Controller 层：

在 EasyExcel 导出数据时，在 Controller 层需要接收 HttpServletResponse，这样就可以直接控制 HTTP 响应，实现文件下载功能，这是文件导出的标准做法。
并且必须设置返回类型为 void 而不是 Result，因为文件下载响应是二进制的 Excel 数据，不能再用 JSON 格式的 Result 包装。

```java
@GetMapping(value = "/exportData")
@Operation(summary = "导出商品三级分类 Excel")
public void exportData(HttpServletResponse response) {
    // 1. 设置响应头信息和其它信息
    // 告诉浏览器这是 Excel 文件
    response.setContentType("application/vnd.ms-excel");
    response.setCharacterEncoding("utf-8");
    // 这里 URLEncoder.encode 可以防止中文乱码 当然和 EasyExcel 没有关系
    String fileName = URLEncoder.encode("分类数据", StandardCharsets.UTF_8);
    // 告诉浏览器以下载方式处理，而不是直接打开
    response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
    categoryService.exportData(response);
}
```

Service 层：

Service 层就是接收到已经处理好的 HttpServletResponse，直接将数据库表中的数据转换成 Excel 并将数据发送给浏览器。

```java
@Override
public void exportData(HttpServletResponse response) {
    // 2. 查询数据库中的数据
    List<Category> categoryList = list();
    List<CategoryExcelVo> categoryExcelVoList = new ArrayList<>(categoryList.size());
    // 将从数据库中查询到的 Category 对象转换成 CategoryExcelVo 对象
    for (Category category : categoryList) {
        CategoryExcelVo categoryExcelVo = new CategoryExcelVo();
        BeanUtils.copyProperties(category, categoryExcelVo);
        categoryExcelVoList.add(categoryExcelVo);
    }
    // 3. 写出数据到浏览器端
    // 获取 HTTP 响应输出流，数据直接发送给客户端
    EasyExcel.write(response.getOutputStream(), CategoryExcelVo.class).sheet("分类数据").doWrite(categoryExcelVoList);
}
```

****
#### 2.2 导入功能

当用户点击导入按钮的时候，此时会弹出一个对话框，让用户选择要导入的 Excel 文件，选择后将文件上传到服务端，服务端通过 EasyExcel 解析文件的内容，
然后将解析的结果存储到 category 表中。

Controller 层：

导入 Excel 的功能就不需要使用 HttpServletResponse 了，但是要用一个 MultipartFile 来接收文件，之前及路过，这个类可以自动封装好 HTTP　请求为一个文件，
当　Service　层接收到这个封装好的文件后，直接对里面的内容进行处理即可。

```java
@PostMapping("importData")
@Operation(summary = "导入商品三级分类 Excel")
public Result importData(MultipartFile file) {
    categoryService.importData(file);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

Service 层：

Service　层主要就是调用读取方法触发监听器，而创建监听器时需要传入当前　Service 层，因为使用的是　ＭｙｂａｔｉｓＰｌｕｓ，所以可以利用它来进行插入数据到数据库。

```java
@Override
public void importData(MultipartFile file) {
    try {
        // 传入 CategoryService 到监听器
        ExcelListener<CategoryExcelVo> excelListener = new ExcelListener<>(this);
        // 调用 EasyExcel 读取
        //　把文件当作输入流交给 EasyExcel
        EasyExcel.read(file.getInputStream(),
                CategoryExcelVo.class,
                excelListener).sheet().doRead();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

监听器才是主要处理　Excel　的地方，每解析一行数据，就把当前的对象放进集合，达到设置的最大记录时就触发存入数据库的操作。

```java
public class ExcelListener<T> extends AnalysisEventListener<T> {
    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    /**
     * 缓存的数据
     */
    private List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    //获取 mapper 对象
    private CategoryService categoryService;

    public ExcelListener(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 每解析一行数据就会调用一次该方法
    @Override
    public void invoke(T data, AnalysisContext analysisContext) {
        cachedDataList.add(data);
        // 达到 BATCH_COUNT 了，需要去存储一次数据库，防止数据几万条数据在内存，容易 OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // excel　解析完毕以后需要执行的代码
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
    }

    private void saveData() {
        // 将 CategoryExcelVo 转换为 Category 实体
        List<Category> categoryList = cachedDataList.stream()
                .map(vo -> {
                    Category category = new Category();
                    BeanUtils.copyProperties(vo, category);
                    return category;
                })
                .collect(Collectors.toList());
        categoryService.saveBatch(categoryList);
    }
}
```

****
## 3. 封装 Minio 工具类

因为设置成工具类了，那么保存文件的路径就不能设置成固定的，要根据每个服务自己要上传的东西而定，例如品牌管理这里上传的就是以 logo 为父目录的情况。

```java
@Data
@Component
@ConfigurationProperties(prefix = "spzx.minio")
public class MinioProperties {
    private String accessKey;
    private String secretKey;
    private String endpointUrl;
    private String bucketName;
    private String region;
}
```

```yaml
spzx:
  minio:
    access-key: admin
    secret-key: admin123
    endpoint-url: http://192.168.149.101:9001
    bucket-name: spzx-bucket
    enabled: true # 加载 Minio 配置文件
    region: logo
```

```java
@Component
public class MinioUtil {

    // 配置 log
    private static final Logger log = Logger.getLogger(MinioUtil.class.getName());

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final Set<String> ALLOWED = Set.of("image/png", "image/jpeg", "image/jpg", "image/gif");

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioProperties minioProperties;

    // 上传图片文件
    public String upload(MultipartFile file) {}
    // 获取文件扩展名
    public String getExt(MultipartFile file) {}
    // 将文件从临时目录拷贝到使用中目录
    public String copyMinioTempToCurrent(String currentUrl) {}
    // 获取 Minio 对象名
    public String getMinioObjectName(String url) {}
    // 批量获取 Minio 对象名
    public List<String> getBatchMinioObjectName(List<String> urls) {}
    // 获取指定目录下的所有文件
    public Iterable<Result<Item>> getResults(String prefix) {}
    // 删除单个使用中目录下的 Minio 文件
    public void deleteMinioOldFile(String url) {}
    // 批量删除指定目录下的 Minion 文件
    public <T> void deleteBatchMinioFile(T t) {}
}
```

在原来编写的方法基础上新增了多个方法，例如批量修改 URL 为 Minio 文件对象名和批量删除 Minio 文件。

1、批量修改 URL　直接利用 stream 批量处理即可

```java
@NotNull
public List<String> getBatchMinioObjectName(List<String> urls) {
    return urls.stream().map(url -> url.substring(
            url.indexOf(minioProperties.getBucketName())
                    + minioProperties.getBucketName().length() + 1 // 最后加 1 是用来去掉桶名后的斜杠 "/"
    )).collect(Collectors.toList());
}
```

2、获取指定目录下的所有文件

通过接收一个字符串并和当前服务设置的 region 进行拼接，就能作为 Minio 的某个具体文件路径的前缀路径了，例如 region 为 logo，传入的 prefix 为 /temp，
那么这个文件路劲前缀就是 logo/temp。

```java
/**
 * 获取指定目录下的所有文件
 * @param prefix 传递指定的前缀名（是哪个目录）
 */
public Iterable<Result<Item>> getResults(String prefix) {
    return minioClient.listObjects(
            ListObjectsArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .prefix(minioProperties.getRegion() + prefix)
                    .recursive(true) // 递归遍历子目录
                    .build()
    );
}
```

3、批量删除

批量删除 Minio 文件则可以使用 Minio 的批量删除 API，只不过在进行批量删除后必须遍历它的结果集。MinioClient.removeObjects(RemoveObjectsArgs) 接收一个 Iterable<DeleteObject>，
返回的是 Iterable<Result<DeleteError>>，这个返回结果就是用来接收批量删除操作中哪些文件删除失败了，不是删除成功的文件才返回，
而是每个可能失败的文件都会有一个 DeleteError，成功的文件不会返回错误。每个 Result<DeleteError> 都是懒加载的，必须调用 get() 才能触发实际的删除或拿到异常信息。
因此不能直接用 Iterable<Result<Item>> 去做 removeObjects，必须先把 Result<Item> 转成 DeleteObject 类型，当然可以选择不进行遍历结果，
但成功删除的文件不会返回任何信息。

```java
public void deleteBatchMinioFile(List<String> urls) {
    if (urls == null || urls.isEmpty()) {
        log.warning("批量删除文件列表为空");
        return;
    }
    // 批量处理 urls 为 Minio 的存储对象名
    List<DeleteObject> deleteObjectNames = getBatchMinioObjectName(urls).stream().map(DeleteObject::new).collect(Collectors.toList());
    // 调用批量删除
    Iterable<Result<DeleteError>> results = minioClient.removeObjects(
            RemoveObjectsArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .objects(deleteObjectNames)
                    .build()
    );
    // 必须遍历结果，否则删除可能不会执行
    int errorCount = 0;
    for (Result<DeleteError> result : results) {
        errorCount++;
        try {
            DeleteError error = result.get();
            // 打印错误信息
            log.log(Level.SEVERE, "MinIO 批量删除失败，文件：" + error.objectName() + "，错误：" + error.message());
        } catch (Exception e) {
            log.log(Level.SEVERE, "批量删除 MinIO 文件时发生异常", e);
        }
    }
    log.info("批量删除完成，共处理 " + (deleteObjectNames.size() - errorCount) + " 个文件");
}
```

不过当前批量删除存在两种情况，一种是传入完整的文件路径；另一种则是查询 Minio 指定目录下的所有文件，再把这些文件封装好进行删除。所以要对这两种情况进行区分，
但不管怎么样，最终都需要封装成 List<DeleteObject> 才能进行删除操作。

对上面的批量删除完整路径下的文件进行改造，传递的参数设置成泛型，让它可以接收多种类型的参数。根据上面记录的两种值的类型可以得出：一种为 List<String>；
一种为 Iterable<Result<Item>>；那么就可以从这两种类型着手，判断当前传入的参数是属于它们两中的哪种，那么就可以写为：

```java
if (t instanceof List<?>) {
    
} else if (t instanceof Iterable){
    
}
```

1) 当传递的参数类型为 List<String> 时

因为是批量删除操作，那么传入的参数肯定是由多条数据封装成一个对象的，所以可以遍历该参数中的每个元素，将它们强转成 String 类型，
接着调用 getMinioObjectName(String url)（根据完整路径获取 Minio 文件对象名），并把该 Minio 对象封装成 DeleteObject 对象，那么此时就可以把它放进 List<DeleteObject> 集合了。

2) 当传递的参数类型为 Iterable<Result<Item>>

同理，需要遍历该对象中的所有元素，接着对每一个元素进行强转，因为通过 Minio 的 listObjects(ListObjectsArgs) 这个 API 获取到的每个元素的类型为 Result<Item>，
因此可以把当前遍历的该泛型对象中的每个元素强转成 Result<Item>，再通过该元素（Result<Item>）获取到它的对象名并封装成 DeleteObject。

```java
/**
 * 批量删除 Minio 中的文件
 * @param t 传递值可以为 List<String>（完整路径集合）；或者为 Iterable<Result<Item>>（从 Minio 指定文件夹下遍历出的所有文件）
 * @param <T>
 */
public <T> void deleteBatchMinioFile(T t) {
    List<DeleteObject> deleteObjects = new ArrayList<>();
    if (t == null) {
        log.warning("批量删除文件列表为空");
        return;
    }
    // 如果传递来的是集合，那证明传来的是完整路径的文件 URL
    if (t instanceof List<?>) {
        for (Object obj : (List<?>) t) {
            if (obj instanceof String) {
                deleteObjects.add(new DeleteObject(getMinioObjectName((String) obj)));
            }
        }
    } else if (t instanceof Iterable) {
        for (Object obj : (Iterable<?>) t) {
            if (obj instanceof Result) {
                Result<Item> resultItem = (Result<Item>) obj;
                String objectName;
                try {
                    objectName = resultItem.get().objectName();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                deleteObjects.add(new DeleteObject(objectName));
            }
        }
    }
    if (!deleteObjects.isEmpty()) {
        // 调用批量删除
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(
                RemoveObjectsArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .objects(deleteObjects)
                        .build()
        );
        // 必须遍历结果，否则删除可能不会执行
        int errorCount = 0;
        for (Result<DeleteError> result : results) {
            errorCount++;
            try {
                DeleteError error = result.get();
                // 打印错误信息
                log.log(Level.SEVERE, "MinIO 批量删除失败，文件：" + error.objectName() + "，错误：" + error.message());
            } catch (Exception e) {
                log.log(Level.SEVERE, "批量删除 MinIO 文件时发生异常", e);
            }
        }
        log.info("批量删除完成，共处理 " + (deleteObjects.size() - errorCount) + " 个文件");
    } else {
        log.log(Level.SEVERE, "MinIO 文件为空，无需删除!");
    }
}
```

4、修改定时任务

同理，定时清理临时目录的那个定时任务也需要进行修改，最初是把它写在某个服务中的，不过后续可能有多个服务都会进行图片文件的上传，因此把它写进 common 服务是较为合理的，
同时给它添加一个分布式锁，当开启了多个不同端口的相同服务时，它们都会定时启动该任务，添加了分布式锁后可以确保只有一个线程会执行删除临时目录的操作（因为存在多个线程同时删除一个目录的情况）。

```java
@Service
@EnableScheduling
public class MinioSchedule {

    // 配置 log
    private static final Logger log = Logger.getLogger(MinioSchedule.class.getName());

    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private RedissonClient redissonClient;

    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadSeckillSkuLatest3Days() throws Exception {
        log.info("开始扫描 Minio 临时目录！");
        // 1. 获取分布式锁
        RLock rLock = redissonClient.getLock("minioDelete-lock");
        // 2. 加锁
        boolean isLock = rLock.tryLock(5, 10,TimeUnit.SECONDS); // 最多等待 5s，当锁持有时间 10 分钟自动释放
        if (!isLock) {
            log.info("其他实例正在执行 Minio 清理任务，本次任务跳过");
            return;
        }
        try {
            String tempPrefix = "/temp/";
            // 遍历获取指定目录下的所有文件
            Iterable<Result<Item>> results = minioUtil.getResults(tempPrefix);
            // 批量删除
            minioUtil.deleteBatchMinioOldFile(results);
        } finally {
            rLock.unlock();
        }
    }
}
```

****
## 4. 品牌管理

品牌数据所对应的表结构与实体类如下：

| 名称 | 类型 | 长度 | 小数点 | 不是 null | 虚拟 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| id | bigint |  |  | ✔️ |  | 🔑 1 | ID |
| name | varchar | 100 |  |  |  |  | 品牌名称 |
| logo | varchar | 255 |  |  |  |  | 品牌图标 |
| create_time | timestamp |  |  | ✔️ |  |  | 创建时间 |
| update_time | timestamp |  |  | ✔️ |  |  | 更新时间 |
| is_deleted | tinyint |  |  | ✔️ |  |  | 删除标记 (0:不可用 1:可用) |

```java
@Data
@TableName("brand")
@Schema(description = "品牌实体类")
public class Brand extends BaseEntity {

	@Schema(description = "品牌名称")
	private String name;

	@Schema(description = "品牌logo")
	private String logo;

}
```

品牌管理就是对商品的所涉及到的品牌数据进行维护，常见的品牌数据：小米、华为、海尔...也就是对这些数据进行基础的增删改查操作，当然，如果这些品牌有子品牌的存在，那就需要修改表结构了，
不过这里暂时不涉及。

****
### 4.1 查询品牌

查询品牌数据则是使用的分页查询，所以需要封装一个分页参数实体类，不过之前设置了一个分页查询基础实体类，因此只需要继承该类再增加一些需要的查询参数即可：

```java
@Data
@Schema(description = "分页查询品牌数据请求参数实体类")
public class BrandQueryDto extends QueryPageDto {

    @Schema(description = "品牌名称")
    private String name;

}
```

Controller 层：

```java
@PostMapping("/listBrandPage")
@Operation(summary = "分页查询品牌数据")
public Result listBrandPage(@RequestBody BrandQueryDto brandQueryDto) {
    PageResult<Brand> brandPageResult = brandService.listBrandPage(brandQueryDto);
    return Result.build(brandPageResult, ResultCodeEnum.SUCCESS);
}
```

Service 层：

```java
@Override
public PageResult<Brand> listBrandPage(BrandQueryDto brandQueryDto) {
    LambdaQueryWrapper<Brand> lambdaQueryWrapper = new LambdaQueryWrapper<>();
    if (brandQueryDto.getName() != null && !brandQueryDto.getName().isEmpty()) {
        lambdaQueryWrapper.eq(Brand::getName, brandQueryDto.getName());
    }
    Page<Brand> page = new Page<>(brandQueryDto.getPage(), brandQueryDto.getSize());
    Page<Brand> pageResult = page(page, lambdaQueryWrapper);
    return new PageResult<Brand>(pageResult.getTotal(), pageResult.getPages(), pageResult.getRecords());
}
```

****
### 4.2 新增品牌

Controller 层：

```java
@PostMapping("/addBrand")
@Operation(summary = "新增品牌数据")
public Result addBrand(@RequestBody BrandDto brandDto) {
    brandService.addBrand(brandDto);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

Service 层：

因为新增操作中包含上传品牌 logo 的操作，而上传品牌 logo 是一个单独的请求，只有点击后当前新增操作的请求中才会携带 logo 的 URL（临时目录下的文件 URL），
而最终需要将 Minio 的 use 目录下的 URL 保存到数据库，因此这里需要调用 MinioUtil 工具类的拷贝方法，该方法会将临时目录下的文件拷贝一份到 use 目录，
如果临时目录中的文件不存在则会返回 null，以此来判断用户是否点击上传按钮。

```java
@Override
public void addBrand(BrandDto brandDto) {
    // 检查传递来的 BrandDto 中是否包含 Logo URL
    String currentLogoUrl = brandDto.getLogo();
    // 将临时目录下的 logo 拷贝到 use 目录，如果返回为空，证明没有上传图片
    String newLogoUrl = minioUtil.copyMinioTempToCurrent(currentLogoUrl);
    if (currentLogoUrl != null && newLogoUrl != null) {
        brandDto.setLogo(newLogoUrl);
    }
    // 当 currentLogoUrl 为空时，说明没有进行上传 Logo 的操作，那么存入数据库时使用空的 URL　即可（前端未点击上传时即为空）
    Brand brand = new Brand();
    BeanUtils.copyProperties(brandDto, brand);
    save(brand);
}
```

****
### 4.3 修改品牌

Controller 层：

```java
@PutMapping("updateBrand")
@Operation(summary = "修改品牌信息")
public Result updateBrand(@RequestBody Brand brand) {
    brandService.updateBrand(brand);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

Service 层：

修改品牌操作中最重要的就是判断当前是否上传了新的 logo，如果上传了新的那么就需要将旧的删除。

```java
@Override
@Transactional
public void updateBrand(Brand brand) {
    String oldLogoUrl = getById(brand.getId()).getLogo();
    String currentLogoUrl = brand.getLogo();
    // 当传入的 Logo 不为空时，则需要判断是新增的头像，还是原始的头像
    if (currentLogoUrl != null) {
        // 只有 url 中包含临时路径 /temp 才会通过该方法返回修改为使用中路径 /use
        String newLogoUrl = minioUtil.copyMinioTempToCurrent(currentLogoUrl);
        // 如果不为空，证明更新了 Logo，那么就要查询数据库删除以前的存放在 /use 中的旧 Logo
        if (newLogoUrl != null && oldLogoUrl != null) {
            minioUtil.deleteMinioOldFile(oldLogoUrl);
            brand.setLogo(newLogoUrl);
        }
    }
    updateById(brand);
}
```

****
### 4.4 删除品牌

Controller 层：

```java
@DeleteMapping("/deleteBrand")
@Operation(summary = "删除品牌信息", description = "可以批量删除，也可以单个删除")
public Result deleteBrand(@RequestBody List<Long> ids) {
    brandService.deleteBrand(ids);
    return Result.build(null, ResultCodeEnum.SUCCESS);
}
```

Service 层：

删除品牌的同时也要删除存在 Minio 中的 logo 文件，这里是批量删除，所以也是批量删除 Minio 文件，但这里是不需要增加分布式锁的，因为每次删除操作都是删除指定的数据，
也就是说查出的每个元素完整文件 URL 都是不同的，因此就算是多个线程进行删除操作也不会导致误删操作的出现。

```java
@Override
@Transactional
public void deleteBrand(List<Long> ids) {
    List<Brand> deleteBrandList = list(new LambdaQueryWrapper<Brand>().in(Brand::getId, ids));
    List<String> logoList = deleteBrandList.stream().map(Brand::getLogo).collect(Collectors.toList());
    removeBatchByIds(ids);
    minioUtil.deleteBatchMinioFile(logoList);
}
```

****
## 5. 分类品牌管理

分类品牌管理就是将商品分类的数据和品牌的数据进行关联，分类数据和品牌数据之间的关系是多对多的关系，一个品牌可以有多种商品分类，而一个商品分类也可以由多个品牌同时拥有。

| 名称 | 类型 | 长度 | 小数点 | 不是 null | 虚拟 | 键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| id | bigint |  |  | ✔️ |  | 🔑 1 | ID |
| brand_id | bigint |  |  |  |  |  | 品牌ID |
| category_id | bigint |  |  |  |  |  | 分类ID |
| create_time | timestamp |  |  | ✔️ |  |  | 创建时间 |
| update_time | timestamp |  |  | ✔️ |  |  | 更新时间 |
| is_deleted | tinyint |  |  | ✔️ |  |  | 删除标记 (0:不可用 1:可用) |

```java
@Data
@TableName("category_brand")
@Schema(description = "分类品牌实体类")
public class CategoryBrand extends BaseEntity {

	@Schema(description = "品牌 id")
	private Long brandId;

	@Schema(description = "商品分类 id") 
	private Long categoryId;

}
```

****
### 5.1 列表查询

如果在搜索表单中选择了某一个品牌以及分类，那么此时就需要按照品牌 id 和分类 id 进行查询，所以它的查询条件有两个，分别是品牌和分类。不过这里是通过下拉列表的方式，
因此该操作会触发查询所有的请求，然后根据前端选中的数据传递 id。

```java
@Data
@Schema(description = "搜索条件实体类")
public class CategoryBrandDto extends QueryPageDto {

	@Schema(description = "品牌id")
	private Long brandId;

	@Schema(description = "分类id")
	private Long categoryId;

}
```

```java
@Override
@Cacheable(value = "allBrand", key = "'getAllBrand'")
public List<Brand> getAllBrand() {
    return list();
}
```

```java
@Override
@Cacheable(value = "allCategory", key = "'getAllCategory'")
public List<Category> getCategory() {
    Map<Long, List<Category>> allCategories = list().stream().collect(Collectors.groupingBy(Category::getParentId));
    return buildTreeCategory(0L, allCategories);
}
```

不过返回数据时不可能只展示它们 id，还得展示哪个品牌名关联哪个商品分类名，因此要在实体类上新增一些字段用来展示它们的名字，而这些字段肯定得通过对应 id 查询数据库。

```java
@Data
@TableName("category_brand")
@Schema(description = "分类品牌实体类")
public class CategoryBrand extends BaseEntity {

	@Schema(description = "品牌id")
	private Long brandId;

	@Schema(description = "分类id")
	private Long categoryId;

    @TableField(exist = false)
	@Schema(description = "分类名称")
	private String categoryName;

    @TableField(exist = false)
	@Schema(description = "品牌名称")
	private String brandName;

    @TableField(exist = false)
	@Schema(description = "品牌logo")
	private String logo;

}
```

Controller 层：

```java
@PostMapping("/listPage")
public Result findByPage(@RequestBody CategoryBrandDto CategoryBrandDto) {
    PageResult<CategoryBrand> categoryBrandPageResult = categoryBrandService.findByPage(CategoryBrandDto);
    return Result.build(categoryBrandPageResult, ResultCodeEnum.SUCCESS);
}
```

Service 层：

该查询总共分为四种情况：(1)同时指定商品分类和品牌；(2)什么都不指定；(3)只指定商品分类；(4)只指定品牌。因此得分类讨论。

1) 同时指定商品分类和品牌

这种情况只会查询出空数据或者一条数据，因为商品分类和品牌是互相对应的，两个条件则能确定唯一数据，因此只需要获取到 category_brand 表中的那条记录存在，
那么就可以通过 categoryId 和 brandId 查询它们的名字 logo 等数据。当然，因为最多一条数据，所以返回的分页数据可以固定写为 1。

2) 什么都不指定

当什么都不指定时，就是查询所有的关联数据，通过查询表获取所有的 id 并查询对应的表即可。

3) 只指定商品分类

这种情况就是确定唯一的 categoryId，那么就可能查询出多条不同 brandId 的数据，所以在遍历这些数据时还得让它们各自查询 brand 表获取 brand 的字段。

4) 只指定品牌

这种情况和上面的类似，只是条件调转了而已。

```java
@Override
public PageResult<CategoryBrand> findByPage(CategoryBrandDto categoryBrandDto) {
    Long brandId = categoryBrandDto.getBrandId();
    Long categoryId = categoryBrandDto.getCategoryId();
    if (categoryBrandDto.getBrandId() != null && categoryBrandDto.getCategoryId() != null) {
        LambdaQueryWrapper<CategoryBrand> cbWrapper = new LambdaQueryWrapper<>();
        cbWrapper.eq(CategoryBrand::getBrandId, categoryBrandDto.getBrandId());
        cbWrapper.eq(CategoryBrand::getCategoryId, categoryBrandDto.getCategoryId());
        List<CategoryBrand> categoryBrandList = new ArrayList<>();
        CategoryBrand categoryBrand = new CategoryBrand();
        if (count(cbWrapper) > 0) {
            String categoryName = categoryService.getById(categoryBrandDto.getCategoryId()).getName();
            Brand brand = brandService.getById(categoryBrandDto.getBrandId());
            String brandName = brand.getName();
            String brandLogo = brand.getLogo();
            categoryBrand.setCategoryId(categoryBrandDto.getCategoryId());
            categoryBrand.setBrandId(categoryBrandDto.getBrandId());
            categoryBrand.setCategoryName(categoryName);
            categoryBrand.setBrandName(brandName);
            categoryBrand.setLogo(brandLogo);
            categoryBrandList.add(categoryBrand);
            return new PageResult<CategoryBrand>(1, 1, categoryBrandList);
        }
    }

    if (categoryBrandDto.getBrandId() == null && categoryBrandDto.getCategoryId() == null) {
        LambdaQueryWrapper<CategoryBrand> cbWrapper = new LambdaQueryWrapper<>();
        Page<CategoryBrand> page = new Page<>(categoryBrandDto.getPage(), categoryBrandDto.getSize());
        Page<CategoryBrand> pageResult = page(page, cbWrapper);
        return new PageResult<>(pageResult.getTotal(), pageResult.getPages(), pageResult.getRecords());
    }

    if (categoryBrandDto.getBrandId() != null && categoryBrandDto.getCategoryId() == null) {
        LambdaQueryWrapper<CategoryBrand> cbWrapper = new LambdaQueryWrapper<CategoryBrand>().eq(CategoryBrand::getBrandId, categoryBrandDto.getBrandId());
        Page<CategoryBrand> page = new Page<>(categoryBrandDto.getPage(), categoryBrandDto.getSize());
        Page<CategoryBrand> pageResult = page(page, cbWrapper);
        List<CategoryBrand> list = pageResult.getRecords();
        Brand brand = brandService.getById(categoryBrandDto.getBrandId());
        if (!list.isEmpty()) {
            List<Long> categoryIds = list.stream().map(CategoryBrand::getCategoryId).collect(Collectors.toList());
            List<CategoryBrand> categoryBrandList = categoryService.list(new LambdaQueryWrapper<Category>().in(Category::getId, categoryIds))
                    .stream()
                    .map(category -> {
                        CategoryBrand categoryBrand = new CategoryBrand();
                        categoryBrand.setBrandId(categoryBrandDto.getBrandId());
                        categoryBrand.setBrandName(brand.getName());
                        categoryBrand.setLogo(brand.getLogo());
                        categoryBrand.setCategoryId(category.getId());
                        categoryBrand.setCategoryName(category.getName());
                        return categoryBrand;
                    }).collect(Collectors.toList());
            return new PageResult<>(pageResult.getTotal(), pageResult.getPages(), categoryBrandList);
        }
    }
    if (categoryBrandDto.getCategoryId() != null && categoryBrandDto.getBrandId() == null) {
        LambdaQueryWrapper<CategoryBrand> cbWrapper = new LambdaQueryWrapper<CategoryBrand>().eq(CategoryBrand::getCategoryId, categoryBrandDto.getCategoryId());
        Page<CategoryBrand> page = new Page<>(categoryBrandDto.getPage(), categoryBrandDto.getSize());
        Page<CategoryBrand> pageResult = page(page, cbWrapper);
        List<CategoryBrand> list = pageResult.getRecords();
        Category category = categoryService.getById(categoryBrandDto.getCategoryId());
        if (!list.isEmpty()) {
            List<Long> brandIds = list.stream().map(CategoryBrand::getBrandId).collect(Collectors.toList());
            List<CategoryBrand> categoryBrandList = brandService.list(new LambdaQueryWrapper<Brand>().in(Brand::getId, brandIds))
                    .stream()
                    .map(brand -> {
                        CategoryBrand categoryBrand = new CategoryBrand();
                        categoryBrand.setBrandId(categoryBrandDto.getBrandId());
                        categoryBrand.setBrandName(brand.getName());
                        categoryBrand.setLogo(brand.getLogo());
                        categoryBrand.setCategoryId(categoryBrandDto.getCategoryId());
                        categoryBrand.setCategoryName(category.getName());
                        return categoryBrand;
                    }).collect(Collectors.toList());
            return new PageResult<>(pageResult.getTotal(), pageResult.getPages(), categoryBrandList);
        }
    }
    return new PageResult<>();
}
```

虽然上述代码能实现基本的功能，但却十分冗余且重复，需要优化一下。

****
