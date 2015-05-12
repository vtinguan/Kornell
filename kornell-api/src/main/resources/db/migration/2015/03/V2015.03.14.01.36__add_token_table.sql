CREATE TABLE IF NOT EXISTS Token (
	token char(36) primary key,
	personUUID char(36),
	expiry timestamp,
	clientType char(10) not null,
	UNIQUE KEY unique_personUUID (personUUID)
)