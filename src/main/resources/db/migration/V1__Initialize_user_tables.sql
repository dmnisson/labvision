CREATE TABLE users(
   username VARCHAR_IGNORECASE(128) NOT NULL PRIMARY KEY,
   password VARCHAR(128) NOT NULL,
   enabled BOOLEAN NOT NULL
);

CREATE TABLE authorities(
   username VARCHAR_IGNORECASE(128) NOT NULL,
   authority VARCHAR_IGNORECASE(128) NOT NULL,
   CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username)
);

CREATE TABLE persistent_logins(
   username VARCHAR(128) NOT NULL,
   series VARCHAR(128) NOT NULL,
   token VARCHAR(128) NOT NULL,
   last_used TIMESTAMP NOT NULL
);