#!/bin/bash

echo "Stopping Kafka microservices demo..."

# Stop and remove containers
docker-compose down

# Optional: Remove volumes (uncomment if you want to clear data)
# docker-compose down -v

echo "Services stopped successfully!"