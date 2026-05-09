const { createClient } = require('redis');

const redisClient = createClient({
    url: process.env.REDIS_URL || 'redis://localhost:6379'
});

redisClient.on('error', (err) => {
    console.error('Redis error:', err);
});

redisClient.on('connect', () => {
    console.log('Redis client connected');
});

// Cache helpers
const cacheHelpers = {
    async set(key, value, expireSeconds = 3600) {
        await redisClient.setEx(key, expireSeconds, JSON.stringify(value));
    },

    async get(key) {
        const value = await redisClient.get(key);
        return value ? JSON.parse(value) : null;
    },

    async del(key) {
        await redisClient.del(key);
    },

    async setHash(key, field, value) {
        await redisClient.hSet(key, field, JSON.stringify(value));
    },

    async getHash(key, field) {
        const value = await redisClient.hGet(key, field);
        return value ? JSON.parse(value) : null;
    },

    async delHash(key, field) {
        await redisClient.hDel(key, field);
    },

    async incr(key) {
        return await redisClient.incr(key);
    },

    async expire(key, seconds) {
        await redisClient.expire(key, seconds);
    }
};

module.exports = { redisClient, cacheHelpers };
