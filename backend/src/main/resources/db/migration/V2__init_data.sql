-- V2__init_data.sql
-- Insert initial test data for GCT Reporter

-- Insert test users with BCrypt encrypted passwords
-- All passwords are in the format: username123
-- admin123, designer123, viewer123

INSERT INTO users (username, password, role, enabled, created_at, updated_at) VALUES
('admin', '$2a$10$1ySkjuR733l9Hnnq58XbFufRNanDDuIZKvR0qUU93dzNOiBvVjhTi', 'ADMIN', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('designer', '$2a$10$UrbGRtkd6dK83ZycP0OnEe79//qX2I/6zhxrnea5xJ0OWFl3TfJze', 'DESIGNER', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('viewer', '$2a$10$2zhc3sQyMNgljaYjP.3AFe8m2XLUcjPm.VTSCpgPVkdOtjn/XpZ5S', 'VIEWER', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
