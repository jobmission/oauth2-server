## SpringBoot 2.x oauth2 Server, SSO 单点登录
## 创建数据库：持久层采用JPA框架，项目启动前应先创建数据库，启动时数据表会自动创建</br>
````
#创建数据库SQL，数据库名、数据库用户名、密码需要和application.properties中的一致
CREATE DATABASE IF NOT EXISTS oauth2_server DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
grant all privileges on oauth2_server.* to oauth2_server@localhost identified by 'password_dev';
#初始化sql在src/main/resources/sql/init.sql,项目启动后可自行修改client_id等初始化数据
````
## 管理员角色登录后，可以对用户和client进行管理</br>
## 支持的4种授权模式grant_type</br>
````
authorization_code,implicit,password,client_credentials;
````
#####
>>**authorization_code模式：用于PC端，页面跳转** 相对复杂，安全性最高，需要两步获取token
````
1. Get /oauth/authorize?client_id=SampleClientId&response_type=code&redirect_uri=http://client.sso.com/login
响应：
重定向到：http://client.sso.com/login?code=1E37Xk，接收code,然后后端调用步骤2获取token
2. Post /oauth/token?client_id=SampleClientId&client_secret=secret&grant_type=authorization_code&redirect_uri=http://client.sso.com/login&code=1E37Xk
响应：
{
    "access_token": "a.b.c",
    "token_type": "bearer",
    "refresh_token": "d.e.f",
    "expires_in": 43199,
    "scope": "read",
    "userId": "1",
    "jti": "823cdd71-4732-4f9d-b949-a37ceb4488a4"
}
````
>>**password模式：用于手机端或者其他无页面跳转场景，应由后台服务端调用，保护client_id和client_secret**
````
Post /oauth/token?grant_type=password&scope=read&client_id=SampleClientId&client_secret=secret&username=zhangsan&password=password
响应：
{
    "access_token": "a.b.c",
    "token_type": "bearer",
    "refresh_token": "d.e.f",
    "expires_in": 43199,
    "scope": "read",
    "userId": "1",
    "jti": "823cdd71-4732-4f9d-b949-a37ceb4488a4"
}
````
## 非对称密钥生成，用于签名token、在资源端本地验证token</br>
````
使用Java工具包中的keytool制作证书jwt.jks，设置别名为【jwt】，密码为【keypass】,替换位置src/main/resources/jwt.jks
keytool -genkey -alias jwt -keyalg RSA -keysize 1024 -keystore jwt.jks -validity 3650
````
## 获取token签名公钥，用于本地直接验证token</br>
````
/oauth/token_key
````
## 验证token是否有效，用于在资源端调用验证token</br>
````
/oauth/check_token?token=a.b.c
````
## 刷新token</br>
````
Post /oauth/token?grant_type=refresh_token&refresh_token=d.e.f
````

## 注册新用户接口，应由后台服务端调用，保护client_id和client_secret</br>
````
Post /oauth/signUp?username=lisi&password=password&client_id=SampleClientId&client_secret=secret
````

## 启动方法</br>
````
java -jar oauth2-server-0.0.1-SNAPSHOT.jar
或者指定配置文件覆盖默认配置
java -jar oauth2-server-0.0.1-SNAPSHOT.jar --spring.config.additional-location=/path/to/override.properties
````
## 效果图
![登录页](https://raw.githubusercontent.com/jobmission/oauth2-server/master/src/test/resources/static/imgs/login.png)
![用户管理](https://raw.githubusercontent.com/jobmission/oauth2-server/master/src/test/resources/static/imgs/users.png)
![client管理](https://raw.githubusercontent.com/jobmission/oauth2-server/master/src/test/resources/static/imgs/clients.png)

## OAuth 2 Developers Guide
[spring-security-oauth官方文档](https://projects.spring.io/spring-security-oauth/docs/oauth2.html) <br/>
[Spring Boot and OAuth2 Tutorial](https://spring.io/guides/tutorials/spring-boot-oauth2/)

[client 前端DEMO](https://github.com/jobmission/oauth2-client.git) <br/>
[api 资源接口端DEMO](https://github.com/jobmission/oauth2-resource.git)

### TODO LIST
* 完善客户端管理
* 完善用户管理
* 分布式支持

## 注意！！！
当Server和Client在一台机器上时，请配置域名代理，避免cookie相互覆盖

