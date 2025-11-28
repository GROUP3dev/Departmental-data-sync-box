# LAN Connection Troubleshooting

If you see "Connection Refused" or "Communications link failure", follow these steps:

## 1. Check Server IP
Ensure the `db.host` in `resources/config/db.properties` on the Client PC matches the Server PC's IP address.
- Server PC: Run `ipconfig` in Command Prompt to find IPv4 Address.
- Client PC: Open `db.properties` and verify `db.host=YOUR_SERVER_IP`.

## 2. Check Windows Firewall (CRITICAL for "Connection Timed Out")
If you see **"Connection Timed Out"**, it means the Server PC is reachable but the Firewall is dropping the packets.

1.  **Open Windows Defender Firewall**:
# LAN DATABASE SETUP - SIMPLE GUIDE

## 🎯 GOAL
Make your application work on **multiple PCs** sharing the **same database**.

---

## 📍 STEP 1: FIND SERVER IP ADDRESS

**On the SERVER PC** (the one with XAMPP/MySQL):

1. Press `Windows Key` + `R`
2. Type: `cmd` → Press **Enter**
3. Type: `ipconfig` → Press **Enter**
4. Look for **"Wireless LAN adapter Wi-Fi"**
5. Find **"IPv4 Address"** → Example: `10.17.53.86`
6. **WRITE IT DOWN** ✍️

---

## 🔧 STEP 2: SETUP MYSQL (Server PC Only)

### A. Edit MySQL Config File

1. Open **XAMPP Control Panel**
2. Click **"Stop"** to stop MySQL
3. Click **"Config"** (next to MySQL) → Select **"my.ini"**
4. Press `Ctrl + F` to search
5. Search for: `bind-address`
6. You'll find: `bind-address = 127.0.0.1`
7. **Change it to:** `bind-address = 0.0.0.0`
8. Press `Ctrl + S` to **Save**
9. Close the file
10. Click **"Start"** to start MySQL again

### B. Run SQL Commands

1. In XAMPP, click **"Admin"** (next to MySQL)
2. This opens **phpMyAdmin** in your browser
3. Click the **"SQL"** tab at the top
4. **Copy this EXACTLY:**

```sql
CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
```

5. Click **"Go"**
6. You should see ✅ **"Query executed successfully"**

---

## 🔥 STEP 3: OPEN FIREWALL (Server PC Only)

1. Press `Windows Key`
2. Type: `firewall`
3. Click **"Windows Defender Firewall"**
4. Click **"Advanced settings"** (on the left side)
5. Click **"Inbound Rules"** (on the left side)
6. Click **"New Rule..."** (on the right side)
7. Now follow these **EXACTLY**:

   **Screen 1:**
   - Select: **"Port"**
   - Click: **Next**

   **Screen 2:**
   - Keep: **"TCP"** selected
   - Select: **"Specific local ports"**
   - Type: **3306**
   - Click: **Next**

   **Screen 3:**
   - Select: **"Allow the connection"**
   - Click: **Next**

   **Screen 4:**
   - ✅ Check **ALL THREE** boxes:
     - ✅ Domain
     - ✅ Private
     - ✅ Public
   - Click: **Next**

   **Screen 5:**
   - Name: **MySQL Access**
   - Click: **Finish**

---

## 💻 STEP 4: CONFIGURE CLIENT PCs

**Do this on EVERY other computer:**

1. Open your project folder
2. Navigate to: `resources/config/db.properties`
3. Right-click → Open with **Notepad**
4. Find the line: `db.host=localhost`
5. **Change it to YOUR SERVER IP** (from Step 1):
   ```
   db.host=10.17.53.86
   ```
   ⚠️ Replace `10.17.53.86` with YOUR actual IP!
6. Press `Ctrl + S` to **Save**
7. Close Notepad

---

## ✅ STEP 5: TEST IT

**On the CLIENT PC:**

1. Run your application
2. Try to login
3. If you see the login screen and can login → **SUCCESS!** ✅

---

## ❌ STILL NOT WORKING?

### Quick Test: Can Client See Server?

**On the CLIENT PC:**

1. Press `Windows Key` + `R`
2. Type: `cmd` → Press **Enter**
3. Type: `ping YOUR_SERVER_IP` (example: `ping 10.17.53.86`)
4. Press **Enter**

**If you see:**
- ✅ `Reply from 10.17.53.86...` → **Network is OK**, problem is MySQL/Firewall
- ❌ `Request timed out` → **Network problem**, check below

---

## 🆘 TROUBLESHOOTING

### Problem: Ping Times Out

**Both PCs must be on the SAME Wi-Fi!**

1. Check Server PC Wi-Fi name
2. Check Client PC Wi-Fi name
3. **They MUST be the same!**

### Problem: Can Ping but Can't Connect

**Firewall is blocking MySQL!**

**Try this:**
1. On **SERVER PC**, press `Windows Key`
2. Type: `firewall`
3. Click **"Windows Defender Firewall"**
4. Click: **"Turn Windows Defender Firewall on or off"**
5. Select **"Turn off"** for **Private** network
6. Try again from Client
7. If it works → Your firewall rule was wrong, redo **Step 3**
8. **Turn firewall back ON** after testing!

### Problem: Application Shows "Invalid Credentials"

**Connection is OK! Your login is wrong!**

Use these default credentials:
- Username: `admin`
- Password: `admin123`

---

## 📋 FINAL CHECKLIST

Before asking for help, verify:

- [ ] Server PC has XAMPP running ✅
- [ ] MySQL is **started** in XAMPP ✅
- [ ] You changed `bind-address` to `0.0.0.0` in my.ini ✅
- [ ] You ran the SQL commands in phpMyAdmin ✅
- [ ] You created firewall rule for port 3306 ✅
- [ ] Client PC has correct IP in `db.properties` ✅
- [ ] Both PCs are on same Wi-Fi network ✅
- [ ] You can `ping` server IP from client ✅



to run jar fil
"C:\Users\Ebed.Dev\.jdks\openjdk-25\bin\java.exe" -jar out\artifacts\DepartmentalDataSyncBox_jar\DepartmentalDataSyncBox.jar