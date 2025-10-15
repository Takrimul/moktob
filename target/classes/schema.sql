-- Moktob Management SaaS Database Schema
-- PostgreSQL Database Setup Script

-- Create database (run this separately)
-- CREATE DATABASE moktob_saas;

-- Connect to the database
-- \c moktob_saas;

-- Core Module Tables
CREATE TABLE client (
    client_id BIGSERIAL PRIMARY KEY,
    client_name VARCHAR(100) NOT NULL,
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    address TEXT,
    subscription_plan VARCHAR(50),
    expiry_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE role (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE
);

CREATE TABLE user_account (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    username VARCHAR(100) UNIQUE,
    password_hash VARCHAR(255),
    full_name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    role_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE SET NULL
);

-- Education Module Tables
CREATE TABLE teacher (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    qualification VARCHAR(200),
    phone VARCHAR(20),
    email VARCHAR(100),
    joining_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE
);

CREATE TABLE class_entity (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    class_name VARCHAR(100) NOT NULL,
    teacher_id BIGINT,
    start_time TIME,
    end_time TIME,
    days_of_week VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teacher(id) ON DELETE SET NULL
);

CREATE TABLE student (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    guardian_name VARCHAR(100),
    guardian_contact VARCHAR(20),
    address TEXT,
    enrollment_date DATE,
    current_class_id BIGINT,
    photo_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE,
    FOREIGN KEY (current_class_id) REFERENCES class_entity(id) ON DELETE SET NULL
);

CREATE TABLE student_class_map (
    student_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (student_id, class_id),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES class_entity(id) ON DELETE CASCADE
);

-- Learning Module Tables
CREATE TABLE memorization_record (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    surah_name VARCHAR(100) NOT NULL,
    start_ayah INTEGER,
    end_ayah INTEGER,
    times_revised INTEGER DEFAULT 0,
    last_checked_date DATE,
    teacher_comment TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
);

CREATE TABLE assessment (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    assessment_date DATE,
    recitation_score INTEGER,
    tajweed_score INTEGER,
    discipline_score INTEGER,
    comments TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teacher(id) ON DELETE CASCADE
);

-- Attendance Module Tables
CREATE TABLE attendance (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    attendance_date DATE,
    status VARCHAR(20) CHECK (status IN ('PRESENT', 'ABSENT', 'LATE')),
    remarks TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES class_entity(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teacher(id) ON DELETE CASCADE
);

-- Finance Module Tables
CREATE TABLE payment (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    payer_type VARCHAR(20) CHECK (payer_type IN ('STUDENT', 'TEACHER', 'PARENT', 'OTHER')),
    payer_id BIGINT,
    amount DECIMAL(10,2),
    payment_type VARCHAR(20) CHECK (payment_type IN ('TUITION_FEE', 'BOOK_FEE', 'TRANSPORT_FEE', 'EXAM_FEE', 'OTHER')),
    payment_method VARCHAR(20) CHECK (payment_method IN ('CASH', 'BANK_TRANSFER', 'CHECK', 'CARD', 'ONLINE')),
    payment_date DATE,
    reference_no VARCHAR(100),
    remarks TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE
);

CREATE TABLE expense (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    category VARCHAR(100),
    amount DECIMAL(10,2),
    expense_date DATE,
    note TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE
);

-- Communication Module Tables
CREATE TABLE notification (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    recipient_user_id BIGINT NOT NULL,
    type VARCHAR(50),
    title VARCHAR(200),
    message TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_user_id) REFERENCES user_account(id) ON DELETE CASCADE
);

CREATE TABLE announcement (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    published_by BIGINT NOT NULL,
    published_at TIMESTAMP DEFAULT NOW(),
    target_role VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE,
    FOREIGN KEY (published_by) REFERENCES user_account(id) ON DELETE CASCADE
);

-- System Module Tables
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    user_id BIGINT,
    action VARCHAR(100),
    table_name VARCHAR(100),
    record_id BIGINT,
    timestamp TIMESTAMP DEFAULT NOW(),
    details TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE SET NULL
);

