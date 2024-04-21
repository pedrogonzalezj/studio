CREATE OR REPLACE TABLE classes (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    class_date DATE NOT NULL,
    capacity INT NOT NULL DEFAULT 0
) ENGINE InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci;

