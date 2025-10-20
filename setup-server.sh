#!/bin/bash

# 🇧🇩 Moktob Management System - Server Setup Script
# This script helps you set up your cloud server for deployment

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if running as root
check_root() {
    if [ "$EUID" -ne 0 ]; then
        print_error "Please run this script as root (use sudo)"
        exit 1
    fi
}

# Function to update system
update_system() {
    print_status "Updating system packages..."
    apt update && apt upgrade -y
    print_success "System updated successfully!"
}

# Function to install Java
install_java() {
    print_status "Installing Java 17..."
    apt install openjdk-17-jdk -y
    java -version
    print_success "Java 17 installed successfully!"
}

# Function to install Maven
install_maven() {
    print_status "Installing Maven..."
    apt install maven -y
    mvn -version
    print_success "Maven installed successfully!"
}

# Function to install Git
install_git() {
    print_status "Installing Git..."
    apt install git -y
    git --version
    print_success "Git installed successfully!"
}

# Function to install Nginx
install_nginx() {
    print_status "Installing Nginx..."
    apt install nginx -y
    systemctl enable nginx
    systemctl start nginx
    systemctl status nginx --no-pager
    print_success "Nginx installed and started successfully!"
}

# Function to install PostgreSQL
install_postgresql() {
    print_status "Installing PostgreSQL..."
    apt install postgresql postgresql-contrib -y
    systemctl enable postgresql
    systemctl start postgresql
    systemctl status postgresql --no-pager
    print_success "PostgreSQL installed and started successfully!"
}

# Function to configure PostgreSQL
configure_postgresql() {
    print_status "Configuring PostgreSQL database..."
    
    # Create database and user
    sudo -u postgres psql << EOF
CREATE DATABASE moktob_prod;
CREATE USER moktob_user WITH PASSWORD 'secure_password_123';
GRANT ALL PRIVILEGES ON DATABASE moktob_prod TO moktob_user;
\q
EOF
    
    print_success "PostgreSQL configured successfully!"
    print_warning "Database password: secure_password_123"
    print_warning "Please change this password in production!"
}

# Function to install Redis
install_redis() {
    print_status "Installing Redis..."
    apt install redis-server -y
    systemctl enable redis-server
    systemctl start redis-server
    systemctl status redis-server --no-pager
    print_success "Redis installed and started successfully!"
}

# Function to install monitoring tools
install_monitoring() {
    print_status "Installing monitoring tools..."
    apt install htop net-tools curl wget -y
    print_success "Monitoring tools installed successfully!"
}

# Function to configure firewall
configure_firewall() {
    print_status "Configuring firewall..."
    apt install ufw -y
    
    # Allow SSH
    ufw allow ssh
    
    # Allow HTTP and HTTPS
    ufw allow 80
    ufw allow 443
    
    # Allow application port
    ufw allow 8080
    
    # Enable firewall
    ufw --force enable
    
    print_success "Firewall configured successfully!"
}

# Function to create application directory
create_app_directory() {
    print_status "Creating application directory..."
    mkdir -p /opt/moktob
    mkdir -p /var/log/moktob
    print_success "Application directory created successfully!"
}

# Function to create environment file
create_env_file() {
    print_status "Creating environment file..."
    
    cat > /opt/moktob/.env << 'EOF'
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://localhost:5432/moktob_prod
DB_USERNAME=moktob_user
DB_PASSWORD=secure_password_123
JWT_SECRET=your-super-secure-jwt-secret-key-min-32-chars-change-this
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
REDIS_HOST=localhost
REDIS_PORT=6379
LOG_FILE_PATH=/var/log/moktob/application.log
EOF
    
    print_success "Environment file created successfully!"
    print_warning "Please update the environment file with your actual values!"
}

# Function to create systemd service
create_systemd_service() {
    print_status "Creating systemd service..."
    
    cat > /etc/systemd/system/moktob.service << 'EOF'
[Unit]
Description=Moktob Management System
After=network.target postgresql.service redis-server.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/moktob
EnvironmentFile=/opt/moktob/.env
ExecStart=/usr/bin/java -jar /opt/moktob/moktob-management-saas.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF
    
    systemctl daemon-reload
    print_success "Systemd service created successfully!"
}

# Function to configure Nginx
configure_nginx() {
    print_status "Configuring Nginx..."
    
    cat > /etc/nginx/sites-available/moktob << 'EOF'
server {
    listen 80;
    server_name _;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # Main application
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }

    # Health check endpoint
    location /moktob/actuator/health {
        access_log off;
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
    }

    # Static files caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
    }
}
EOF
    
    # Enable site
    ln -sf /etc/nginx/sites-available/moktob /etc/nginx/sites-enabled/
    
    # Remove default site
    rm -f /etc/nginx/sites-enabled/default
    
    # Test configuration
    nginx -t
    
    # Restart Nginx
    systemctl restart nginx
    
    print_success "Nginx configured successfully!"
}

# Function to create health check script
create_health_check() {
    print_status "Creating health check script..."
    
    cat > /opt/moktob/health-check.sh << 'EOF'
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
    sleep 30
    if curl -f -s "$APP_URL" > /dev/null; then
        echo "$(date): Application restarted successfully" >> "$LOG_FILE"
    else
        echo "$(date): Application restart failed" >> "$LOG_FILE"
    fi
fi
EOF
    
    chmod +x /opt/moktob/health-check.sh
    
    # Add to crontab
    (crontab -l 2>/dev/null; echo "*/5 * * * * /opt/moktob/health-check.sh") | crontab -
    
    print_success "Health check script created successfully!"
}

# Function to show final instructions
show_final_instructions() {
    echo ""
    print_success "🎉 Server setup completed successfully!"
    echo ""
    print_status "Next steps:"
    echo "1. Upload your JAR file to /opt/moktob/"
    echo "2. Update environment variables in /opt/moktob/.env"
    echo "3. Start your application: systemctl start moktob.service"
    echo "4. Check status: systemctl status moktob.service"
    echo "5. View logs: journalctl -u moktob.service -f"
    echo ""
    print_status "Useful commands:"
    echo "- Start service: systemctl start moktob.service"
    echo "- Stop service: systemctl stop moktob.service"
    echo "- Restart service: systemctl restart moktob.service"
    echo "- Check status: systemctl status moktob.service"
    echo "- View logs: journalctl -u moktob.service -f"
    echo "- Test health: curl http://localhost:8080/moktob/actuator/health"
    echo ""
    print_warning "Important:"
    echo "- Change database password in production"
    echo "- Update JWT secret in production"
    echo "- Configure SSL certificate for HTTPS"
    echo "- Set up proper backup strategy"
    echo ""
    print_status "Your application will be available at: http://YOUR_SERVER_IP"
}

# Main function
main() {
    echo -e "${GREEN}🇧🇩 Moktob Management System - Server Setup Script${NC}"
    echo ""
    
    # Check if running as root
    check_root
    
    # Update system
    update_system
    
    # Install required software
    install_java
    install_maven
    install_git
    install_nginx
    install_postgresql
    configure_postgresql
    install_redis
    install_monitoring
    
    # Configure services
    configure_firewall
    create_app_directory
    create_env_file
    create_systemd_service
    configure_nginx
    create_health_check
    
    # Show final instructions
    show_final_instructions
}

# Run main function
main "$@"
