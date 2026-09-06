-- Database Creation (Run this in PostgreSQL admin/psql if database does not exist)
-- CREATE DATABASE banking_db;

-- Connect to banking_db before running the following:

CREATE TABLE IF NOT EXISTS accounts (
    account_number VARCHAR(50) PRIMARY KEY,
    holder_name VARCHAR(100) NOT NULL,
    balance NUMERIC(15, 2) NOT NULL,
    account_type VARCHAR(20) NOT NULL
);
