
## SpringBoot 3.x oauth2 server

#### 创建数据库：持久层采用JPA框架，项目启动前必须先创建数据库，启动时数据表会自动创建

##### 默认用Mysql数据库，如需用其他数据库请修改配置文件以及数据库驱动
##### 创建数据库SQL：数据库名、数据库用户名、数据库密码需要和application.properties中的一致
````
CREATE DATABASE IF NOT EXISTS oauth2_server DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
create user 'oauth2_server'@'localhost' identified by 'password_dev';
grant all privileges on oauth2_server.* to 'oauth2_server'@'localhost';
````
##### 初始化数据sql在src/main/resources/sql/init.sql, liquibase自动执行(首次执行需禁用)

#### RSA密钥生成，用于签名token，客户端、资源端本地验证token
````
使用Java工具包中的keytool制作证书jwt.jks，重要参数:设置别名为【jwt】，有效天数为【1000】，密码为【keypass】，替换位置src/main/resources/jwt.jks
keytool -genkey -alias jwt -keyalg RSA -keysize 2048 -keystore /your/path/to/jwt.jks -validity 1000
````

#### oauth2 openid 端点
````
Get /.well-known/openid-configuration
````

#### 支持的授权模式grant_type</br>
````
authorization_code, refresh_token
````
#### 接口调用
````
1. Get /oauth2/authorize?client_id=SampleClientId&response_type=code&redirect_uri=http://client.sso.com/login/oauth2/code/sso-login&scope=openid profile
用户同意授权后服务端响应,浏览器重定向到：http://client.sso.com/login?code=1E37Xk，接收code,然后后端调用步骤2获取token
2. Post /oauth/token?client_id=SampleClientId&client_secret=tgb.258&grant_type=authorization_code&redirect_uri=http://client.sso.com/login/oauth2/code/sso-login&code=1E37Xk
响应：
{
    "access_token": "a.b.c",
    "refresh_token": "d.e.f",
    "scope": "openid profile",
    "id_token": "h.i.j",
    "token_type": "Bearer",
    "expires_in": 7199
}
````

#### 访问受保护资源，请求时携带token
````
Get /user/me?access_token=a.b.c
或者http header中加入Authorization,如下
Authorization: Bearer a.b.c
````

#### 刷新token</br>
````
Post /oauth2/token?client_id=SampleClientId&client_secret=tgb.258&grant_type=refresh_token&refresh_token=d.e.f
````

#### 启动方法</br>
````
java -jar oauth2-server-x.y.z.jar
或者指定配置文件覆盖默认配置
java -jar oauth2-server-x.y.z.jar --spring.config.additional-location=/path/to/override.properties
````

#### 管理员角色登录后，可以对用户和client进行管理</br>
#### 效果图
![登录页](https://raw.githubusercontent.com/jobmission/oauth2-server/master/src/test/resources/static/imgs/login.png)
![用户管理](https://raw.githubusercontent.com/jobmission/oauth2-server/master/src/test/resources/static/imgs/users.png)
![client管理](https://raw.githubusercontent.com/jobmission/oauth2-server/master/src/test/resources/static/imgs/clients.png)


#### 注意！！！当Server和Client在一台机器上时，请配置域名代理，避免cookie相互覆盖


