const jwt = require('jsonwebtoken');
const { RefreshToken } = require('../config/database');

const JWT_SECRET = process.env.JWT_SECRET || 'your-secret-key';
const JWT_REFRESH_SECRET = process.env.JWT_REFRESH_SECRET || 'your-refresh-secret';

const generateTokens = async (user) => {
    const accessToken = jwt.sign(
        { userId: user.id, email: user.email },
        JWT_SECRET,
        { expiresIn: '24h' }
    );

    const refreshToken = jwt.sign(
        { userId: user.id, type: 'refresh' },
        JWT_REFRESH_SECRET,
        { expiresIn: '7d' }
    );

    // Store refresh token
    await RefreshToken.create({
        token: refreshToken,
        userId: user.id,
        expiresAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000), // 7 days
        deviceInfo: {
            userAgent: typeof navigator !== 'undefined' ? navigator.userAgent : 'unknown'
        }
    });

    return {
        accessToken,
        refreshToken,
        expiresIn: 86400 // 24 hours in seconds
    };
};

const verifyAccessToken = (token) => {
    return jwt.verify(token, JWT_SECRET);
};

const verifyRefreshToken = (token) => {
    return jwt.verify(token, JWT_REFRESH_SECRET);
};

module.exports = { generateTokens, verifyAccessToken, verifyRefreshToken };
