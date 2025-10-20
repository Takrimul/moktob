# 🇧🇩 Practical Guide: Buying Cloud Server from Bangladesh

## 🎯 **Quick Start - Recommended Path**

### **Best Option for Beginners: DigitalOcean**
- ✅ Easy to use
- ✅ $200 free credits
- ✅ Accepts Bangladeshi credit cards
- ✅ Excellent documentation
- ✅ Good performance from Bangladesh

---

## 💳 **Payment Methods That Work**

### **1. Credit/Debit Cards (Easiest)**
**Cards that work internationally:**
- Dutch-Bangla Bank Visa/Mastercard
- BRAC Bank Visa/Mastercard
- City Bank Visa/Mastercard
- Eastern Bank Visa/Mastercard
- Standard Chartered Visa/Mastercard

**How to enable international transactions:**
1. Call your bank's customer service
2. Request to enable international online transactions
3. Set transaction limits
4. Test with a small amount first

### **2. PayPal (Alternative)**
- Create PayPal account
- Link your Bangladeshi bank account
- Add money to PayPal wallet
- Use PayPal to pay for services

### **3. Wise (Best Exchange Rates)**
- Create Wise account
- Transfer money from Bangladesh bank
- Get USD/EUR balance
- Use Wise card for payments

---

## 🚀 **Step-by-Step: DigitalOcean Setup**

### **Step 1: Create Account**
```bash
# Go to DigitalOcean
https://digitalocean.com

# Click "Sign Up"
# Enter email: your-email@gmail.com
# Create strong password
# Verify email address
```

### **Step 2: Add Payment Method**
```bash
# Go to Billing section
# Click "Add Payment Method"
# Enter your credit card details:
# - Card Number: 4xxx-xxxx-xxxx-xxxx
# - Expiry Date: MM/YY
# - CVV: xxx
# - Billing Address: Your Bangladesh address
```

### **Step 3: Create Your First Server**
```bash
# Click "Create" → "Droplets"
# Choose image: Ubuntu 22.04 LTS
# Choose plan: Basic ($6/month)
# Choose data center: Singapore (closest to Bangladesh)
# Authentication: Add SSH key (recommended) or password
# Hostname: moktob-server
# Click "Create Droplet"
```

### **Step 4: Connect to Your Server**
```bash
# Get your server IP from DigitalOcean dashboard
# SSH into your server
ssh root@YOUR_SERVER_IP

# Update system
apt update && apt upgrade -y
```

### **Step 5: Install Required Software**
```bash
# Install Java 17
apt install openjdk-17-jdk -y

# Install Maven
apt install maven -y

# Install Git
apt install git -y

# Install Nginx (for reverse proxy)
apt install nginx -y

# Install PostgreSQL
apt install postgresql postgresql-contrib -y

# Install Redis (optional)
apt install redis-server -y
```

### **Step 6: Configure Database**
```bash
# Switch to postgres user
sudo -u postgres psql

# Create database and user
CREATE DATABASE moktob_prod;
CREATE USER moktob_user WITH PASSWORD 'secure_password_123';
GRANT ALL PRIVILEGES ON DATABASE moktob_prod TO moktob_user;
\q
```

### **Step 7: Deploy Your Application**
```bash
# Create application directory
mkdir /opt/moktob
cd /opt/moktob

# Upload your JAR file (use SCP or Git)
# scp target/moktob-management-saas.jar root@YOUR_SERVER_IP:/opt/moktob/

# Create environment file
nano .env
```

**Environment file content:**
```bash
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://localhost:5432/moktob_prod
DB_USERNAME=moktob_user
DB_PASSWORD=secure_password_123
JWT_SECRET=your-super-secure-jwt-secret-key-min-32-chars-change-this
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

### **Step 8: Create Systemd Service**
```bash
# Create service file
nano /etc/systemd/system/moktob.service
```

**Service file content:**
```ini
[Unit]
Description=Moktob Management System
After=network.target postgresql.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/moktob
EnvironmentFile=/opt/moktob/.env
ExecStart=/usr/bin/java -jar /opt/moktob/moktob-management-saas.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### **Step 9: Start Your Application**
```bash
# Reload systemd
systemctl daemon-reload

# Enable service
systemctl enable moktob.service

# Start service
systemctl start moktob.service

# Check status
systemctl status moktob.service

# View logs
journalctl -u moktob.service -f
```

### **Step 10: Configure Nginx**
```bash
# Create Nginx configuration
nano /etc/nginx/sites-available/moktob
```

**Nginx configuration:**
```nginx
server {
    listen 80;
    server_name YOUR_SERVER_IP;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# Enable site
ln -s /etc/nginx/sites-available/moktob /etc/nginx/sites-enabled/

# Test configuration
nginx -t

# Restart Nginx
systemctl restart nginx
```

---

## 🔒 **Security Setup**

