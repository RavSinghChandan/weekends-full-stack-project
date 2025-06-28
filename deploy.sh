#!/bin/bash

# Healthcare Management System Deployment Script
# Usage: ./deploy.sh [dev|staging|prod]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Environment variables
ENVIRONMENT=${1:-dev}
COMPOSE_FILE="docker-compose.yml"

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

# Function to check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    # Check if Docker is installed
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    # Check if Docker Compose is installed
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    
    # Check if Docker daemon is running
    if ! docker info &> /dev/null; then
        print_error "Docker daemon is not running. Please start Docker first."
        exit 1
    fi
    
    print_success "Prerequisites check passed"
}

# Function to set environment-specific configurations
set_environment() {
    print_status "Setting up environment: $ENVIRONMENT"
    
    case $ENVIRONMENT in
        "dev")
            export SPRING_PROFILES_ACTIVE=dev
            export COMPOSE_PROJECT_NAME=healthcare-dev
            ;;
        "staging")
            export SPRING_PROFILES_ACTIVE=staging
            export COMPOSE_PROJECT_NAME=healthcare-staging
            COMPOSE_FILE="docker-compose.staging.yml"
            ;;
        "prod")
            export SPRING_PROFILES_ACTIVE=prod
            export COMPOSE_PROJECT_NAME=healthcare-prod
            COMPOSE_FILE="docker-compose.prod.yml"
            ;;
        *)
            print_error "Invalid environment. Use: dev, staging, or prod"
            exit 1
            ;;
    esac
    
    print_success "Environment set to: $ENVIRONMENT"
}

# Function to build images
build_images() {
    print_status "Building Docker images..."
    
    # Build backend
    print_status "Building backend image..."
    docker build -t healthcare-backend:latest ./04-backend
    
    # Build frontend
    print_status "Building frontend image..."
    docker build -t healthcare-frontend:latest ./05-frontend
    
    print_success "All images built successfully"
}

# Function to deploy services
deploy_services() {
    print_status "Deploying services..."
    
    # Stop existing services
    print_status "Stopping existing services..."
    docker-compose -f $COMPOSE_FILE down --remove-orphans
    
    # Start services
    print_status "Starting services..."
    docker-compose -f $COMPOSE_FILE up -d
    
    print_success "Services deployed successfully"
}

# Function to wait for services to be ready
wait_for_services() {
    print_status "Waiting for services to be ready..."
    
    # Wait for MySQL
    print_status "Waiting for MySQL..."
    timeout=60
    while ! docker-compose -f $COMPOSE_FILE exec -T mysql mysqladmin ping -h localhost --silent; do
        if [ $timeout -le 0 ]; then
            print_error "MySQL failed to start within timeout"
            exit 1
        fi
        sleep 1
        timeout=$((timeout - 1))
    done
    
    # Wait for Redis
    print_status "Waiting for Redis..."
    timeout=30
    while ! docker-compose -f $COMPOSE_FILE exec -T redis redis-cli ping; do
        if [ $timeout -le 0 ]; then
            print_error "Redis failed to start within timeout"
            exit 1
        fi
        sleep 1
        timeout=$((timeout - 1))
    done
    
    # Wait for Backend
    print_status "Waiting for Backend..."
    timeout=120
    while ! curl -f http://localhost:8080/api/health &> /dev/null; do
        if [ $timeout -le 0 ]; then
            print_error "Backend failed to start within timeout"
            exit 1
        fi
        sleep 2
        timeout=$((timeout - 2))
    done
    
    # Wait for Frontend
    print_status "Waiting for Frontend..."
    timeout=60
    while ! curl -f http://localhost:80/health &> /dev/null; do
        if [ $timeout -le 0 ]; then
            print_error "Frontend failed to start within timeout"
            exit 1
        fi
        sleep 2
        timeout=$((timeout - 2))
    done
    
    print_success "All services are ready"
}

# Function to run health checks
run_health_checks() {
    print_status "Running health checks..."
    
    # Check backend health
    if curl -f http://localhost:8080/api/health; then
        print_success "Backend health check passed"
    else
        print_error "Backend health check failed"
        exit 1
    fi
    
    # Check frontend health
    if curl -f http://localhost:80/health; then
        print_success "Frontend health check passed"
    else
        print_error "Frontend health check failed"
        exit 1
    fi
    
    print_success "All health checks passed"
}

# Function to show deployment info
show_deployment_info() {
    print_success "Deployment completed successfully!"
    echo
    echo "=== Healthcare Management System ==="
    echo "Environment: $ENVIRONMENT"
    echo "Frontend: http://localhost:80"
    echo "Backend API: http://localhost:8080"
    echo "Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "Database: localhost:3306"
    echo "Redis: localhost:6379"
    echo
    echo "=== Useful Commands ==="
    echo "View logs: docker-compose -f $COMPOSE_FILE logs -f"
    echo "Stop services: docker-compose -f $COMPOSE_FILE down"
    echo "Restart services: docker-compose -f $COMPOSE_FILE restart"
    echo
}

# Main deployment function
main() {
    echo "=== Healthcare Management System Deployment ==="
    echo "Environment: $ENVIRONMENT"
    echo
    
    check_prerequisites
    set_environment
    build_images
    deploy_services
    wait_for_services
    run_health_checks
    show_deployment_info
}

# Run main function
main "$@" 