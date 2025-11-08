-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 08, 2025 at 10:56 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dept_sync_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `attachments`
--

CREATE TABLE `attachments` (
  `attachment_id` int(11) NOT NULL,
  `record_id` int(11) NOT NULL,
  `filename` varchar(255) NOT NULL,
  `mime_type` varchar(100) DEFAULT NULL,
  `blob_data` longblob DEFAULT NULL,
  `uploaded_by` int(11) NOT NULL,
  `uploaded_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `audit_log`
--

CREATE TABLE `audit_log` (
  `AUDIT_ID` int(11) NOT NULL,
  `USER_ID` int(11) NOT NULL,
  `RECORD_ID` int(11) NOT NULL,
  `ACTION` varchar(50) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `TIMESTAMP` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `audit_log`
--

INSERT INTO `audit_log` (`AUDIT_ID`, `USER_ID`, `RECORD_ID`, `ACTION`, `DESCRIPTION`, `TIMESTAMP`) VALUES
(13, 1, 101, 'UPDATE', 'Corrected HR record details', '2025-11-08 21:24:55'),
(15, 1, 101, 'UPDATE', 'Corrected HR record details', '2025-11-08 21:26:44'),
(16, 1, 101, 'DELETE', 'Deleted an outdated HR record', '2025-11-08 21:26:45');

-- --------------------------------------------------------

--
-- Table structure for table `departments`
--

CREATE TABLE `departments` (
  `department_id` int(11) NOT NULL,
  `dept_code` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `contact_email` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `departments`
--

INSERT INTO `departments` (`department_id`, `dept_code`, `name`, `location`, `contact_email`) VALUES
(1, 'ADMIN', 'Administration', 'Main Building', 'admin@institution.com'),
(9, 'HR', 'Human Resources', 'Block A', 'hr@institution.com'),
(10, 'FIN', 'Finance', 'Block B', 'finance@institution.com'),
(11, 'REG', 'Registry', 'Records Office', 'registry@institution.com'),
(12, 'ICT', 'Information & Communication Technology', 'ICT Center', 'ict@institution.com'),
(13, 'ACAD', 'Academics', 'Academic Block', 'academics@institution.com'),
(14, 'LIB', 'Library Services', 'Library Building', 'library@institution.com');

-- --------------------------------------------------------

--
-- Table structure for table `records`
--

CREATE TABLE `records` (
  `record_id` int(11) NOT NULL,
  `external_id` varchar(255) DEFAULT NULL,
  `title` varchar(500) DEFAULT NULL,
  `payload` longtext DEFAULT NULL,
  `owner_department_id` int(11) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  `created_by` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `last_updated_by` int(11) DEFAULT NULL,
  `last_updated_at` timestamp NULL DEFAULT NULL,
  `row_version` int(11) DEFAULT 1,
  `is_deleted` tinyint(1) DEFAULT 0 CHECK (`is_deleted` in (0,1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `records`
--

INSERT INTO `records` (`record_id`, `external_id`, `title`, `payload`, `owner_department_id`, `status`, `created_by`, `created_at`, `last_updated_by`, `last_updated_at`, `row_version`, `is_deleted`) VALUES
(101, 'EXT101', 'Sample Record', 'Sample payload details', 1, 'ACTIVE', 1, '2025-11-08 21:23:09', 1, '2025-11-08 21:23:09', 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `record_visibility`
--

CREATE TABLE `record_visibility` (
  `record_id` int(11) NOT NULL,
  `department_id` int(11) NOT NULL,
  `access_level` varchar(50) DEFAULT NULL,
  `granted_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `granted_by` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE `roles` (
  `role_id` int(11) NOT NULL,
  `role_name` varchar(100) NOT NULL,
  `description` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`role_id`, `role_name`, `description`) VALUES
(1, 'ADMIN', NULL),
(7, 'STAFF', 'Departmental staff responsible for entering, updating, and managing departmental records.'),
(8, 'DEPT_HEAD', 'Department head responsible for approving data, generating reports, and oversight.'),
(9, 'STUDENT', 'End user (student) with read-only access to personal synchronized records.'),
(10, 'SYSTEM', 'Automated system process used for synchronization and background operations.');

-- --------------------------------------------------------

--
-- Table structure for table `sync_queue`
--

CREATE TABLE `sync_queue` (
  `sync_id` int(11) NOT NULL,
  `record_id` int(11) NOT NULL,
  `source_department_id` int(11) NOT NULL,
  `target_department_id` int(11) NOT NULL,
  `operation` varchar(50) NOT NULL,
  `payload` longtext DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `processed_flag` tinyint(1) DEFAULT 0 CHECK (`processed_flag` in (0,1)),
  `processed_at` timestamp NULL DEFAULT NULL,
  `error_message` varchar(4000) DEFAULT NULL,
  `retry_count` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `department_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `last_login_at` timestamp NULL DEFAULT NULL,
  `active_flag` tinyint(1) DEFAULT 1 CHECK (`active_flag` in (0,1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password_hash`, `full_name`, `email`, `department_id`, `role_id`, `created_at`, `last_login_at`, `active_flag`) VALUES
(1, 'admin', 'hashedpassword123', 'Admin User', 'admin@example.com', 1, 1, '2025-11-08 21:14:02', '2025-11-08 21:14:02', 1),
(2, 'admin1', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Admin1 User', 'admin@example1.com', 1, 1, '2025-11-08 21:46:51', NULL, 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `attachments`
--
ALTER TABLE `attachments`
  ADD PRIMARY KEY (`attachment_id`),
  ADD KEY `record_id` (`record_id`),
  ADD KEY `uploaded_by` (`uploaded_by`);

--
-- Indexes for table `audit_log`
--
ALTER TABLE `audit_log`
  ADD PRIMARY KEY (`AUDIT_ID`),
  ADD KEY `USER_ID` (`USER_ID`),
  ADD KEY `RECORD_ID` (`RECORD_ID`);

--
-- Indexes for table `departments`
--
ALTER TABLE `departments`
  ADD PRIMARY KEY (`department_id`),
  ADD UNIQUE KEY `dept_code` (`dept_code`);

--
-- Indexes for table `records`
--
ALTER TABLE `records`
  ADD PRIMARY KEY (`record_id`),
  ADD KEY `owner_department_id` (`owner_department_id`),
  ADD KEY `created_by` (`created_by`),
  ADD KEY `last_updated_by` (`last_updated_by`);

--
-- Indexes for table `record_visibility`
--
ALTER TABLE `record_visibility`
  ADD PRIMARY KEY (`record_id`,`department_id`),
  ADD KEY `department_id` (`department_id`),
  ADD KEY `granted_by` (`granted_by`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`role_id`),
  ADD UNIQUE KEY `role_name` (`role_name`);

--
-- Indexes for table `sync_queue`
--
ALTER TABLE `sync_queue`
  ADD PRIMARY KEY (`sync_id`),
  ADD KEY `record_id` (`record_id`),
  ADD KEY `source_department_id` (`source_department_id`),
  ADD KEY `target_department_id` (`target_department_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `department_id` (`department_id`),
  ADD KEY `role_id` (`role_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `attachments`
--
ALTER TABLE `attachments`
  MODIFY `attachment_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `audit_log`
--
ALTER TABLE `audit_log`
  MODIFY `AUDIT_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `departments`
--
ALTER TABLE `departments`
  MODIFY `department_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `records`
--
ALTER TABLE `records`
  MODIFY `record_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=102;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
  MODIFY `role_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `sync_queue`
--
ALTER TABLE `sync_queue`
  MODIFY `sync_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `attachments`
--
ALTER TABLE `attachments`
  ADD CONSTRAINT `attachments_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `records` (`record_id`),
  ADD CONSTRAINT `attachments_ibfk_2` FOREIGN KEY (`uploaded_by`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `audit_log`
--
ALTER TABLE `audit_log`
  ADD CONSTRAINT `audit_log_ibfk_1` FOREIGN KEY (`USER_ID`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `audit_log_ibfk_2` FOREIGN KEY (`RECORD_ID`) REFERENCES `records` (`record_id`);

--
-- Constraints for table `records`
--
ALTER TABLE `records`
  ADD CONSTRAINT `records_ibfk_1` FOREIGN KEY (`owner_department_id`) REFERENCES `departments` (`department_id`),
  ADD CONSTRAINT `records_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `records_ibfk_3` FOREIGN KEY (`last_updated_by`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `record_visibility`
--
ALTER TABLE `record_visibility`
  ADD CONSTRAINT `record_visibility_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `records` (`record_id`),
  ADD CONSTRAINT `record_visibility_ibfk_2` FOREIGN KEY (`department_id`) REFERENCES `departments` (`department_id`),
  ADD CONSTRAINT `record_visibility_ibfk_3` FOREIGN KEY (`granted_by`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `sync_queue`
--
ALTER TABLE `sync_queue`
  ADD CONSTRAINT `sync_queue_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `records` (`record_id`),
  ADD CONSTRAINT `sync_queue_ibfk_2` FOREIGN KEY (`source_department_id`) REFERENCES `departments` (`department_id`),
  ADD CONSTRAINT `sync_queue_ibfk_3` FOREIGN KEY (`target_department_id`) REFERENCES `departments` (`department_id`);

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `departments` (`department_id`),
  ADD CONSTRAINT `users_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