### **1. Configure Firewall**
```bash
# Install UFW
apt install ufw -y

# Allow SSH
ufw allow ssh

# Allow HTTP and HTTPS
ufw allow 80
ufw allow 443

# Enable firewall
ufw enable
```

### **2. Setup SSL Certificate (Optional)**
```bash
# Install Certbot
apt install certbot python3-certbot-nginx -y

# Get SSL certificate
certbot --nginx -d your-domain.com

# Auto-renewal
crontab -e
# Add: 0 12 * * * /usr/bin/certbot renew --quiet
```

---

## 📊 **Monitoring Setup**

### **1. Install Monitoring Tools**
```bash
# Install htop for system monitoring
apt install htop -y

# Install netstat for network monitoring
apt install net-tools -y

# Install curl for health checks
apt install curl -y
```

### **2. Create Health Check Script**
```bash
# Create health check script
nano /opt/moktob/health-check.sh
```

**Health check script:**
```bash
#!/bin/bash
# Health check script for Moktob application

APP_URL="http://localhost:8080/moktob/actuator/health"
LOG_FILE="/var/log/moktob-health.log"

# Check if application is responding
if curl -f -s "$APP_URL" > /dev/null; then
    echo "$(date): Application is healthy" >> "$LOG_FILE"
else
    echo "$(date): Application is down, restarting..." >> "$LOG_FILE"
    systemctl restart moktob.service
fi
```

```bash
# Make script executable
chmod +x /opt/moktob/health-check.sh

# Add to crontab (check every 5 minutes)
crontab -e
# Add: */5 * * * * /opt/moktob/health-check.sh
```

---

## 💰 **Cost Breakdown**

### **DigitalOcean $6/month Droplet**
- **Server**: $6/month (~৳600)
- **Domain**: $12/year (~৳100/month)
- **SSL Certificate**: Free (Let's Encrypt)
- **Total**: ~৳700/month (~$7)

### **Additional Services (Optional)**
- **Managed Database**: $15/month (~৳1,500)
- **Load Balancer**: $12/month (~৳1,200)
- **Backup**: $1/month (~৳100)

---

## 🆘 **Troubleshooting**

### **Common Issues**

#### **1. Payment Declined**
```bash
# Solutions:
# - Call your bank to enable international transactions
# - Try different card
# - Use PayPal instead
# - Contact DigitalOcean support
```

#### **2. Can't Connect to Server**
```bash
# Check if server is running
# Check firewall settings
# Verify SSH key/password
# Check server IP address
```

#### **3. Application Won't Start**
```bash
# Check logs
journalctl -u moktob.service -f

# Check Java installation
java -version

# Check database connection
sudo -u postgres psql -c "SELECT 1;"

# Check port availability
netstat -tulpn | grep :8080
```

#### **4. Database Connection Failed**
```bash
# Check PostgreSQL status
systemctl status postgresql

# Check database exists
sudo -u postgres psql -c "\l"

# Check user permissions
sudo -u postgres psql -c "\du"
```

---

## 📞 **Getting Help**

### **DigitalOcean Support**
- **Community**: https://www.digitalocean.com/community
- **Documentation**: https://docs.digitalocean.com
- **Support Tickets**: Available in dashboard

### **Bangladesh Developer Communities**
- **Facebook Groups**: Search "Bangladesh Developers"
- **GitHub**: Bangladesh developer organizations
- **Discord**: Bangladesh tech communities

### **Local Support**
- **BDIX**: Direct phone support
- **DataSoft**: Local technical support
- **SSL Wireless**: Enterprise support

---

## 🎯 **Quick Commands Reference**

```bash
# Server management
systemctl status moktob.service
systemctl restart moktob.service
systemctl stop moktob.service
systemctl start moktob.service

# Logs
journalctl -u moktob.service -f
tail -f /var/log/moktob-health.log

# Database
sudo -u postgres psql
sudo -u postgres psql -d moktob_prod

# Nginx
nginx -t
systemctl restart nginx
systemctl status nginx

# Firewall
ufw status
ufw allow 8080
ufw deny 8080

# System monitoring
htop
df -h
free -h
```

---

## ✅ **Deployment Checklist**

- [ ] DigitalOcean account created
- [ ] Payment method added
- [ ] Server created (Singapore data center)
- [ ] SSH access working
- [ ] Java 17 installed
- [ ] Maven installed
- [ ] PostgreSQL installed and configured
- [ ] Application JAR uploaded
- [ ] Environment variables configured
- [ ] Systemd service created
- [ ] Nginx configured
- [ ] Firewall configured
- [ ] SSL certificate installed (optional)
- [ ] Monitoring setup
- [ ] Health checks working
- [ ] Application accessible via browser

---

*Follow this guide step by step, and you'll have your Moktob Management System running on a cloud server within a few hours!*
