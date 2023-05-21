INSERT INTO oauth_client_entity (id, client_id, application_name, client_secret, authorized_grant_types,
                                 scope, web_server_redirect_uri, access_token_validity, refresh_token_validity, remarks)
VALUES (1, 'SampleClientId', 'SampleClientId 测试应用', '$2a$10$gcrWom7ubcRaVD1.6ZIrIeJP0mtPLH5J9V/.8Qth59lZ4B/5HMq96',
        'authorization_code,refresh_token', 'openid,profile,message.read,message.write',
        'http://client.sso.com/login/oauth2/code/sso-login', 7200, 2592000, '测试明文:tgb.258')
        ON DUPLICATE KEY UPDATE version = version + 1;

INSERT INTO scope_definition_entity (id, scope, definition)
values (1, 'openid', 'openid'),(2, 'profile', '昵称、头像')
ON DUPLICATE KEY UPDATE version = version + 1;

insert into role_entity(id, role_name)
values (1, 'ROLE_SUPER'), (2, 'ROLE_USER'), (3, 'ROLE_OPERATOR')
ON DUPLICATE KEY UPDATE version = version + 1;

INSERT INTO user_account_entity (id, username, password, account_open_code, nick_name, remarks)
VALUES (1, 'zhangsan', '$2a$10$gcrWom7ubcRaVD1.6ZIrIeJP0mtPLH5J9V/.8Qth59lZ4B/5HMq96', '1', '张三', '测试明文:tgb.258'),
       (2, 'lisi', '$2a$10$gcrWom7ubcRaVD1.6ZIrIeJP0mtPLH5J9V/.8Qth59lZ4B/5HMq96', '2', '李四', '测试明文:tgb.258')
       ON DUPLICATE KEY UPDATE version = version + 1;

insert into user_account_entity_roles(user_id, role_id)
values (1, 1),(1, 3), (2, 2), (2, 3)
ON DUPLICATE KEY UPDATE role_id = role_id;

