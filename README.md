# FinAlert Pro

Real-time financial market alert platform delivering instant trading notifications and actionable insights.

## Overview

FinAlert Pro is a sophisticated financial monitoring system that provides:
- Real-time market alerts using WebSockets with STOMP protocol
- Spring Boot backend with intelligent alert generation
- Interactive dashboard for alert visualization and monitoring
- Financial market simulations with realistic price movements and trading signals

## Technology Stack

- **Backend**: Java 21, Spring Boot
- **Real-time Communication**: WebSocket with STOMP protocol and SockJS
- **Build Tool**: Gradle
- **Frontend**: HTML, CSS, JavaScript

## How to Run FinAlert Pro

1. **Using Gradle wrapper (recommended)**:
   ```bash
   ./gradlew bootRun
   ```

2. **On Windows with Gradle wrapper**:
   ```bash
   gradlew.bat bootRun
   ```

3. **Using IntelliJ IDEA**:
   - Open the Gradle tool window (View -> Tool Windows -> Gradle)
   - Navigate to Tasks -> application -> bootRun
   - Double-click on bootRun

4. **Access FinAlert Pro**:
   - Open your browser and navigate to http://localhost:8080
   - The FinAlert Pro dashboard will display trading alerts in real-time

## FinAlert Pro WebSocket Architecture

FinAlert Pro uses a robust WebSocket-based architecture for reliable real-time communication:

1. **Server-Side Setup**:
   - STOMP (Simple Text Oriented Messaging Protocol) over WebSocket
   - Message Broker endpoint (`/topic`)
   - WebSocket connection endpoint (`/websocket`)

2. **Data Flow**:
   - `AlertSimulatorService` generates trading alerts every 2 seconds
   - Alerts are sent through the message broker to `/topic/alerts`
   - Browser connects to WebSocket endpoint and subscribes to the topic
   - Alerts appear in real-time on the dashboard

3. **Technical Implementation**:
   - `WebSocketConfig` configures the message broker and endpoints
   - `SimpMessagingTemplate` handles server-to-client messages
   - SockJS provides fallback options for cross-browser compatibility
   - Full-duplex communication for instant updates

## Features

   - **Real-time Trade Alerts**: FinAlert Pro delivers instant notifications for various market events
   - **Financial Instruments**: Simulates multiple assets across different sectors with detailed metrics
   - **Dynamic Price Movement**: Realistic price fluctuations with volatility indicators
   - **Alert Categories**: Various alert types (price swings, order executions, etc.) with color-coded urgency
   - **Action Recommendations**: AI-informed trading action suggestions based on market conditions
