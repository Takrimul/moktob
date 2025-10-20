#!/bin/bash

# 🚀 Moktob Management System - Quick Deployment Script
# This script helps you deploy your application to various cloud platforms

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

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    if ! command_exists java; then
        print_error "Java is not installed. Please install Java 17 or higher."
        exit 1
    fi
    
    if ! command_exists mvn; then
        print_error "Maven is not installed. Please install Maven."
        exit 1
    fi
    
    print_success "Prerequisites check passed!"
}

# Function to build the application
build_application() {
    print_status "Building the application..."
    
    # Clean and compile
    mvn clean compile
    
    # Run tests
    print_status "Running tests..."
    mvn test
    
    # Package the application
    print_status "Packaging the application..."
    mvn package -DskipTests
    
    print_success "Application built successfully!"
}

# Function to create Docker image
create_docker_image() {
    print_status "Creating Docker image..."
    
    # Create Dockerfile if it doesn't exist
    if [ ! -f Dockerfile ]; then
        cat > Dockerfile << 'EOF'
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the JAR file
COPY target/moktob-management-saas.jar app.jar

# Expose port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/moktob/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
        print_success "Dockerfile created!"
    fi
    
    # Build Docker image
    docker build -t moktob-app:latest .
    
    print_success "Docker image created successfully!"
}

# Function to deploy to Heroku
deploy_heroku() {
    print_status "Deploying to Heroku..."
    
    if ! command_exists heroku; then
        print_error "Heroku CLI is not installed. Please install it first."
        print_status "Visit: https://devcenter.heroku.com/articles/heroku-cli"
        exit 1
    fi
    
    # Login to Heroku
    heroku login
    
    # Create Heroku app
    read -p "Enter your Heroku app name (or press Enter for auto-generated): " app_name
    if [ -z "$app_name" ]; then
        heroku create
    else
        heroku create "$app_name"
    fi
    
    # Add PostgreSQL addon
    heroku addons:create heroku-postgresql:hobby-dev
    
    # Set environment variables
    print_status "Setting environment variables..."
    heroku config:set SPRING_PROFILES_ACTIVE=prod
    heroku config:set JWT_SECRET=$(openssl rand -base64 32)
    
    # Get database URL
    db_url=$(heroku config:get DATABASE_URL)
    heroku config:set DATABASE_URL="$db_url"
    
    # Deploy
    git add .
    git commit -m "Deploy to Heroku"
    git push heroku main
    
    print_success "Deployed to Heroku successfully!"
    print_status "Your app is available at: https://$(heroku apps:info --json | jq -r '.app.name').herokuapp.com"
}

# Function to deploy to AWS Elastic Beanstalk
deploy_aws_eb() {
    print_status "Deploying to AWS Elastic Beanstalk..."
    
    if ! command_exists eb; then
        print_error "EB CLI is not installed. Please install it first."
        print_status "Run: pip install awsebcli"
        exit 1
    fi
    
    # Initialize EB
    eb init moktob-app --platform java --region us-east-1
    
    # Create environment
    eb create prod --envvars SPRING_PROFILES_ACTIVE=prod
    
    # Deploy
    eb deploy
    
    print_success "Deployed to AWS Elastic Beanstalk successfully!"
    print_status "Your app is available at: $(eb status --verbose | grep 'CNAME')"
}

# Function to deploy to Google Cloud Run
deploy_gcp() {
    print_status "Deploying to Google Cloud Run..."
    
    if ! command_exists gcloud; then
        print_error "Google Cloud CLI is not installed. Please install it first."
        print_status "Visit: https://cloud.google.com/sdk/docs/install"
        exit 1
    fi
    
    # Get project ID
    project_id=$(gcloud config get-value project)
    if [ -z "$project_id" ]; then
        read -p "Enter your Google Cloud project ID: " project_id
        gcloud config set project "$project_id"
    fi
    
    # Build and push image
    gcloud builds submit --tag gcr.io/"$project_id"/moktob-app
    
    # Deploy to Cloud Run
    gcloud run deploy moktob-app \
        --image gcr.io/"$project_id"/moktob-app \
        --platform managed \
        --region us-central1 \
        --allow-unauthenticated \
        --set-env-vars SPRING_PROFILES_ACTIVE=prod
    
    print_success "Deployed to Google Cloud Run successfully!"
    print_status "Your app is available at: https://moktob-app-[hash]-uc.a.run.app"
}

