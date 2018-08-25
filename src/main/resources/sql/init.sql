INSERT INTO oauth_client_entity (id, client_id, client_secret, authorized_grant_types, authorities, scope, web_server_redirect_uri)
VALUES (1, 'SampleClientId', '$2a$10$jdSgDx1AN.BUHYAqV1YOZOAo5KIl62iPgpPl.oszt1EpzQZFo.xFS',
        'implicit,authorization_code,refresh_token,password,client_credentials', 'ROLE_TRUSTED_CLIENT', 'read,write',
        'http://client.sso.com/login');

INSERT INTO user_account_entity (id, username, password, role)
VALUES (1, 'zhangsan', '$2a$10$8ZYKma84VAkov7wxhCbK9eYVcdetXoYM8Nnm8xIZA/8X/Kvwg7BN6', 'ROLE_SUPER'),
  (2, 'lisi', '$2a$10$8ZYKma84VAkov7wxhCbK9eYVcdetXoYM8Nnm8xIZA/8X/Kvwg7BN6', 'ROLE_USER');