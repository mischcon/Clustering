CREATE TABLE clustering.tasks (
  id            INT(11)      NOT NULL AUTO_INCREMENT,
  method        VARCHAR(128) NOT NULL,
  task_status   VARCHAR(16)  NOT NULL DEFAULT 'NOT_STARTED',
  end_state     VARCHAR(16),
  task_result   VARCHAR(2048),
  started_at	TIMESTAMP    DEFAULT 0,
  finished_at	TIMESTAMP    DEFAULT 0,
  time_spent	INT(10),
  PRIMARY KEY (id),
  UNIQUE KEY method_UQ (method)
);