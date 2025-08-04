#!/bin/bash

# Wait for Kafka to be ready
echo "Waiting for Kafka to be ready..."
sleep 30

# Create topics
echo "Creating Kafka topics..."

# Order events topic
docker exec kafka kafka-topics --create \
  --topic order-events \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3

# Inventory events topic
docker exec kafka kafka-topics --create \
  --topic inventory-events \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3

# Payment events topic
docker exec kafka kafka-topics --create \
  --topic payment-events \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3

echo "Topics created successfully!"

# List all topics
echo "Available topics:"
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092