# 📦 Departmental Data Sync Box 2

## 🔹 Project Overview
The **Departmental Data Synchronization Project** is designed to eliminate data silos and improve reporting efficiency across the institution.  
Currently, departments manage data independently, which causes redundancy, inconsistencies, and delays in reporting.  

This project will create a **centralized yet synchronized system** using **Java (Swing)** and an **Oracle Database**, with **LAN and Cloud synchronization**.  
The system automates data updates, ensuring accuracy and consistency across all departments.

## ✨ Key Benefits
- **Enhanced Data Accuracy** – Real-time synchronization minimizes errors, ensuring all departments work with up-to-date and consistent information.  
- **Improved Collaboration** – A unified system breaks down barriers between departments, fostering seamless cooperation.  
- **Increased Security** – Centralized data handling enables stronger security measures and controlled access.  
- **Operational Efficiency** – Automation reduces manual workloads, saving time and resources.  

📊 **Expected Impact within 6 months**  
- ⏳ **30% reduction** in reporting time.  
- 📝 **15% decrease** in data entry errors.  



## ⚙️ Tech Stack
- **Frontend:** Java Swing (desktop-based UI)  
- **Backend:** Oracle Database (21c+)  
- **Middleware:** JDBC, LAN Communication (Sockets/RMI)  
- **Cloud Integration:** Google Drive API for backup & sync  


## 📂 Repository Structure

Departmental-Data-Sync-Box/
│
├── docs/                        # Documentation files
│
├── lib/                         # External libraries (JAR files)
│   ├── ojdbc11.jar              # Oracle JDBC Driver
│   └── other-libs.jar           # Other required libs
│
├── src/                         # Source code
│   ├── main/                    # Main source code
│   │   ├── model/               # Data models (POJOs)
│   │   │   ├── Department.java
│   │   │   ├── Staff.java
│   │   │   ├── SyncLog.java
│   │   │   └── User.java
│   │   │
│   │   ├── dao/                 # Data Access Objects (DB handling)
│   │   │   ├── DepartmentDAO.java
│   │   │   ├── StaffDAO.java
│   │   │   ├── SyncLogDAO.java
│   │   │   └── UserDAO.java
│   │   │
│   │   ├── db/                  # Database utility classes
│   │   │   ├── DBConnection.java
│   │   │   └── DBInitializer.java
│   │   │
│   │   ├── sync/                # Sync engine (LAN + Cloud)
│   │   │   ├── SyncManager.java
│   │   │   ├── LanSync.java
│   │   │   └── CloudSync.java
│   │   │
│   │   ├── ui/                  # User Interface (Java Swing)
│   │   │   ├── LoginUI.java
│   │   │   ├── AdminDashboard.java
│   │   │   ├── StaffDashboard.java
│   │   │   └── SyncStatusUI.java
│   │   │
│   │   └── Main.java            # Application entry point
│   │
│   └── test/                    # Test classes (JUnit)
│       ├── DepartmentDAOTest.java
│       ├── SyncManagerTest.java
│       └── UserDAOTest.java
│
├── database/                    # SQL scripts
│   ├── schema.sql               # Create tables
│   ├── seed.sql                 # Sample/test data
│   └── procedures.sql           # Stored procedures & triggers
│
├── config/                      # Config files
│   ├── db.properties            # DB connection settings
│   └── sync.properties          # Sync configurations
│
├── build/                       # Compiled .class files (generated after compilation)
│
├── dist/                        # Packaged JAR files
│   └── DepartmentalDataSyncBox.jar
│
├── .gitignore                   # Git ignore rules


## 📌 Commit Message Guidelines

We follow the **Conventional Commits** standard to keep our commit history clean and meaningful.  
Each commit message should start with a **type** that describes the purpose of the change.

### ✅ Commit Types

- **feat:** → A new feature  
  _Example:_ `feat(ui): add login screen with validation`

- **fix:** → A bug fix  
  _Example:_ `fix(sync): resolve timestamp mismatch issue`

- **docs:** → Documentation changes (README, comments, etc.)  
  _Example:_ `docs: update setup instructions in README`

- **style:** → Code style/formatting (no logic changes)  
  _Example:_ `style(ui): improve table alignment in dashboard`

- **refactor:** → Code changes that neither fix a bug nor add a feature  
  _Example:_ `refactor(jdbc): simplify query execution method`

- **test:** → Adding or modifying tests  
  _Example:_ `test(sync): add unit tests for merge conflicts`

- **chore:** → Maintenance tasks (build scripts, configs, dependencies, etc.)  
  _Example:_ `chore: configure Maven dependencies`

- **perf:** → Performance improvements  
  _Example:_ `perf(sync): optimize batch data merge speed`

- **ci:** → Continuous integration related  
  _Example:_ `ci: add GitHub Actions workflow for build`



