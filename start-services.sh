#!/bin/bash

echo "Starting Kafka microservices demo..."

# Start Docker Compose
docker-compose up -d

# Wait for services to be ready
echo "Waiting for services to start..."
sleep 45

# Initialize Kafka topics
chmod +x kafka-topics-init.sh
./kafka-topics-init.sh

echo "Services started successfully!"
echo ""
echo "Available services:"
echo "- Kafka UI: http://localhost:8080"
echo "- MailHog Web UI: http://localhost:8025"
echo "- Order Service: http://localhost:8081"
echo "- Notification Service: http://localhost:8082"
echo ""
echo "To test the services, run the test script:"
echo "./test-services.sh"