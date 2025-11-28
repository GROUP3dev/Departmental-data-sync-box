```sql
-- Script to enable remote access for the 'root' user
-- Run this script on the SERVER machine (the one hosting the database)

-- 1. Allow root to connect from any IP address (%)
-- NOTE: Using empty password as per your setup.
CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;

-- 2. Apply changes
FLUSH PRIVILEGES;

-- Note: Ensure Windows Firewall allows traffic on port 3306.
```
