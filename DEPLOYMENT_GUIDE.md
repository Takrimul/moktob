# 🚀 Moktob Management System - Cloud Deployment Guide

## 📋 Table of Contents
1. [Pre-Deployment Checklist](#pre-deployment-checklist)
2. [Environment Configuration](#environment-configuration)
3. [Database Setup](#database-setup)
4. [Cloud Platform Options](#cloud-platform-options)
5. [Deployment Strategies](#deployment-strategies)
6. [Security Considerations](#security-considerations)
7. [Monitoring & Maintenance](#monitoring--maintenance)
8. [Troubleshooting](#troubleshooting)

---

## ✅ Pre-Deployment Checklist

### Code Preparation
- [ ] All tests passing (`mvn test`)
- [ ] Application compiles successfully (`mvn clean compile`)
- [ ] No hardcoded local URLs or credentials
- [ ] Environment-specific configurations externalized
- [ ] Database migrations ready
- [ ] Static assets optimized
- [ ] Error handling implemented
- [ ] Logging configured properly

### Security Checklist
- [ ] Environment variables for sensitive data
- [ ] HTTPS/SSL certificates configured
- [ ] Database credentials secured
- [ ] API endpoints protected
- [ ] CORS policies configured
- [ ] Input validation implemented
- [ ] SQL injection prevention verified

---

## 🔧 Environment Configuration

### 1. Application Properties

Create environment-specific configuration files:

#### `application-prod.properties`
```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/moktob

# Database Configuration
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=false

# Redis Configuration (if using)
spring.redis.host=${REDIS_HOST}
spring.redis.port=${REDIS_PORT}
spring.redis.password=${REDIS_PASSWORD}

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}

# Email Configuration
spring.mail.host=${MAIL_HOST}
spring.mail.port=${MAIL_PORT}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging Configuration
logging.level.com.moktob=INFO
logging.level.org.springframework.security=WARN
logging.file.name=${LOG_FILE_PATH}

# Management Endpoints
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

### 2. Environment Variables

Set these environment variables in your cloud platform:

```bash
# Database
DATABASE_URL=jdbc:postgresql://your-db-host:5432/moktob_prod
DB_USERNAME=your_db_user
DB_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your-super-secure-jwt-secret-key-min-32-chars
JWT_EXPIRATION=86400000

# Redis (optional)
REDIS_HOST=your-redis-host
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Logging
LOG_FILE_PATH=/var/log/moktob/application.log
```

---

## 🗄️ Database Setup

### PostgreSQL Setup

#### 1. Create Production Database
```sql
-- Connect to PostgreSQL as superuser
CREATE DATABASE moktob_prod;
CREATE USER moktob_user WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE moktob_prod TO moktob_user;

-- Connect to the database
\c moktob_prod;

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO moktob_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO moktob_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO moktob_user;
```

#### 2. Database Migration Script
```bash
#!/bin/bash
# migrate-db.sh

echo "Starting database migration..."

# Run Flyway migrations (if using Flyway)
flyway -url=$DATABASE_URL -user=$DB_USERNAME -password=$DB_PASSWORD migrate

# Or run Spring Boot migrations
java -jar moktob-management-saas.jar --spring.profiles.active=prod --spring.jpa.hibernate.ddl-auto=update

echo "Database migration completed!"
```

---

## ☁️ Cloud Platform Options

### 1. AWS (Amazon Web Services)

#### Option A: AWS Elastic Beanstalk
```bash
# Install EB CLI
pip install awsebcli

# Initialize EB application
eb init moktob-app --platform java --region us-east-1

# Create environment
eb create prod --envvars DATABASE_URL=$DATABASE_URL,DB_USERNAME=$DB_USERNAME,DB_PASSWORD=$DB_PASSWORD

# Deploy
eb deploy
```

#### Option B: AWS EC2 with Docker
```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/moktob-management-saas.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

```bash
# Build and deploy
docker build -t moktob-app .
docker run -d -p 8080:8080 --env-file .env moktob-app
```

#### Option C: AWS ECS/Fargate
```yaml
# docker-compose.yml
version: '3.8'
services:
  moktob-app:
    image: your-registry/moktob-app:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DATABASE_URL=${DATABASE_URL}
      - DB_USERNAME=${DB_USERNAME}
      - DB_PASSWORD=${DB_PASSWORD}
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/moktob/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

### 2. Google Cloud Platform (GCP)

#### Option A: Google App Engine
```yaml
# app.yaml
runtime: java17
env: standard

env_variables:
  SPRING_PROFILES_ACTIVE: prod
  DATABASE_URL: ${DATABASE_URL}
  DB_USERNAME: ${DB_USERNAME}
  DB_PASSWORD: ${DB_PASSWORD}

automatic_scaling:
  min_instances: 1
  max_instances: 10
  target_cpu_utilization: 0.6

handlers:
- url: /.*
  script: auto
```

```bash
# Deploy to App Engine
gcloud app deploy
```

#### Option B: Google Cloud Run
```bash
# Build and deploy
gcloud builds submit --tag gcr.io/PROJECT_ID/moktob-app
gcloud run deploy --image gcr.io/PROJECT_ID/moktob-app --platform managed --region us-central1
```

### 3. Microsoft Azure

#### Option A: Azure App Service
```bash
# Create App Service
az webapp create --resource-group myResourceGroup --plan myAppServicePlan --name moktob-app --runtime "JAVA|17"

# Configure app settings
az webapp config appsettings set --resource-group myResourceGroup --name moktob-app --settings \
  SPRING_PROFILES_ACTIVE=prod \
  DATABASE_URL=$DATABASE_URL \
  DB_USERNAME=$DB_USERNAME \
  DB_PASSWORD=$DB_PASSWORD

# Deploy
az webapp deployment source config --resource-group myResourceGroup --name moktob-app --repo-url https://github.com/your-repo/moktob.git --branch main --manual-integration
```

#### Option B: Azure Container Instances
```yaml
# azure-container-instance.yml
apiVersion: 2018-10-01
location: eastus
name: moktob-app
properties:
  containers:
  - name: moktob-app
    properties:
      image: your-registry/moktob-app:latest
      resources:
        requests:
          cpu: 1
          memoryInGb: 2
      ports:
      - port: 8080
      environmentVariables:
      - name: SPRING_PROFILES_ACTIVE
        value: prod
      - name: DATABASE_URL
        secureValue: $DATABASE_URL
  osType: Linux
  ipAddress:
    type: Public
    ports:
    - protocol: tcp
      port: 8080
```

### 4. Heroku

#### Option A: Heroku with PostgreSQL
```bash
# Install Heroku CLI
# Create Heroku app
heroku create moktob-app

# Add PostgreSQL addon
heroku addons:create heroku-postgresql:hobby-dev

# Set environment variables
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set JWT_SECRET=your-jwt-secret
heroku config:set MAIL_USERNAME=your-email@gmail.com
heroku config:set MAIL_PASSWORD=your-app-password

# Deploy
git push heroku main
```

#### Option B: Heroku with Docker
```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/moktob-management-saas.jar app.jar

EXPOSE $PORT

ENTRYPOINT ["sh", "-c", "java -jar app.jar --spring.profiles.active=prod --server.port=$PORT"]
```

### 5. DigitalOcean

#### Option A: DigitalOcean App Platform
```yaml
# .do/app.yaml
name: moktob-app
services:
- name: moktob-backend
  source_dir: /
  github:
    repo: your-username/moktob
    branch: main
  run_command: java -jar target/moktob-management-saas.jar --spring.profiles.active=prod
  environment_slug: java
  instance_count: 1
  instance_size_slug: basic-xxs
  envs:
  - key: SPRING_PROFILES_ACTIVE
    value: prod
  - key: DATABASE_URL
    value: ${db.DATABASE_URL}
  - key: DB_USERNAME
    value: ${db.USERNAME}
  - key: DB_PASSWORD
    value: ${db.PASSWORD}
  http_port: 8080
  routes:
  - path: /
databases:
- name: db
  engine: PG
  version: "13"
```

#### Option B: DigitalOcean Droplet
```bash
# Create droplet
doctl compute droplet create moktob-server --image ubuntu-20-04-x64 --size s-1vcpu-2gb --region nyc1

# SSH into droplet
ssh root@your-droplet-ip

# Install Java and dependencies
apt update
apt install openjdk-17-jdk nginx postgresql-client

# Setup application
mkdir /opt/moktob
# Upload your JAR file
# Configure systemd service
# Setup Nginx reverse proxy
```

---

## 🚀 Deployment Strategies

### 1. Blue-Green Deployment
```bash
#!/bin/bash
# blue-green-deploy.sh

# Deploy to green environment
echo "Deploying to green environment..."
kubectl apply -f k8s/green-deployment.yaml

# Wait for green to be ready
kubectl wait --for=condition=available --timeout=300s deployment/moktob-green

# Switch traffic to green
kubectl apply -f k8s/green-service.yaml

# Verify green is working
curl -f http://green.moktob.com/moktob/actuator/health

# Clean up blue environment
kubectl delete deployment moktob-blue
```

### 2. Rolling Deployment
```yaml
# k8s/rolling-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: moktob-app
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1
      maxSurge: 1
  selector:
    matchLabels:
      app: moktob-app
  template:
    metadata:
      labels:
        app: moktob-app
    spec:
      containers:
      - name: moktob-app
        image: your-registry/moktob-app:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: moktob-secrets
              key: database-url
        livenessProbe:
          httpGet:
            path: /moktob/actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /moktob/actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

### 3. Canary Deployment
```yaml
# k8s/canary-deployment.yaml
apiVersion: argoproj.io/v1alpha1
kind: Rollout
metadata:
  name: moktob-app
spec:
  replicas: 5
  strategy:
    canary:
      steps:
      - setWeight: 20
      - pause: {duration: 10m}
      - setWeight: 40
      - pause: {duration: 10m}
      - setWeight: 60
      - pause: {duration: 10m}
      - setWeight: 80
      - pause: {duration: 10m}
  selector:
    matchLabels:
      app: moktob-app
  template:
    metadata:
      labels:
        app: moktob-app
    spec:
      containers:
      - name: moktob-app
        image: your-registry/moktob-app:latest
        ports:
        - containerPort: 8080
```

---

## 🔒 Security Considerations

### 1. SSL/TLS Configuration
```yaml
# nginx.conf
server {
    listen 80;
    server_name moktob.com www.moktob.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name moktob.com www.moktob.com;
    
    ssl_certificate /etc/ssl/certs/moktob.crt;
    ssl_certificate_key /etc/ssl/private/moktob.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 2. Environment Variables Security
```bash
# Use secrets management
# AWS Secrets Manager
aws secretsmanager create-secret --name moktob/database --secret-string '{"username":"moktob_user","password":"secure_password"}'

# Azure Key Vault
az keyvault secret set --vault-name moktob-vault --name database-password --value "secure_password"

# Google Secret Manager
gcloud secrets create database-password --data-file=- <<< "secure_password"
```

### 3. Network Security
```yaml
# k8s/network-policy.yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: moktob-network-policy
spec:
  podSelector:
    matchLabels:
      app: moktob-app
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          name: ingress-nginx
    ports:
    - protocol: TCP
      port: 8080
  egress:
  - to:
    - namespaceSelector:
        matchLabels:
          name: database
    ports:
    - protocol: TCP
      port: 5432
```

---

## 📊 Monitoring & Maintenance

### 1. Health Checks
```java
// HealthCheckController.java
@RestController
@RequestMapping("/moktob/actuator")
public class HealthCheckController {
    
    @Autowired
    private DataSource dataSource;
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        
        // Database health
        try (Connection conn = dataSource.getConnection()) {
            health.put("database", "UP");
        } catch (SQLException e) {
            health.put("database", "DOWN");
        }
        
        // Application health
        health.put("status", "UP");
        health.put("timestamp", Instant.now());
        
        return ResponseEntity.ok(health);
    }
}
```

### 2. Logging Configuration
```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/moktob/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/var/log/moktob/application.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

### 3. Monitoring Setup
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
- job_name: 'moktob-app'
  static_configs:
  - targets: ['moktob-app:8080']
  metrics_path: '/moktob/actuator/prometheus'
```

### 4. Backup Strategy
```bash
#!/bin/bash
# backup.sh

# Database backup
pg_dump $DATABASE_URL > backup_$(date +%Y%m%d_%H%M%S).sql

# Upload to cloud storage
aws s3 cp backup_$(date +%Y%m%d_%H%M%S).sql s3://moktob-backups/

# Cleanup old backups
find /backups -name "backup_*.sql" -mtime +7 -delete
```

---

## 🔧 Troubleshooting

### Common Issues & Solutions

#### 1. Database Connection Issues
```bash
# Check database connectivity
telnet your-db-host 5432

# Test connection
psql $DATABASE_URL -c "SELECT 1;"

# Check connection pool
curl http://localhost:8080/moktob/actuator/health
```

#### 2. Memory Issues
```bash
# Check JVM memory usage
jstat -gc <pid>

# Increase heap size
java -Xmx2g -Xms1g -jar moktob-management-saas.jar
```

#### 3. Port Conflicts
```bash
# Check port usage
netstat -tulpn | grep :8080

# Kill process using port
sudo kill -9 $(lsof -t -i:8080)
```

#### 4. SSL Certificate Issues
```bash
# Check certificate validity
openssl x509 -in moktob.crt -text -noout

# Test SSL connection
openssl s_client -connect moktob.com:443
```

---

## 📝 Deployment Checklist

### Pre-Deployment
- [ ] Code reviewed and tested
- [ ] Database migrations prepared
- [ ] Environment variables configured
- [ ] SSL certificates ready
- [ ] Monitoring setup complete
- [ ] Backup strategy implemented

### Deployment
- [ ] Deploy to staging environment
- [ ] Run smoke tests
- [ ] Deploy to production
- [ ] Verify health checks
- [ ] Monitor logs for errors
- [ ] Test critical functionality

### Post-Deployment
- [ ] Verify all services are running
- [ ] Check performance metrics
- [ ] Monitor error rates
- [ ] Update documentation
- [ ] Notify stakeholders

---

## 🎯 Quick Start Commands

### AWS Elastic Beanstalk
```bash
eb init moktob-app --platform java --region us-east-1
eb create prod
eb deploy
```

### Google App Engine
```bash
gcloud app deploy
```

### Heroku
```bash
heroku create moktob-app
heroku addons:create heroku-postgresql:hobby-dev
git push heroku main
```

### Docker
```bash
docker build -t moktob-app .
docker run -d -p 8080:8080 --env-file .env moktob-app
```

### Kubernetes
```bash
kubectl apply -f k8s/
kubectl get pods
kubectl get services
```

---

## 📞 Support & Resources

- **Documentation**: [Spring Boot Deployment Guide](https://spring.io/guides/gs/spring-boot-for-azure/)
- **Cloud Providers**: AWS, GCP, Azure documentation
- **Monitoring**: Prometheus, Grafana, ELK Stack
- **Security**: OWASP guidelines, cloud security best practices

---

*This guide provides comprehensive deployment options for your Moktob Management System. Choose the option that best fits your requirements, budget, and technical expertise.*
