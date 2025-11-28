# 🔧 CORRECT db.properties SETUP

## ⚠️ IMPORTANT: Different Config for SERVER vs CLIENT!

---

## 📌 **ON THE SERVER PC** (Computer with MySQL/XAMPP)

**File:** `resources/config/db.properties`

```properties
db.host=localhost
```

✅ **Always use `localhost`** - the server connects to its own database!

---

## 📌 **ON CLIENT PCs** (Other computers)

**File:** `resources/config/db.properties`

```properties
db.host=192.168.43.252
```

⚠️ **Replace `192.168.43.252` with YOUR SERVER'S ACTUAL IP!**

### How to Find Server IP:

**On the SERVER PC:**
1. Press `Windows + R`
2. Type: `cmd` → Enter
3. Type: `ipconfig` → Enter
4. Look for **"IPv4 Address"** under your network adapter
5. Example: `192.168.43.252` or `10.17.53.86`

---

## ✅ **QUICK CHECKLIST**

### Server PC Setup:
- [x] `db.host=localhost` ✅ **(DONE!)**
- [ ] XAMPP MySQL is **running**
- [ ] MySQL `bind-address = 0.0.0.0` in `my.ini`
- [ ] Firewall allows port 3306
- [ ] Mobile hotspot is **ON** (or same WiFi as clients)

### Client PC Setup:
- [ ] Copy project folder to client PC
- [ ] Change `db.host` to SERVER's IP
- [ ] Connect to same network as server
- [ ] Run the app

---

## 🎯 **SIMPLIFIED STEPS**

### **STEP 1: SERVER PC**
1. Make sure `db.host=localhost` ✅ **(Already done!)**
2. Open XAMPP → Start MySQL
3. Turn ON Mobile Hotspot (Settings → Network)

### **STEP 2: CLIENT PC**  
1. Connect to SERVER's hotspot
2. Change `db.host=192.168.43.252` (use SERVER's IP)
3. Run the app

---

## ❌ **COMMON MISTAKES**

### ❌ WRONG: Server using remote IP
```properties
# ON SERVER PC - WRONG!
db.host=192.168.43.252  ❌
```

### ✅ CORRECT: Server using localhost
```properties
# ON SERVER PC - CORRECT!
db.host=localhost  ✅
```

---

## 🆘 **TROUBLESHOOTING**

### Error: "Connection timed out" on SERVER
**Cause:** Server is trying to connect to a remote IP instead of localhost.
**Fix:** Change `db.host=localhost` on SERVER PC ✅ **(Already fixed!)**

### Error: "Connection refused" on CLIENT
**Cause:** MySQL not configured for remote access.
**Fix:**
1. Edit `my.ini`: `bind-address = 0.0.0.0`
2. Run SQL: `GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';`
3. Open firewall port 3306

### Error: Can't find server IP
**On SERVER PC, run:**
```cmd
ipconfig
```
Look for IPv4 Address.

---

## ✅ **NOW TRY AGAIN!**

1. **On SERVER PC:** Run your app now! It should work! ✅
2. **On CLIENT PC:** Update `db.host` to server IP, then run!

**No internet needed - LAN works offline!** 🎉