CREATE TABLE system_setting (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    key_name VARCHAR(100) NOT NULL,
    key_value TEXT,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE,
    UNIQUE(client_id, key_name)
);

-- Create Indexes for Performance
CREATE INDEX idx_student_client ON student(client_id);
CREATE INDEX idx_student_class ON student(current_class_id);
CREATE INDEX idx_teacher_client ON teacher(client_id);
CREATE INDEX idx_class_client ON class_entity(client_id);
CREATE INDEX idx_class_teacher ON class_entity(teacher_id);
CREATE INDEX idx_attendance_client ON attendance(client_id);
CREATE INDEX idx_attendance_student ON attendance(student_id);
CREATE INDEX idx_attendance_class ON attendance(class_id);
CREATE INDEX idx_attendance_date ON attendance(attendance_date);
CREATE INDEX idx_payment_client ON payment(client_id);
CREATE INDEX idx_payment_date ON payment(payment_date);
CREATE INDEX idx_expense_client ON expense(client_id);
CREATE INDEX idx_expense_date ON expense(expense_date);
CREATE INDEX idx_notification_client ON notification(client_id);
CREATE INDEX idx_notification_user ON notification(recipient_user_id);
CREATE INDEX idx_announcement_client ON announcement(client_id);
CREATE INDEX idx_audit_log_client ON audit_log(client_id);
CREATE INDEX idx_audit_log_user ON audit_log(user_id);
CREATE INDEX idx_system_setting_client ON system_setting(client_id);

-- Insert Sample Data
INSERT INTO client (client_name, contact_email, contact_phone, address, subscription_plan, expiry_date, is_active) 
VALUES ('Moktob Academy', 'admin@moktob.com', '+1234567890', '123 Education Street, City', 'PREMIUM', '2024-12-31', TRUE);

INSERT INTO role (client_id, role_name, description) 
VALUES (1, 'ADMIN', 'System Administrator'),
       (1, 'TEACHER', 'Teacher Role'),
       (1, 'STUDENT', 'Student Role'),
       (1, 'PARENT', 'Parent Role');

INSERT INTO user_account (client_id, username, password_hash, full_name, email, phone, role_id, is_active) 
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'System Administrator', 'admin@moktob.com', '+1234567890', 1, TRUE);

-- Additional sample users for testing
INSERT INTO user_account (client_id, username, password_hash, full_name, email, phone, role_id, is_active) 
VALUES (1, 'teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Teacher One', 'teacher1@moktob.com', '+1234567891', 2, TRUE),
       (1, 'student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Student One', 'student1@moktob.com', '+1234567892', 3, TRUE);

-- Create a function to update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at columns
CREATE TRIGGER update_role_updated_at BEFORE UPDATE ON role FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_user_account_updated_at BEFORE UPDATE ON user_account FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_teacher_updated_at BEFORE UPDATE ON teacher FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_class_entity_updated_at BEFORE UPDATE ON class_entity FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_student_updated_at BEFORE UPDATE ON student FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_student_class_map_updated_at BEFORE UPDATE ON student_class_map FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_memorization_record_updated_at BEFORE UPDATE ON memorization_record FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_assessment_updated_at BEFORE UPDATE ON assessment FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_attendance_updated_at BEFORE UPDATE ON attendance FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_payment_updated_at BEFORE UPDATE ON payment FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_expense_updated_at BEFORE UPDATE ON expense FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_notification_updated_at BEFORE UPDATE ON notification FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_announcement_updated_at BEFORE UPDATE ON announcement FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_audit_log_updated_at BEFORE UPDATE ON audit_log FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_system_setting_updated_at BEFORE UPDATE ON system_setting FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Grant permissions (adjust as needed for your environment)
-- GRANT ALL PRIVILEGES ON DATABASE moktob_saas TO postgres;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;
