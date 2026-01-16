-- V3__create_sequences.sql
-- Create sequence tables for Hibernate generators

-- Create sequence table for users
CREATE TABLE users_seq (
    next_val INTEGER DEFAULT 4
);

-- Initialize the sequence with current max ID + 1
INSERT INTO users_seq (next_val) VALUES (4);