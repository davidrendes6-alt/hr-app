-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
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

-- Absence requests table
CREATE TABLE absence_requests (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL REFERENCES users(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Feedback table
CREATE TABLE feedback (
    id UUID PRIMARY KEY,
    profile_id UUID NOT NULL REFERENCES users(id),
    author_id UUID NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    is_polished BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT feedback_no_self_feedback CHECK (profile_id != author_id)
);

-- Create indexes
CREATE INDEX idx_absence_requests_employee_id ON absence_requests(employee_id);
CREATE INDEX idx_absence_requests_status ON absence_requests(status);
CREATE INDEX idx_feedback_profile_id ON feedback(profile_id);
CREATE INDEX idx_feedback_author_id ON feedback(author_id);

