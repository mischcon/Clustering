CREATE TABLE scalaDB.tasks (
  id     INT(11)      NOT NULL AUTO_INCREMENT,
  method VARCHAR(128) NOT NULL,
  status VARCHAR(16)  NOT NULL DEFAULT 'NOT_STARTED',
  result VARCHAR(16),
  PRIMARY KEY (id),
  UNIQUE KEY method_UQ (method),
  CONSTRAINT check_status CHECK (status IN ('NOT_STARTED', 'IN_PROCESS', 'DONE')),
  CONSTRAINT check_result CHECK (result IN ('SUCCESS', 'FAILURE', 'ERROR'))
) 