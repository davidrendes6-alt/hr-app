-- Insert test users
-- Note: password_hash is BCrypt hash for 'password123'
INSERT INTO users (id, email, password_hash, name, role, department, position, hire_date, salary, phone_number, address, emergency_contact, ssn, created_at, updated_at) VALUES
('11111111-1111-1111-1111-111111111111', 'manager@company.com', '$2a$10$tQkKlsqmJYtYGV6jVN.P9.Gi9VWfLQ2ZIH.RJAz0zPloepf5P7Fdy', 'Jane Manager', 'manager', 'Management', 'HR Manager', '2020-01-15', 85000.00, '+1-555-0101', '123 Manager St, City, State 12345', 'Emergency Contact: +1-555-0102', '123-45-6789', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222222', 'employee1@company.com', '$2a$10$tQkKlsqmJYtYGV6jVN.P9.Gi9VWfLQ2ZIH.RJAz0zPloepf5P7Fdy', 'John Employee', 'employee', 'Engineering', 'Software Developer', '2021-03-10', 75000.00, '+1-555-0201', '456 Employee Ave, City, State 12345', 'Emergency Contact: +1-555-0202', '234-56-7890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('33333333-3333-3333-3333-333333333333', 'employee2@company.com', '$2a$10$tQkKlsqmJYtYGV6jVN.P9.Gi9VWfLQ2ZIH.RJAz0zPloepf5P7Fdy', 'Alice Developer', 'employee', 'Engineering', 'Senior Developer', '2019-06-20', 90000.00, '+1-555-0301', '789 Developer Blvd, City, State 12345', 'Emergency Contact: +1-555-0302', '345-67-8901', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('44444444-4444-4444-4444-444444444444', 'employee3@company.com', '$2a$10$tQkKlsqmJYtYGV6jVN.P9.Gi9VWfLQ2ZIH.RJAz0zPloepf5P7Fdy', 'Bob Smith', 'employee', 'Sales', 'Sales Representative', '2022-01-05', 65000.00, '+1-555-0401', '321 Sales St, City, State 12345', 'Emergency Contact: +1-555-0402', '456-78-9012', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test absence requests
INSERT INTO absence_requests (id, employee_id, start_date, end_date, reason, status, created_at, updated_at) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222', '2025-12-20', '2025-12-31', 'Holiday vacation', 'pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '33333333-3333-3333-3333-333333333333', '2025-11-25', '2025-11-26', 'Personal days', 'approved', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cccccccc-cccc-cccc-cccc-cccccccccccc', '44444444-4444-4444-4444-444444444444', '2025-12-01', '2025-12-05', 'Medical leave', 'pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test feedback
INSERT INTO feedback (id, profile_id, author_id, content, is_polished, created_at) VALUES
('dddddddd-dddd-dddd-dddd-dddddddddddd', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'John is an excellent team player and consistently delivers high-quality work.', false, CURRENT_TIMESTAMP),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', 'Alice demonstrates exceptional technical skills and leadership qualities.', false, CURRENT_TIMESTAMP),
('ffffffff-ffff-ffff-ffff-ffffffffffff', '22222222-2222-2222-2222-222222222222', '33333333-3333-3333-3333-333333333333', 'Great colleague, always willing to help and share knowledge.', false, CURRENT_TIMESTAMP);

