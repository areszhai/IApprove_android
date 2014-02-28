CREATE TABLE "ia_todotask_approving" ("_id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, "taskId" INTEGER NOT NULL, "enterpriseId" INTEGER NOT NULL, "approveNumber" INTEGER NOT NULL, "submitContacts" VARCHAR(100) NOT NULL, "judge" SMALLINT NOT NULL, "adviceInfo" VARCHAR(400), "taskSenderFakeId" INTEGER NOT NULL, "taskStatus" SMALLINT NOT NULL, "taskOperateState" SMALLINT NOT NULL)