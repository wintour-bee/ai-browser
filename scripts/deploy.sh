#!/bin/bash

# AI Browser - Deployment Script
# For Ubuntu/Debian servers

set -e

echo "======================================"
echo "AI Browser - Deployment Script"
echo "======================================"

# Variables
APP_DIR="/opt/ai-browser"
REPO_URL="https://github.com/your-repo/ai-browser.git"
BACKUP_DIR="/opt/ai-browser-backups"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if running as root
if [ "$EUID" -ne 0 ]; then 
    warn "Please run as root or with sudo"
    exit 1
fi

# Update system
log "Updating system packages..."
apt update && apt upgrade -y

# Install Docker
log "Installing Docker..."
if ! command -v docker &> /dev/null; then
    curl -fsSL https://get.docker.com | sh
    systemctl enable docker
    systemctl start docker
fi

# Install Docker Compose
log "Installing Docker Compose..."
if ! command -v docker-compose &> /dev/null; then
    curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
fi

# Create app directory
log "Creating application directory..."
mkdir -p $APP_DIR
mkdir -p $BACKUP_DIR

# Pull latest code or copy files
if [ -d "$APP_DIR/.git" ]; then
    log "Pulling latest code..."
    cd $APP_DIR
    git pull
else
    log "Copying application files..."
    cp -r . $APP_DIR/
    cd $APP_DIR
fi

# Create SSL directory
mkdir -p nginx/ssl

# Create .env file
if [ ! -f "$APP_DIR/.env" ]; then
    log "Creating .env file..."
    cp .env.example $APP_DIR/.env
    warn "Please edit $APP_DIR/.env with your configuration"
fi

# Set permissions
log "Setting permissions..."
chmod +x $APP_DIR/scripts/deploy.sh

# Build and start containers
log "Building Docker containers..."
cd $APP_DIR
docker-compose build

log "Starting services..."
docker-compose up -d

# Wait for services
log "Waiting for services to be ready..."
sleep 10

# Check status
log "Checking service status..."
docker-compose ps

# Show logs
log "Recent logs:"
docker-compose logs --tail=50

# Firewall setup (optional)
read -p "Configure firewall (ufw)? [y/N] " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    apt install -y ufw
    ufw allow 22/tcp
    ufw allow 80/tcp
    ufw allow 443/tcp
    ufw --force enable
fi

echo ""
log "======================================"
log "Deployment complete!"
log "======================================"
log "API: http://your-server:3000"
log "Health: http://your-server:3000/health"
echo ""
