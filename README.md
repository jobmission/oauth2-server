## SpringBoot 2.x oauth2 server, SSO 单点登录

## 创建数据库：持久层采用JPA框架，项目启动前必须先创建数据库，启动时数据表会自动创建</br>
````
#默认用Mysql数据库，如需用其他数据库请修改配置文件以及数据库驱动
#创建数据库SQL：数据库名、数据库用户名、数据库密码需要和application.properties中的一致

CREATE DATABASE IF NOT EXISTS oauth2_server DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
create user 'oauth2_server'@'localhost' identified by 'password_dev';
grant all privileges on oauth2_server.* to 'oauth2_server'@'localhost';

#初始化数据sql在src/main/resources/sql/init.sql,可自行修改client_id等初始化数据
````
## 支持的4种授权模式grant_type</br>
````
authorization_code,implicit,password,client_credentials;
````
#####
* authorization_code模式：**用于PC端，页面跳转**，安全性最高，需要两步获取token;`需确保redirect_uri和数据库中对应的redirect_uri一致`
````
1. Get /oauth/authorize?client_id=SampleClientId&response_type=code&redirect_uri=http://client.sso.com/login/oauth2/code/sso-login
用户同意授权后服务端响应,浏览器重定向到：http://client.sso.com/login?code=1E37Xk，接收code,然后后端调用步骤2获取token
2. Post /oauth/token?client_id=SampleClientId&client_secret=tgb.258&grant_type=authorization_code&redirect_uri=http://client.sso.com/login/oauth2/code/sso-login&code=1E37Xk
响应：
{
    "access_token": "a.b.c",
    "token_type": "bearer",
    "refresh_token": "d.e.f",
    "expires_in": 43199,
    "scope": "user_info",
    "userId": "1",
    "jti": "823cdd71-4732-4f9d-b949-a37ceb4488a4"
}
````
* password模式：用于手机端或者其他无页面跳转场景，应由后台服务端调用，**保护client_id和client_secret**
````
Post /oauth/token?client_id=SampleClientId&client_secret=tgb.258&grant_type=password&scope=user_info&username=zhangsan&password=tgb.258
响应：
{
    "access_token": "a.b.c",
    "token_type": "bearer",
    "refresh_token": "d.e.f",
    "expires_in": 43199,
    "scope": "user_info",
    "userId": "1",
    "jti": "823cdd71-4732-4f9d-b949-a37ceb4488a4"
}
````
## RSA密钥生成，用于签名token，客户端、资源端本地验证token
````
使用Java工具包中的keytool制作证书jwt.jks，重要参数:设置别名为【jwt】，有效天数为【36500】，密码为【keypass】，替换位置src/main/resources/jwt.jks
keytool -genkey -alias jwt -keyalg RSA -keysize 1024 -keystore /your/path/to/jwt.jks -validity 36500
````
## 获取jwt token签名的RSA公钥，用于本地验证token
````
Get /oauth/token_key
````
## jwk-set-uri：resource server 可以得到jwt token签名公钥并缓存，进行本地验证
````
Get /.well-known/jwks.json
````

## 验证token，用于在资源端调用验证token是否有效</br>
````
Post /oauth/check_token?token=a.b.c
````

## 访问受保护资源，请求时携带token</br>
````
Get /user/me?access_token=a.b.c
或者http header中加入Authorization,如下
Authorization: Bearer a.b.c
````

## 刷新token</br>
````
Post /oauth/token?client_id=SampleClientId&client_secret=tgb.258&grant_type=refresh_token&refresh_token=d.e.f
````

## 注册新用户接口</br>
````
1、获取验证码序号
 Get /captcha/graph
 响应：
 {
   "graphUrl": "/captcha/graph/print?graphId=32a41c71-d74a-4aa6-b73c-af3627e82485",
   "graphId": "32a41c71-d74a-4aa6-b73c-af3627e82485",
   "ttl": 300,
   "status": 1
 }
2、显示验证码
 Get /captcha/graph/print?graphId=a32a41c71-d74a-4aa6-b73c-af3627e82485
 响应：
 图片流
3、调用注册接口 
 Post /oauth/signUp?username=lisi&password=yourpass0!&graphId=a32a41c71-d74a-4aa6-b73c-af3627e82485&verificationCode=1324
 响应：
 {
     "status": 1,
     "timestamp": 1561729652797
 }
 
````

##  扩展grant_type,參照SMSCodeTokenGranter


## 启动方法</br>
````
java -jar oauth2-server-0.0.1-SNAPSHOT.jar
或者指定配置文件覆盖默认配置
java -jar oauth2-server-0.0.1-SNAPSHOT.jar --spring.config.additional-location=/path/to/override.properties
````

## 管理员角色登录后，可以对用户和client进行管理</br>
## 效果图
![登录页](https://raw.githubusercontent.com/jobmission/oauth2-server/master/src/test/resources/static/imgs/login.png)
![用户管理](https://raw.githubusercontent.com/jobmission/oauth2-server/master/src/test/resources/static/imgs/users.png)
![client管理](https://raw.githubusercontent.com/jobmission/oauth2-server/master/src/test/resources/static/imgs/clients.png)

## OAuth 2 Developers Guide
[spring-security-oauth官方文档](https://projects.spring.io/spring-security-oauth/docs/oauth2.html) <br/>
[Spring Boot and OAuth2 Tutorial](https://spring.io/guides/tutorials/spring-boot-oauth2/)

[client 前端DEMO](https://github.com/jobmission/oauth2-client.git) <br/>
[api 资源接口端DEMO](https://github.com/jobmission/oauth2-resource.git)


## 注意！！！
当Server和Client在一台机器上时，请配置域名代理，避免cookie相互覆盖

[Bcrypt 在线密码生成](https://www.jisuan.mobi/index.php?tag=Bcrypt)
[ Bcrypt 在线密码生成](https://www.devglan.com/online-tools/bcrypt-hash-generator)


