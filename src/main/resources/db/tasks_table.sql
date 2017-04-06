CREATE TABLE clustering.tasks (
  id            INT(11)      NOT NULL AUTO_INCREMENT,
  method        VARCHAR(128) NOT NULL,
  task_status   VARCHAR(16)  NOT NULL DEFAULT 'NOT_STARTED',
  end_state     VARCHAR(16),
  task_result   VARCHAR(2048),
  PRIMARY KEY (id),
  UNIQUE KEY method_UQ (method),
  CONSTRAINT check_task_status CHECK (task_status IN ('NOT_STARTED', 'RUNNING', 'DONE')),
  CONSTRAINT check_end_state CHECK (end_state IN (NULL, 'SUCCESS', 'FAILURE', 'ABANDONED', 'ERROR'))
);