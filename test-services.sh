#!/bin/bash

echo "Testing Kafka Microservices Demo..."
echo "=================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Base URLs
ORDER_SERVICE="http://localhost:8081"
NOTIFICATION_SERVICE="http://localhost:8082"
INVENTORY_SERVICE="http://localhost:8083"

# Wait for services to be ready
echo -e "${YELLOW}Waiting for services to be ready...${NC}"
sleep 10

# Test 1: Check inventory
echo -e "\n${YELLOW}1. Checking initial inventory...${NC}"
curl -s "$INVENTORY_SERVICE/api/inventory" | jq '.'

# Test 2: Create an order
echo -e "\n${YELLOW}2. Creating a new order...${NC}"
ORDER_RESPONSE=$(curl -s -X POST "$ORDER_SERVICE/api/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "productName": "iPhone 15",
    "quantity": 2,
    "price": 1999.99
  }')

echo "$ORDER_RESPONSE" | jq '.'
ORDER_ID=$(echo "$ORDER_RESPONSE" | jq -r '.id')

# Wait for event processing
echo -e "\n${YELLOW}Waiting for events to be processed...${NC}"
sleep 5

# Test 3: Check inventory after order
echo -e "\n${YELLOW}3. Checking inventory after order creation...${NC}"
curl -s "$INVENTORY_SERVICE/api/inventory/iPhone%2015" | jq '.'

# Test 4: Check notifications
echo -e "\n${YELLOW}4. Checking notifications...${NC}"
curl -s "$NOTIFICATION_SERVICE/api/notifications" | jq '.'

# Test 5: Update order status to CONFIRMED
echo -e "\n${YELLOW}5. Confirming the order...${NC}"
curl -s -X PUT "$ORDER_SERVICE/api/orders/$ORDER_ID/status?status=CONFIRMED" | jq '.'

# Wait for event processing
sleep 3

# Test 6: Check final inventory state
echo -e "\n${YELLOW}6. Checking final inventory state...${NC}"
curl -s "$INVENTORY_SERVICE/api/inventory/iPhone%2015" | jq '.'

# Test 7: Check all orders
echo -e "\n${YELLOW}7. Checking all orders...${NC}"
curl -s "$ORDER_SERVICE/api/orders" | jq '.'

# Test 8: Test insufficient inventory
echo -e "\n${YELLOW}8. Testing insufficient inventory scenario...${NC}"
curl -s -X POST "$ORDER_SERVICE/api/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Jane Smith",
    "productName": "iPhone 15",
    "quantity": 200,
    "price": 1999.99
  }' | jq '.'

# Wait for event processing
sleep 3

echo -e "\n${GREEN}Test completed!${NC}"
echo -e "\n${YELLOW}You can also check:${NC}"
echo "- Kafka UI: http://localhost:8080"
echo "- MailHog: http://localhost:8025"
echo "- H2 Console Order Service: http://localhost:8081/h2-console"
echo "- H2 Console Notification Service: http://localhost:8082/h2-console"
echo "- H2 Console Inventory Service: http://localhost:8083/h2-console"