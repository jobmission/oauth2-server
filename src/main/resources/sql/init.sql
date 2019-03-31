INSERT INTO oauth_client_entity (id, client_id, application_name, client_secret, authorized_grant_types, authorities, scope, web_server_redirect_uri, access_token_validity, refresh_token_validity, remarks)
VALUES (1, 'SampleClientId', 'SampleClientId 测试应用', '$2a$10$gcrWom7ubcRaVD1.6ZIrIeJP0mtPLH5J9V/.8Qth59lZ4B/5HMq96',
  'implicit,authorization_code,refresh_token,password,client_credentials', 'ROLE_TRUSTED_CLIENT', 'user_info',
  'http://client.sso.com/login/oauth2/code/sso-login', 3600, 2592000, '测试明文:tgb.258');

INSERT INTO scope_definition_entity (id, scope, definition) values (1, 'user_info', '昵称、头像、性别信息');

INSERT INTO user_account_entity (id, username, password, role, remarks)
VALUES (1, 'zhangsan', '$2a$10$gcrWom7ubcRaVD1.6ZIrIeJP0mtPLH5J9V/.8Qth59lZ4B/5HMq96', 'ROLE_SUPER', '测试明文:tgb.258'),
  (2, 'lisi', '$2a$10$gcrWom7ubcRaVD1.6ZIrIeJP0mtPLH5J9V/.8Qth59lZ4B/5HMq96', 'ROLE_USER', '测试明文:tgb.258');