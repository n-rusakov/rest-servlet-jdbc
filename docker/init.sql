CREATE SCHEMA IF NOT EXISTS games_schema;


CREATE TABLE IF NOT EXISTS games_schema.users
(
    id BIGSERIAL PRIMARY KEY,
	nickname VARCHAR(255) NOT NULL,
	email VARCHAR(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS games_schema.publishers
(
	id BIGSERIAL PRIMARY KEY,
	company_name VARCHAR(255) NOT NULL
);


CREATE TABLE IF NOT EXISTS games_schema.games
(
	id BIGSERIAL PRIMARY KEY,
	title VARCHAR(255) NOT NULL,
	publisher_id BIGINT NOT NULL REFERENCES games_schema.publishers(id)
		ON UPDATE CASCADE
);


CREATE TABLE IF NOT EXISTS games_schema.subscriptions
(
	user_id BIGINT REFERENCES games_schema.users(id) 
		ON DELETE CASCADE ON UPDATE CASCADE,
	game_id BIGINT REFERENCES games_schema.games(id)
		ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT user_game_key PRIMARY KEY (user_id, game_id)
);