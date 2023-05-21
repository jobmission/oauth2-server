CREATE TABLE user_account_entity(
  id bigint NOT NULL AUTO_INCREMENT,
  date_created datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  record_status int DEFAULT '0',
  remarks varchar(255) DEFAULT NULL,
  sort_priority int DEFAULT '0',
  version int DEFAULT '0',
  account_open_code varchar(255) DEFAULT NULL,
  address varchar(255) DEFAULT NULL,
  avatar_url varchar(255) DEFAULT NULL,
  birthday date DEFAULT NULL,
  city varchar(255) DEFAULT NULL,
  email varchar(255) DEFAULT NULL,
  failure_count int DEFAULT '0',
  failure_time datetime(6) DEFAULT NULL,
  gender varchar(255) DEFAULT NULL,
  mobile varchar(255) DEFAULT NULL,
  nick_name varchar(255) DEFAULT NULL,
  password varchar(255) NOT NULL,
  province varchar(255) DEFAULT NULL,
  username varchar(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (username)
) ENGINE=InnoDB;

CREATE TABLE role_entity (
  id bigint NOT NULL AUTO_INCREMENT,
  date_created datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  record_status int DEFAULT '0',
  remarks varchar(255) DEFAULT NULL,
  sort_priority int DEFAULT '0',
  version int DEFAULT '0',
  role_name varchar(15) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (role_name)
) ENGINE=InnoDB;


CREATE TABLE user_account_entity_roles (
  user_id bigint NOT NULL,
  role_id bigint NOT NULL
) ENGINE=InnoDB;

CREATE TABLE login_history_entity (
  id bigint NOT NULL AUTO_INCREMENT,
  date_created datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  record_status int DEFAULT '0',
  remarks varchar(255) DEFAULT NULL,
  sort_priority int DEFAULT '0',
  version int DEFAULT '0',
  client_id varchar(255) DEFAULT NULL,
  device varchar(255) DEFAULT NULL,
  ip varchar(255) DEFAULT NULL,
  username varchar(40) NOT NULL,
  PRIMARY KEY (id),
  KEY index_username (username)
) ENGINE=InnoDB;

CREATE TABLE oauth_client_entity (
  id bigint NOT NULL AUTO_INCREMENT,
  date_created datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  record_status int DEFAULT '0',
  remarks varchar(255) DEFAULT NULL,
  sort_priority int DEFAULT '0',
  version int DEFAULT '0',
  access_token_validity int DEFAULT NULL,
  additional_information varchar(255) DEFAULT NULL,
  application_name varchar(255) DEFAULT NULL,
  authorities varchar(255) DEFAULT NULL,
  authorized_grant_types varchar(255) NOT NULL,
  auto_approve varchar(255) DEFAULT NULL,
  client_id varchar(255) NOT NULL,
  client_secret varchar(255) NOT NULL,
  expiration_date datetime(6) DEFAULT NULL,
  refresh_token_validity int DEFAULT NULL,
  resource_ids varchar(255) DEFAULT NULL,
  scope varchar(255) DEFAULT NULL,
  web_server_redirect_uri varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (client_id)
) ENGINE=InnoDB;

CREATE TABLE scope_definition_entity (
  id bigint NOT NULL AUTO_INCREMENT,
  date_created datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  record_status int DEFAULT '0',
  remarks varchar(255) DEFAULT NULL,
  sort_priority int DEFAULT '0',
  version int DEFAULT '0',
  definition varchar(255) DEFAULT NULL,
  scope varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (scope)
) ENGINE=InnoDB;

CREATE TABLE third_party_account_entity (
  id bigint NOT NULL AUTO_INCREMENT,
  date_created datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  record_status int DEFAULT '0',
  remarks varchar(255) DEFAULT NULL,
  sort_priority int DEFAULT '0',
  version int DEFAULT '0',
  account_open_code varchar(255) DEFAULT NULL,
  avatar_url varchar(255) DEFAULT NULL,
  nick_name varchar(255) DEFAULT NULL,
  third_party varchar(20) NOT NULL,
  third_party_account_id varchar(100) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY  (third_party,third_party_account_id)
) ENGINE=InnoDB;

CREATE TABLE third_party_account_entity_roles (
  third_party_account_id bigint NOT NULL,
  role_id bigint NOT NULL
) ENGINE=InnoDB ;


