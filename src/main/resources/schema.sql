CREATE TABLE IF NOT EXISTS teacher(
    id INT AUTO_INCREMENT PRIMARY KEY ,
    name VARCHAR(200) NOT NULL ,
    contact VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS course(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    duration_in_months INT NOT NULL
);
CREATE TABLE IF NOT EXISTS teacher_course(
    teacher_id  INT NOT NULL,
    course_id INT NOT NULL,
    CONSTRAINT PRIMARY KEY (teacher_id, course_id),
    CONSTRAINT fk_1 FOREIGN KEY (teacher_id) REFERENCES teacher(id),
    CONSTRAINT fk_2 FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
);


INSERT INTO teacher (name, contact) VALUES ('Dasith',contact='021-32323321');