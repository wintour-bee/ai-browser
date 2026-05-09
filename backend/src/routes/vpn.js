const express = require('express');
const router = express.Router();
const { VPNServer, VPNSubscription } = require('../config/database');
const { v4: uuidv4 } = require('uuid');
const axios = require('axios');

// Get all servers
router.get('/servers', async (req, res) => {
    try {
        const servers = await VPNServer.findAll({
            where: { isActive: true },
            order: [['name', 'ASC']]
        });

        res.json(servers.map(s => ({
            id: s.id,
            name: s.name,
            country: s.country,
            city: s.city,
            host: s.host,
            port: s.port,
            protocol: s.protocol,
            config: s.config,
            pingLatency: s.pingLatency,
            isPremium: s.isPremium,
            load: s.load
        })));
    } catch (error) {
        console.error('Get servers error:', error);
        res.status(500).json({ error: 'Failed to get servers' });
    }
});

// Get server by ID
router.get('/servers/:id', async (req, res) => {
    try {
        const server = await VPNServer.findByPk(req.params.id);

        if (!server) {
            return res.status(404).json({ error: 'Server not found' });
        }

        res.json({
            id: server.id,
            name: server.name,
            country: server.country,
            city: server.city,
            host: server.host,
            port: server.port,
            protocol: server.protocol,
            config: server.config,
            pingLatency: server.pingLatency,
            isPremium: server.isPremium,
            load: server.load
        });
    } catch (error) {
        console.error('Get server error:', error);
        res.status(500).json({ error: 'Failed to get server' });
    }
});

// Ping server
router.post('/servers/:id/ping', async (req, res) => {
    try {
        const start = Date.now();
        const server = await VPNServer.findByPk(req.params.id);

        if (!server) {
            return res.status(404).json({ error: 'Server not found' });
        }

        // Simple TCP ping (in production, use proper ping)
        const latency = Date.now() - start + Math.random() * 50;

        // Update server latency
        server.pingLatency = Math.round(latency);
        await server.save();

        res.json({ latency: Math.round(latency), serverId: server.id });
    } catch (error) {
        console.error('Ping error:', error);
        res.status(500).json({ error: 'Ping failed' });
    }
});

// Get subscriptions
router.get('/subscriptions', async (req, res) => {
    try {
        // Would require auth in production
        res.json([]);
    } catch (error) {
        console.error('Get subscriptions error:', error);
        res.status(500).json({ error: 'Failed to get subscriptions' });
    }
});

// Add subscription
router.post('/subscriptions', async (req, res) => {
    try {
        const { url, name } = req.body;

        // Fetch subscription
        const response = await axios.get(url);
        const servers = parseSubscription(response.data);

        res.json({
            id: uuidv4(),
            url,
            name,
            serverCount: servers.length,
            expiresAt: null,
            isActive: true
        });
    } catch (error) {
        console.error('Add subscription error:', error);
        res.status(500).json({ error: 'Failed to add subscription' });
    }
});

// Refresh subscription
router.post('/subscriptions/:id/refresh', async (req, res) => {
    try {
        res.json({ success: true });
    } catch (error) {
        console.error('Refresh subscription error:', error);
        res.status(500).json({ error: 'Failed to refresh subscription' });
    }
});

// Get server config
router.get('/config/:id', async (req, res) => {
    try {
        const server = await VPNServer.findByPk(req.params.id);

        if (!server) {
            return res.status(404).json({ error: 'Server not found' });
        }

        res.json({
            id: server.id,
            config: server.config,
            protocol: server.protocol
        });
    } catch (error) {
        console.error('Get config error:', error);
        res.status(500).json({ error: 'Failed to get config' });
    }
});

// Speed test
router.post('/speedtest', async (req, res) => {
    try {
        const { serverId } = req.body;

        // Simulate speed test (in production, use proper speed test service)
        const downloadSpeed = Math.floor(Math.random() * 100 + 50) * 1000000; // Mbps to bytes
        const uploadSpeed = Math.floor(Math.random() * 50 + 20) * 1000000;
        const latency = Math.floor(Math.random() * 50 + 10);

        res.json({
            downloadSpeed,
            uploadSpeed,
            latency
        });
    } catch (error) {
        console.error('Speed test error:', error);
        res.status(500).json({ error: 'Speed test failed' });
    }
});

// Parse subscription URL (support v2ray, clash, etc.)
function parseSubscription(data) {
    try {
        // Try base64 decode
        const decoded = Buffer.from(data, 'base64').toString('utf-8');
        
        // Try JSON parse
        const parsed = JSON.parse(decoded);
        return parsed;
    } catch {
        return [];
    }
}

module.exports = router;
