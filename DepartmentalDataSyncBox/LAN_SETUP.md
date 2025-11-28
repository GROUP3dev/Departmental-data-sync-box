# LAN Setup Guide

To allow other computers (Clients) to connect to your PC (Server), follow these steps:

## 1. Server Configuration (Your PC)

### A. Enable Remote Root Access
1. Open your database management tool (e.g., Workbench).
2. Open `database/setup_remote_access.sql`.
3. **Important**: Replace `'YOUR_ROOT_PASSWORD'` in the script with your actual root password.
4. Run the script.
   - This allows the `root` user to connect from other computers.

### B. Configure Windows Firewall
1. Open **Windows Defender Firewall with Advanced Security**.
2. Click **Inbound Rules** > **New Rule**.
3. Select **Port** > **Next**.
4. Select **TCP** and enter **3306** in "Specific local ports".
5. Select **Allow the connection** > **Next**.
6. Check Domain, Private, and Public > **Next**.
7. Name it "MySQL LAN Access" > **Finish**.

### C. Find Your IP Address
1. Open Command Prompt (`cmd`).
2. Type `ipconfig`.
3. Note the **IPv4 Address** (e.g., `192.168.0.88`).

## 2. Client Configuration (Other PCs)

On each client machine that runs the app:

1. Open `resources/config/db.properties`.
2. Update the `db.host` to your Server's IP:
   ```properties
   db.host=192.168.0.88  <-- Replace with your actual Server IP
   ```
3. Update the user/password to your root credentials:
   ```properties
   db.user=root
   db.password=YOUR_ROOT_PASSWORD
   ```
4. Save the file.

Now the client app will connect to the database on your PC.
