# oauth2-server
基于spring boot2 sso Oauth2 Server服务



#创建API数据库
CREATE DATABASE IF NOT EXISTS oauth2_server DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
grant all privileges on oauth2_server.* to oauth2_server@localhost identified by 'password_dev';