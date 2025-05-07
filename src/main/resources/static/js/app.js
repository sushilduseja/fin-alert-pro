// WebSocket connection variables
let stompClient = null;

function connect() {
    const socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    
    console.log('Attempting to connect to WebSocket...');
    
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        
        // Update UI to show connected status
        const statusElement = document.getElementById('connection-status');
        if (statusElement) {
            statusElement.textContent = 'Connected';
            statusElement.className = 'connected';
        }
        
        // Subscribe to trade alerts topic
        stompClient.subscribe('/topic/alerts', function (alertMessage) {
            console.log('Received alert:', alertMessage);
            showAlert(JSON.parse(alertMessage.body));
        });
    }, function(error) {
        console.log('WebSocket connection error: ' + error);
        
        // Update UI to show disconnected status
        const statusElement = document.getElementById('connection-status');
        if (statusElement) {
            statusElement.textContent = 'Disconnected';
            statusElement.className = 'disconnected';
        }
        
        // Try to reconnect after a delay
        setTimeout(connect, 5000);
    });
}

function showAlert(alert) {
    console.log('Processing alert:', alert);
    const alertsList = document.getElementById('alerts-list');
    
    if (!alertsList) {
        console.error('alerts-list element not found!');
        return;
    }
    
    // Create alert element
    const alertElement = document.createElement('div');
    alertElement.className = 'alert-item';
    
    if (alert.urgency) {
        alertElement.className += ' urgency-' + alert.urgency;
    }
    
    if (alert.type) {
        alertElement.setAttribute('data-type', alert.type);
        alertElement.className += ' ' + alert.type.toLowerCase().replace('_', '-');
    }
    
    // Format timestamp if present
    let formattedTime = alert.timestamp;
    if (alert.timestamp && !isNaN(new Date(alert.timestamp).getTime())) {
        const timestamp = new Date(alert.timestamp);
        formattedTime = timestamp.toLocaleTimeString();
    }
    
    // Determine price field name (could be currentPrice or price)
    const price = alert.currentPrice !== undefined ? alert.currentPrice : alert.price;
    
    // Construct alert content with a unified format
    // Calculate price change percentage if data available
    let priceChangeDisplay = '';
    let priceChangeClass = '';
    if (alert.currentPrice !== undefined && alert.previousPrice !== undefined) {
        const priceChange = ((alert.currentPrice - alert.previousPrice) / alert.previousPrice * 100).toFixed(2);
        const isPositive = alert.currentPrice >= alert.previousPrice;
        priceChangeClass = isPositive ? 'positive' : 'negative';
        const changeIcon = isPositive ? '▲' : '▼';
        priceChangeDisplay = `${changeIcon} ${priceChange}%`;
    }
    
    alertElement.innerHTML = `
        <div class="alert-header">
            <span class="alert-symbol">${alert.symbol}</span>
            <span class="alert-price">$${price ? price.toFixed(2) : '0.00'}</span>
            ${priceChangeDisplay ? `<span class="alert-change ${priceChangeClass}">${priceChangeDisplay}</span>` : ''}
        </div>
        <div class="alert-message">${alert.message}</div>
        <div class="alert-details">
            ${alert.recommendedAction ? `<span class="alert-action">${alert.recommendedAction}</span>` : 
              alert.type ? `<span class="alert-action">${formatAlertType(alert.type)}</span>` : ''}
            <span class="alert-time">${formattedTime}</span>
        </div>
    `;
    
    // Add to the beginning of the list
    alertsList.insertBefore(alertElement, alertsList.firstChild);
    
    // Limit the number of alerts to avoid browser performance issues
    if (alertsList.children.length > 50) {
        alertsList.removeChild(alertsList.lastChild);
    }
    
    // Update alert counter if it exists
    const counter = document.getElementById('alert-counter');
    if (counter) {
        counter.textContent = alertsList.children.length;
    }
}

// Format alert type for display
function formatAlertType(type) {
    if (!type) return '';
    return type.replace('_', ' ').toLowerCase()
        .split(' ')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ');
}

// Update current time in header if element exists
function updateCurrentTime() {
    const timeElement = document.getElementById('current-time');
    if (timeElement) {
        const now = new Date();
        timeElement.textContent = now.toLocaleTimeString();
        setTimeout(updateCurrentTime, 1000);
    }
}

// Initialize the application when the page loads
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded, initializing application...');
    connect();
    updateCurrentTime();
});
