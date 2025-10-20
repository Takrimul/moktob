# 🚀 Moktob Management System - Quick Deployment Guide

## 📋 Prerequisites

Before deploying, ensure you have:

- **Java 17+** installed
- **Maven 3.6+** installed
- **Docker** and **Docker Compose** (for containerized deployment)
- **Git** for version control
- **Cloud CLI tools** (optional, for specific platforms)

## 🚀 Quick Start

### Option 1: Automated Deployment Script

The easiest way to deploy is using our automated script:

```bash
# Make the script executable
chmod +x deploy.sh

# Run the deployment script
./deploy.sh
```

The script will:
1. ✅ Check prerequisites
2. 🔨 Build your application
3. 🐳 Create Docker images
4. ☁️ Deploy to your chosen platform

### Option 2: Manual Docker Deployment

```bash
# 1. Build the application
mvn clean package -DskipTests

# 2. Build Docker image
docker build -t moktob-app:latest .

# 3. Start services with Docker Compose
docker-compose up -d

# 4. Check status
docker-compose ps
```

### Option 3: Cloud Platform Deployment

Choose your preferred platform:

#### 🌟 Heroku (Recommended for beginners)
```bash
# Install Heroku CLI
# Create app
heroku create moktob-app

# Add PostgreSQL
heroku addons:create heroku-postgresql:hobby-dev

# Set environment variables
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set JWT_SECRET=$(openssl rand -base64 32)

# Deploy
git push heroku main
```

#### ☁️ AWS Elastic Beanstalk
```bash
# Install EB CLI
pip install awsebcli

# Initialize and deploy
eb init moktob-app --platform java
eb create prod
eb deploy
```

#### 🔵 Google Cloud Run
```bash
# Build and deploy
gcloud builds submit --tag gcr.io/PROJECT_ID/moktob-app
gcloud run deploy --image gcr.io/PROJECT_ID/moktob-app --platform managed
```

## 🔧 Configuration

### Environment Variables

Set these environment variables in your deployment platform:

```bash
# Database
DATABASE_URL=jdbc:postgresql://your-db-host:5432/moktob_prod
DB_USERNAME=moktob_user
DB_PASSWORD=your_secure_password

# JWT Security
JWT_SECRET=your-super-secure-jwt-secret-key-min-32-chars
JWT_EXPIRATION=86400000

# Email (optional)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Redis (optional)
REDIS_HOST=your-redis-host
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password
```

### Production Configuration

Copy `application-prod.properties` to `src/main/resources/`:

```bash
cp application-prod.properties src/main/resources/
```

## 🗄️ Database Setup

### PostgreSQL Setup

```sql
-- Create database and user
CREATE DATABASE moktob_prod;
CREATE USER moktob_user WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE moktob_prod TO moktob_user;

-- Connect and grant schema privileges
\c moktob_prod;
GRANT ALL ON SCHEMA public TO moktob_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO moktob_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO moktob_user;
```

### Database Migration

The application will automatically create tables on first run with `spring.jpa.hibernate.ddl-auto=update`.

For production, use `validate` and run migrations manually:

```bash
# Set to validate mode
export SPRING_JPA_HIBERNATE_DDL_AUTO=validate

# Run application to validate schema
java -jar target/moktob-management-saas.jar --spring.profiles.active=prod
```

## 🔒 Security Configuration

### SSL/TLS Setup

1. **Obtain SSL Certificate**:
   - Use Let's Encrypt (free)
   - Purchase from certificate authority
   - Use cloud provider certificates

2. **Configure Nginx** (if using):
   ```bash
   # Place certificates in ssl/ directory
   mkdir ssl
   cp your-cert.crt ssl/moktob.crt
   cp your-key.key ssl/moktob.key
   ```

3. **Update Docker Compose**:
   ```yaml
   environment:
     - SSL_ENABLED=true
     - SSL_KEY_STORE=/etc/ssl/certs/moktob.p12
     - SSL_KEY_STORE_PASSWORD=your-password
   ```

### Environment Security

- ✅ Never commit secrets to version control
- ✅ Use environment variables for sensitive data
- ✅ Rotate secrets regularly
- ✅ Use cloud provider secret management
- ✅ Enable HTTPS/SSL
- ✅ Configure proper CORS policies

## 📊 Monitoring & Maintenance

### Health Checks

Access health endpoints:

```bash
# Application health
curl http://localhost:8080/moktob/actuator/health

# Detailed health info
curl http://localhost:8080/moktob/actuator/health/readiness
curl http://localhost:8080/moktob/actuator/health/liveness
```

