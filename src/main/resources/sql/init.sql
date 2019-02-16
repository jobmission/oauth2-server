INSERT INTO oauth_client_entity (id, client_id, client_secret, authorized_grant_types, authorities, scope, web_server_redirect_uri, access_token_validity, refresh_token_validity, remarks)
VALUES (1, 'SampleClientId', '$2a$10$gcrWom7ubcRaVD1.6ZIrIeJP0mtPLH5J9V/.8Qth59lZ4B/5HMq96', 'implicit,authorization_code,refresh_token,password,client_credentials', 'ROLE_TRUSTED_CLIENT', 'read,write', 'http://client.sso.com/login/oauth2/code/sso-login', 3600, 2592000, '测试明文:tgb.258');

INSERT INTO user_account_entity (id, username, password, role, remarks)
VALUES (1, 'zhangsan', '$2a$10$gcrWom7ubcRaVD1.6ZIrIeJP0mtPLH5J9V/.8Qth59lZ4B/5HMq96', 'ROLE_SUPER', '测试明文:tgb.258'),
       (2, 'lisi', '$2a$10$gcrWom7ubcRaVD1.6ZIrIeJP0mtPLH5J9V/.8Qth59lZ4B/5HMq96', 'ROLE_USER', '测试明文:tgb.258');