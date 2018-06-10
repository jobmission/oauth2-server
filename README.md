## oauth2-server: 基于SpringBoot 2 SSO oauth2 Server服务
## 创建SSO数据库,采用JPA框架，项目启动时数据表会自动创建</br>
````SQL
CREATE DATABASE IF NOT EXISTS oauth2_server DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
grant all privileges on oauth2_server.* to oauth2_server@localhost identified by 'password_dev';
初始化sql在src/main/resources/sql/init.sql,项目启动后可自行修改client_id等参数进行数据初始化
````
## 可以支持的授权模式grant_type:
````
4种授权模式：authorization_code,implicit,password,client_credentials;
/oauth/token?grant_type=password&scope=read&client_id=SampleClientId&client_secret=secret&username=zhangsan&password=password
````
## 使用Java工具包中的keytool制作证书jwt.jks，设置别名为jwt，密码为keypass</br>
````
keytool -genkey -alias jwt -keyalg RSA -keysize 1024 -keystore jwt.jks -validity 365
````
## 获取token公钥地址,用于本地直接验证token</br>
````
/oauth/token_key
````
## 验证token是否有效地址</br>
````
/oauth/check_token?token=XXXXXX
````

# 注意！！！
当Server和Client在一台机器上时，请配置域名代理，避免cookie相互覆盖
