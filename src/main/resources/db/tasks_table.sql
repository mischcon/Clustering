CREATE TABLE scalaDB.tasks (
  id     INT(11)      NOT NULL AUTO_INCREMENT,
  method VARCHAR(128) NOT NULL,
  task_status VARCHAR(16)  NOT NULL DEFAULT 'NOT_STARTED',
  end_state VARCHAR(16),
  task_result VARCHAR(2048),
  PRIMARY KEY (id),
  UNIQUE KEY method_UQ (method),
  CONSTRAINT check_status CHECK (status IN ('NOT_STARTED', 'IN_PROCESS', 'DONE')),
  CONSTRAINT check_result CHECK (result IN ('SUCCESS', 'FAILURE', 'ERROR'))
) 