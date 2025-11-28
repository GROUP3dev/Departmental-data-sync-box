# 🌐 LAN Setup Without Internet/Router

## ✅ Your App Works WITHOUT Internet!

**LAN = Local Area Network** - computers talk to each other directly, **NO internet needed**.

You just need the computers to be on the **same network**.

---

## 🔥 METHOD 1: MOBILE HOTSPOT (Easiest!)

This is what you're using now (IP: `192.168.43.252`)

### On SERVER PC (with MySQL):

1. Press `Windows Key` + `I` (opens Settings)
2. Click **"Network & Internet"**
3. Click **"Mobile hotspot"**
4. Turn it **ON** ✅
5. Set:
   - **Network name:** `MySyncBox`
   - **Network password:** (set a password)
6. Keep this window open to see connected devices

### On CLIENT PCs:

1. Open WiFi settings
2. Connect to **"MySyncBox"**
3. Enter the password
4. Open app and login

### ✅ Advantages:
- ✅ No router needed
- ✅ No internet needed
- ✅ Works anywhere
- ✅ Can connect multiple devices

---

## ⚡ METHOD 2: ETHERNET CABLE (Direct Connection)

Connect 2 computers directly with an Ethernet cable.

### Setup:

1. Connect both PCs with Ethernet cable
2. On **SERVER PC**:
   - Press `Windows Key` + `R`
   - Type: `ncpa.cpl` → **Enter**
   - Right-click **"Ethernet"** → **Properties**
   - Select **"Internet Protocol Version 4 (TCP/IPv4)"**
   - Click **"Properties"**
   - Select **"Use the following IP address"**
   - Enter:
     - IP address: `192.168.100.1`
     - Subnet mask: `255.255.255.0`
   - Click **OK**

3. On **CLIENT PC**:
   - Do the same steps
   - Enter:
     - IP address: `192.168.100.2`
     - Subnet mask: `255.255.255.0`
     - Default gateway: `192.168.100.1`
   - Click **OK**

4. Update `db.properties` on CLIENT:
   ```
   db.host=192.168.100.1
   ```

### ✅ Advantages:
- ✅ Very fast
- ✅ Most secure
- ✅ No WiFi needed

### ❌ Limitations:
- ❌ Only 2 computers
- ❌ Need Ethernet cable

---

## 📡 METHOD 3: AD-HOC WIFI (Without Router)

Create a WiFi network from your PC.

### On SERVER PC:

1. Press `Windows Key` + `R`
2. Type: `cmd` (run as Administrator)
3. Type these commands:

```cmd
netsh wlan set hostednetwork mode=allow ssid=SyncBoxNetwork key=password123
netsh wlan start hostednetwork
```

4. Find your IP:
   ```cmd
   ipconfig
   ```
   Look for "Wireless LAN adapter Local Area Connection*"

### On CLIENT PCs:

1. Connect to WiFi: **"SyncBoxNetwork"**
2. Password: `password123`
3. Update `db.properties` with SERVER IP

### ✅ Advantages:
- ✅ Multiple devices
- ✅ No router needed

### ❌ Limitations:
- ❌ Some Windows versions don't support this
- ❌ Mobile Hotspot is easier

---

## 🎯 CURRENT SETUP (Mobile Hotspot)

Based on your IP `192.168.43.252`, you're using **Mobile Hotspot**! ✅

### What You Need to Do:

**On SERVER PC (IP: 192.168.43.252):**

1. ✅ Keep Mobile Hotspot **ON**
2. ✅ Make sure XAMPP MySQL is **running**
3. ✅ Configure MySQL (see TROUBLESHOOTING.md)
4. ✅ Open Firewall for port 3306

**On CLIENT PC:**

1. ✅ Connect to the same hotspot
2. ✅ Set `db.host=192.168.43.252` in `db.properties`
3. ✅ Run the app

---

## 📝 IMPORTANT NOTES

### ⚠️ IP Address Changes

Mobile Hotspot IPs can change! If the SERVER PC restarts:

1. On SERVER, run: `ipconfig`
2. Find the new IP under "Wireless LAN adapter Local Area Connection*"
3. Update CLIENT's `db.properties` with new IP

### 💡 TIP: Use Static IP

**On SERVER PC:**

1. Press `Windows Key` + `R`
2. Type: `ncpa.cpl` → **Enter**
3. Right-click your hotspot adapter → **Properties**
4. Select **"Internet Protocol Version 4 (TCP/IPv4)"**
5. Click **"Properties"**
6. Select **"Use the following IP address"**
7. Set:
   - IP: `192.168.137.1` (common hotspot IP)
   - Subnet: `255.255.255.0`
8. Now the IP never changes! ✅

---

## ✅ VERIFICATION

Test if LAN is working:

**On CLIENT PC:**

```cmd
ping 192.168.43.252
```

Should see:
```
Reply from 192.168.43.252: bytes=32 time<1ms TTL=128
```

✅ **Success!** LAN is working (no internet needed!)

---

## 🆘 TROUBLESHOOTING

### Problem: Can't Connect to Hotspot

**Solution:**
- Turn hotspot OFF then ON again
- Check password is correct
- Restart both PCs

### Problem: Can Ping but Can't Connect to MySQL

**Solution:**
- Check MySQL is running (XAMPP)
- Check `bind-address = 0.0.0.0` in my.ini
- Check firewall allows port 3306
- Try turning firewall OFF to test

### Problem: IP Keeps Changing

**Solution:**
- Use static IP (see "TIP: Use Static IP" above)
- Or: Create a startup script to show IP

---

## 📋 FINAL CHECKLIST

Before starting:

- [ ] SERVER: Mobile hotspot is ON ✅
- [ ] SERVER: MySQL is running in XAMPP ✅
- [ ] SERVER: `bind-address = 0.0.0.0` in my.ini ✅
- [ ] SERVER: Firewall allows port 3306 ✅
- [ ] CLIENT: Connected to same hotspot ✅
- [ ] CLIENT: Correct IP in `db.properties` ✅
- [ ] CLIENT: Can `ping` server IP ✅

---

## 🎉 DONE!

Your app now works on **LAN without internet**! All computers just need to be on the same local network (hotspot, direct cable, or ad-hoc WiFi).

**No router needed. No internet needed.** ✅