### Monitoring Stack

The Docker Compose includes:

- **Prometheus**: Metrics collection
- **Grafana**: Dashboards and visualization
- **Nginx**: Reverse proxy and load balancing

Access monitoring:
- Grafana: http://localhost:3000 (admin/admin123)
- Prometheus: http://localhost:9090

### Log Management

```bash
# View application logs
docker-compose logs -f moktob-app

# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f db
```

## 🔧 Troubleshooting

### Common Issues

#### 1. Database Connection Failed
```bash
# Check database status
docker-compose ps db

# Check database logs
docker-compose logs db

# Test connection
docker-compose exec db psql -U moktob_user -d moktob_prod -c "SELECT 1;"
```

#### 2. Application Won't Start
```bash
# Check application logs
docker-compose logs moktob-app

# Check environment variables
docker-compose exec moktob-app env | grep -E "(DATABASE|JWT|MAIL)"

# Restart application
docker-compose restart moktob-app
```

#### 3. Port Conflicts
```bash
# Check port usage
netstat -tulpn | grep :8080

# Kill process using port
sudo kill -9 $(lsof -t -i:8080)

# Use different port
docker-compose up -d --scale moktob-app=0
docker-compose up -d -p 8081:8080 moktob-app
```

#### 4. Memory Issues
```bash
# Check memory usage
docker stats

# Increase memory limits in docker-compose.yml
services:
  moktob-app:
    deploy:
      resources:
        limits:
          memory: 2G
        reservations:
          memory: 1G
```

### Performance Optimization

#### 1. Database Optimization
```sql
-- Create indexes for better performance
CREATE INDEX idx_student_class_id ON students(current_class_id);
CREATE INDEX idx_attendance_date ON attendance(attendance_date);
CREATE INDEX idx_attendance_student ON attendance(student_id);
```

#### 2. JVM Tuning
```bash
# Add to docker-compose.yml environment
- JAVA_OPTS=-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

#### 3. Connection Pool Tuning
```properties
# In application-prod.properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
```

## 📈 Scaling

### Horizontal Scaling

```yaml
# Scale application instances
docker-compose up -d --scale moktob-app=3

# Use load balancer
nginx:
  image: nginx:alpine
  ports:
    - "80:80"
  volumes:
    - ./nginx/nginx.conf:/etc/nginx/nginx.conf
```

### Vertical Scaling

```yaml
# Increase resources
services:
  moktob-app:
    deploy:
      resources:
        limits:
          memory: 4G
          cpus: '2'
```

## 🔄 Backup & Recovery

### Database Backup

```bash
#!/bin/bash
# backup-db.sh

# Create backup
docker-compose exec db pg_dump -U moktob_user moktob_prod > backup_$(date +%Y%m%d_%H%M%S).sql

# Compress backup
gzip backup_$(date +%Y%m%d_%H%M%S).sql

# Upload to cloud storage (optional)
# aws s3 cp backup_*.sql.gz s3://moktob-backups/
```

### Application Backup

```bash
#!/bin/bash
# backup-app.sh

# Backup application data
docker-compose exec moktob-app tar -czf /tmp/app-data-$(date +%Y%m%d).tar.gz /var/log/moktob

# Copy backup
docker cp moktob-app:/tmp/app-data-$(date +%Y%m%d).tar.gz ./
```

## 📞 Support

### Getting Help

1. **Check Logs**: Always check application logs first
2. **Health Checks**: Use actuator endpoints to diagnose issues
3. **Documentation**: Refer to DEPLOYMENT_GUIDE.md for detailed instructions
4. **Community**: Check GitHub issues and discussions

### Useful Commands

```bash
# Quick health check
curl -f http://localhost:8080/moktob/actuator/health || echo "Health check failed"

# View all services
docker-compose ps

# Restart all services
docker-compose restart

# Update and restart
docker-compose pull && docker-compose up -d

# Clean up
docker-compose down -v
docker system prune -f
```

---

## 🎯 Next Steps

After successful deployment:

1. ✅ **Test all functionality** - Login, CRUD operations, file uploads
2. ✅ **Configure monitoring** - Set up alerts and dashboards
3. ✅ **Setup backups** - Implement automated backup strategy
4. ✅ **Security audit** - Review security configurations
5. ✅ **Performance testing** - Load test your application
6. ✅ **Documentation** - Update deployment documentation

---

*Happy Deploying! 🚀*

For detailed deployment instructions, see [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)
