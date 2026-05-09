require('dotenv').config();
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const compression = require('compression');
const cookieParser = require('cookie-parser');
const http = require('http');
const { Server } = require('socket.io');

// Import routes
const authRoutes = require('./routes/auth');
const userRoutes = require('./routes/user');
const vipRoutes = require('./routes/vip');
const vpnRoutes = require('./routes/vpn');
const aiRoutes = require('./routes/ai');
const syncRoutes = require('./routes/sync');
const inviteRoutes = require('./routes/invite');
const checkinRoutes = require('./routes/checkin');
const settingsRoutes = require('./routes/settings');

// Import middleware
const { errorHandler } = require('./middleware/errorHandler');
const { rateLimiter } = require('./middleware/rateLimiter');
const { authMiddleware } = require('./middleware/auth');

// Import database
const { sequelize } = require('./config/database');
const { redisClient } = require('./config/redis');

const app = express();
const server = http.createServer(app);
const io = new Server(server, {
    cors: {
        origin: process.env.FRONTEND_URL || '*',
        methods: ['GET', 'POST', 'PUT', 'DELETE'],
        credentials: true
    }
});

// Middleware
app.use(helmet({
    contentSecurityPolicy: false
}));
app.use(cors({
    origin: process.env.FRONTEND_URL || '*',
    credentials: true
}));
app.use(compression());
app.use(morgan('combined'));
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));
app.use(cookieParser(process.env.COOKIE_SECRET));

// Rate limiting
app.use(rateLimiter);

// Health check
app.get('/health', (req, res) => {
    res.json({ 
        status: 'healthy', 
        timestamp: new Date().toISOString(),
        uptime: process.uptime()
    });
});

// API Routes
app.use('/api/auth', authRoutes);
app.use('/api/user', authMiddleware, userRoutes);
app.use('/api/vip', authMiddleware, vipRoutes);
app.use('/api/vpn', vpnRoutes); // VPN routes may not require auth
app.use('/api/ai', aiRoutes);
app.use('/api/sync', authMiddleware, syncRoutes);
app.use('/api/invite', authMiddleware, inviteRoutes);
app.use('/api/checkin', authMiddleware, checkinRoutes);
app.use('/api/settings', authMiddleware, settingsRoutes);

// Socket.IO
io.on('connection', (socket) => {
    console.log('Client connected:', socket.id);

    socket.on('ai:chat', async (data) => {
        // Handle AI chat WebSocket
        try {
            const { provider, model, messages } = data;
            // Process AI request
            socket.emit('ai:response', { /* response */ });
        } catch (error) {
            socket.emit('ai:error', { message: error.message });
        }
    });

    socket.on('vpn:status', (data) => {
        // Handle VPN status updates
    });

    socket.on('disconnect', () => {
        console.log('Client disconnected:', socket.id);
    });
});

// Error handling
app.use(errorHandler);

// 404 handler
app.use((req, res) => {
    res.status(404).json({ error: 'Not found' });
});

// Database sync and server start
const PORT = process.env.PORT || 3000;

async function startServer() {
    try {
        // Connect to PostgreSQL
        await sequelize.authenticate();
        console.log('PostgreSQL connected');

        // Sync database (in production, use migrations)
        if (process.env.NODE_ENV !== 'production') {
            await sequelize.sync({ alter: true });
            console.log('Database synced');
        }

        // Connect to Redis
        await redisClient.connect();
        console.log('Redis connected');

        // Start server
        server.listen(PORT, () => {
            console.log(`Server running on port ${PORT}`);
            console.log(`Environment: ${process.env.NODE_ENV || 'development'}`);
        });
    } catch (error) {
        console.error('Failed to start server:', error);
        process.exit(1);
    }
}

// Graceful shutdown
process.on('SIGTERM', async () => {
    console.log('SIGTERM received, shutting down...');
    await redisClient.quit();
    await sequelize.close();
    server.close(() => {
        console.log('Server shut down');
        process.exit(0);
    });
});

startServer();

module.exports = { app, server, io };