# Function to deploy to DigitalOcean App Platform
deploy_digitalocean() {
    print_status "Deploying to DigitalOcean App Platform..."
    
    if ! command_exists doctl; then
        print_error "DigitalOcean CLI is not installed. Please install it first."
        print_status "Visit: https://docs.digitalocean.com/reference/doctl/how-to/install/"
        exit 1
    fi
    
    # Create app spec
    cat > .do/app.yaml << 'EOF'
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
  - key: JWT_SECRET
    value: your-jwt-secret-here
  http_port: 8080
databases:
- name: db
  engine: PG
  version: "13"
EOF
    
    # Deploy
    doctl apps create --spec .do/app.yaml
    
    print_success "Deployed to DigitalOcean App Platform successfully!"
}

# Function to deploy locally with Docker
deploy_local_docker() {
    print_status "Deploying locally with Docker..."
    
    if ! command_exists docker; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    # Create environment file
    cat > .env << 'EOF'
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://localhost:5432/moktob_prod
DB_USERNAME=moktob_user
DB_PASSWORD=secure_password
JWT_SECRET=your-super-secure-jwt-secret-key-min-32-chars
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
EOF
    
    # Run with Docker Compose
    if [ -f docker-compose.yml ]; then
        docker-compose up -d
    else
        # Create docker-compose.yml
        cat > docker-compose.yml << 'EOF'
version: '3.8'
services:
  moktob-app:
    image: moktob-app:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DATABASE_URL=jdbc:postgresql://db:5432/moktob_prod
      - DB_USERNAME=moktob_user
      - DB_PASSWORD=secure_password
      - JWT_SECRET=your-super-secure-jwt-secret-key-min-32-chars
    depends_on:
      - db
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/moktob/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  db:
    image: postgres:13
    environment:
      - POSTGRES_DB=moktob_prod
      - POSTGRES_USER=moktob_user
      - POSTGRES_PASSWORD=secure_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
EOF
        docker-compose up -d
    fi
    
    print_success "Deployed locally with Docker successfully!"
    print_status "Your app is available at: http://localhost:8080"
}

# Function to show deployment options
show_deployment_options() {
    echo -e "${BLUE}🚀 Moktob Management System - Deployment Options${NC}"
    echo ""
    echo "1. Heroku (Easiest - Free tier available)"
    echo "2. AWS Elastic Beanstalk (Scalable - Pay as you use)"
    echo "3. Google Cloud Run (Serverless - Pay per request)"
    echo "4. DigitalOcean App Platform (Simple - Fixed pricing)"
    echo "5. Local Docker (Development - Free)"
    echo "6. Build only (No deployment)"
    echo ""
}

# Main function
main() {
    echo -e "${GREEN}🚀 Moktob Management System - Quick Deployment Script${NC}"
    echo ""
    
    # Check prerequisites
    check_prerequisites
    
    # Build application
    build_application
    
    # Show deployment options
    show_deployment_options
    
    # Get user choice
    read -p "Choose deployment option (1-6): " choice
    
    case $choice in
        1)
            deploy_heroku
            ;;
        2)
            deploy_aws_eb
            ;;
        3)
            deploy_gcp
            ;;
        4)
            deploy_digitalocean
            ;;
        5)
            create_docker_image
            deploy_local_docker
            ;;
        6)
            print_success "Application built successfully!"
            print_status "JAR file location: target/moktob-management-saas.jar"
            ;;
        *)
            print_error "Invalid option. Please choose 1-6."
            exit 1
            ;;
    esac
    
    echo ""
    print_success "Deployment process completed!"
    print_status "Check the deployment guide (DEPLOYMENT_GUIDE.md) for more details."
}

# Run main function
main "$@"
