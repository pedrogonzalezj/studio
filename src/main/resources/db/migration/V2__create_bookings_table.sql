CREATE OR REPLACE TABLE bookings (
    id UUID NOT NULL PRIMARY KEY,
    member VARCHAR(250) NOT NULL,
    class_id UUID,
    CONSTRAINT fk_booking_class FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci;
