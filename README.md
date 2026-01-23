
## SpringBoot 4.0+ oauth2 server

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
1. 获取code，需要 PKCE 方式
Get /oauth2/authorize
client_id=SampleClientId
response_type=code
redirect_uri=http://localhost:10480/login/oauth2/code/sso-login
scope=openid profile
code_challenge=6bQSbd4AdLzLhBvSPjUjthOJOt5AZcV19-pbjpeRM64
code_challenge_method=S256

用户同意授权后服务端响应,浏览器重定向到：http://localhost:10480/login?code=xxxxxxxxxxx，接收code,然后后端调用步骤2获取token

2. 获取token
Post /oauth2/token
表单数据, Content-Type: application/x-www-form-urlencoded
client_id=SampleClientId
client_secret=tgb.258
grant_type=authorization_code
redirect_uri=http://localhost:10480/login/oauth2/code/sso-login
code=UqkOyZ1vLRSjpwcZpUcHJYvvGlsenAvk7CH5LsW3oOSE-rE3znA834YrJYSuJKzs25UnxoLIL47W2GA2BuEyK8GZG2qnDkqsaoaASJWd1IgUQ1x3d1XBL6lO-SQGiqZ7
code_verifier=UWxD3NZtmkuitGkVdZnbkjjHgolTxOXrMpxesbGYUV3vVHYNo6yLyKGm4rzOaTUAT2phuwzXVfh-ozdulmoXrse10IHlWdj5jf7PdQR0YWFcPvHuOTwGsbTuphpMGCna

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
Get /userinfo?access_token=a.b.c
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


#### 注意！！！当Server和Client在一台机器上时，请配置域名代理，避免cookie相互覆盖，或者修改默认的session id
````
#修改默认的JSESSIONID为my_session_id
server.servlet.session.cookie.name=oauth2_session_id
````


