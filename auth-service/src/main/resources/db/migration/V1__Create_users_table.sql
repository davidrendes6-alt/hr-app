-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('employee', 'manager')),
    department VARCHAR(255),
    position VARCHAR(255),
    hire_date DATE,
    salary DECIMAL(10, 2),
    phone_number VARCHAR(50),
    address TEXT,
    emergency_contact TEXT,
    bank_account VARCHAR(100),
    ssn VARCHAR(11),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON users(email);

-- Create index on role for filtering
CREATE INDEX idx_users_role ON users(role);

-- Insert test data with BCrypt hashed passwords (password123)
-- BCrypt hash for "password123": $2a$10$tQkKlsqmJYtYGV6jVN.P9.Gi9VWfLQ2ZIH.RJAz0zPloepf5P7Fdy
INSERT INTO users (id, email, password_hash, name, role, department, position, hire_date, salary, phone_number, address, emergency_contact, ssn) VALUES
    ('11111111-1111-1111-1111-111111111111', 'manager@company.com', '$2a$10$tQkKlsqmJYtYGV6jVN.P9.Gi9VWfLQ2ZIH.RJAz0zPloepf5P7Fdy', 'Jane Manager', 'manager', 'Management', 'HR Manager', '2020-01-15', 85000.00, '555-0101', '123 Manager St', 'Emergency: 555-0102', '123-45-6789'),
    ('22222222-2222-2222-2222-222222222222', 'employee1@company.com', '$2a$10$tQkKlsqmJYtYGV6jVN.P9.Gi9VWfLQ2ZIH.RJAz0zPloepf5P7Fdy', 'John Employee', 'employee', 'Engineering', 'Software Engineer', '2021-03-20', 65000.00, '555-0201', '456 Employee Ave', 'Emergency: 555-0202', '234-56-7890'),
    ('33333333-3333-3333-3333-333333333333', 'employee2@company.com', '$2a$10$tQkKlsqmJYtYGV6jVN.P9.Gi9VWfLQ2ZIH.RJAz0zPloepf5P7Fdy', 'Alice Developer', 'employee', 'Engineering', 'Senior Developer', '2019-06-10', 75000.00, '555-0301', '789 Developer Rd', 'Emergency: 555-0302', '345-67-8901');

