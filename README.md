## SpringBoot2 oauth2 Server, SSO 单点登录
## 创建SSO数据库，采用JPA框架，项目启动时数据表会自动创建</br>
````SQL
CREATE DATABASE IF NOT EXISTS oauth2_server DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
grant all privileges on oauth2_server.* to oauth2_server@localhost identified by 'password_dev';
初始化sql在src/main/resources/sql/init.sql,项目启动后可自行修改client_id等参数进行数据初始化
````
## 支持的授权模式grant_type
````
4种授权模式：authorization_code,implicit,password,client_credentials;
/oauth/token?grant_type=password&scope=read&client_id=SampleClientId&client_secret=secret&username=zhangsan&password=password
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
## 非对称密钥token</br>
````
使用Java工具包中的keytool制作证书jwt.jks，设置别名为【jwt】，密码为【keypass】,替换位置src/main/resources/jwt.jks
keytool -genkey -alias jwt -keyalg RSA -keysize 1024 -keystore jwt.jks -validity 3650
````
## 获取token公钥,用于本地直接验证token</br>
````
/oauth/token_key
````
## 验证token是否有效</br>
````
/oauth/check_token?token=a.b.c
````
## 刷新token</br>
````
/oauth/token?grant_type=refresh_token&refresh_token=d.e.f
````

## 注册新用户接口</br>
````
/oauth/signUp?username=lisi&password=password&client_id=SampleClientId&client_secret=secret
````

## 启动方法</br>
````
java -jar oauth2-server-0.0.1-SNAPSHOT.jar
或者指定文件覆盖默认配置
java -jar oauth2-server-0.0.1-SNAPSHOT.jar --spring.config.additional-location=/path/to/override.properties
````
## OAuth 2 Developers Guide
[spring-security-oauth官方文档](https://projects.spring.io/spring-security-oauth/docs/oauth2.html)
[Tutorial](https://spring.io/guides/tutorials/spring-boot-oauth2/)

## 注意！！！
当Server和Client在一台机器上时，请配置域名代理，避免cookie相互覆盖
