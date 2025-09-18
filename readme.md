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




