const express = require('express');
const router = express.Router();
const { body, validationResult } = require('express-validator');
const jwt = require('jsonwebtoken');
const { OAuth2Client } = require('google-auth-library');
const { User, RefreshToken, VipStatus } = require('../config/database');
const { cacheHelpers } = require('../config/redis');
const { generateTokens } = require('../utils/jwt');

// Google OAuth client
const googleClient = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);

// Validation middleware
const validate = (req, res, next) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
    }
    next();
};

// Register
router.post('/register', [
    body('email').isEmail().normalizeEmail(),
    body('password').isLength({ min: 6 }),
    body('username').isLength({ min: 2, max: 50 }).trim(),
    body('inviteCode').optional().isLength({ min: 8, max: 8 })
], validate, async (req, res) => {
    try {
        const { email, password, username, inviteCode } = req.body;

        // Check if user exists
        const existingUser = await User.findOne({ where: { email } });
        if (existingUser) {
            return res.status(400).json({ error: 'Email already registered' });
        }

        // Check if username is taken
        const existingUsername = await User.findOne({ where: { username } });
        if (existingUsername) {
            return res.status(400).json({ error: 'Username already taken' });
        }

        // Create user
        const user = await User.create({
            email,
            passwordHash: password,
            username,
            usedInviteCode: inviteCode || null
        });

        // Create VIP status
        await VipStatus.create({ userId: user.id });

        // Process invite code
        if (inviteCode) {
            const inviter = await User.findOne({ where: { inviteCode } });
            if (inviter) {
                await inviter.increment('points', { by: 100 });
                await Invite.create({
                    inviterId: inviter.id,
                    inviteeId: user.id,
                    code: inviteCode,
                    isUsed: true
                });
            }
        }

        // Generate tokens
        const tokens = await generateTokens(user);

        res.status(201).json({
            user: user.toJSON(),
            token: tokens
        });
    } catch (error) {
        console.error('Register error:', error);
        res.status(500).json({ error: 'Registration failed' });
    }
});

// Login
router.post('/login', [
    body('email').isEmail().normalizeEmail(),
    body('password').exists()
], validate, async (req, res) => {
    try {
        const { email, password } = req.body;

        const user = await User.findOne({ where: { email } });
        if (!user || !user.passwordHash) {
            return res.status(401).json({ error: 'Invalid credentials' });
        }

        const isValid = await user.comparePassword(password);
        if (!isValid) {
            return res.status(401).json({ error: 'Invalid credentials' });
        }

        // Update last login
        user.lastLoginAt = new Date();
        await user.save();

        // Generate tokens
        const tokens = await generateTokens(user);

        res.json({
            user: user.toJSON(),
            token: tokens
        });
    } catch (error) {
        console.error('Login error:', error);
        res.status(500).json({ error: 'Login failed' });
    }
});

// Google Auth
router.post('/google', async (req, res) => {
    try {
        const { idToken } = req.body;

        // Verify Google token
        const ticket = await googleClient.verifyIdToken({
            idToken,
            audience: process.env.GOOGLE_CLIENT_ID
        });

        const payload = ticket.getPayload();
        const { email, name, sub: googleId } = payload;

        // Find or create user
        let user = await User.findOne({ where: { email } });
        
        if (!user) {
            user = await User.create({
                email,
                username: name || email.split('@')[0],
                googleId
            });
            await VipStatus.create({ userId: user.id });
        } else if (!user.googleId) {
            user.googleId = googleId;
            await user.save();
        }

        // Generate tokens
        const tokens = await generateTokens(user);

        res.json({
            user: user.toJSON(),
            token: tokens
        });
    } catch (error) {
        console.error('Google auth error:', error);
        res.status(500).json({ error: 'Google authentication failed' });
    }
});

// Refresh token
router.post('/refresh', async (req, res) => {
    try {
        const { refreshToken } = req.body;

        if (!refreshToken) {
            return res.status(400).json({ error: 'Refresh token required' });
        }

        // Verify refresh token
        const decoded = jwt.verify(refreshToken, process.env.JWT_REFRESH_SECRET);
        
        // Find stored token
        const storedToken = await RefreshToken.findOne({
            where: { token: refreshToken, isRevoked: false },
            include: [{ model: User }]
        });

        if (!storedToken || storedToken.expiresAt < new Date()) {
            return res.status(401).json({ error: 'Invalid refresh token' });
        }

        const user = storedToken.User;

        // Revoke old token
        storedToken.isRevoked = true;
        await storedToken.save();

        // Generate new tokens
        const tokens = await generateTokens(user);

        res.json(tokens);
    } catch (error) {
        console.error('Refresh error:', error);
        res.status(401).json({ error: 'Invalid refresh token' });
    }
});

// Logout
router.post('/logout', async (req, res) => {
    try {
        const authHeader = req.headers.authorization;
        if (authHeader) {
            const token = authHeader.split(' ')[1];
            // Optionally revoke token
            await RefreshToken.update(
                { isRevoked: true },
                { where: { token } }
            );
        }
        res.json({ message: 'Logged out successfully' });
    } catch (error) {
        console.error('Logout error:', error);
        res.status(500).json({ error: 'Logout failed' });
    }
});

// Get current user
router.get('/me', require('../middleware/auth'), async (req, res) => {
    try {
        const user = await User.findByPk(req.user.id, {
            include: [{ model: VipStatus, as: 'vipStatus' }]
        });
        
        if (!user) {
            return res.status(404).json({ error: 'User not found' });
        }

        res.json(user.toJSON());
    } catch (error) {
        console.error('Get user error:', error);
        res.status(500).json({ error: 'Failed to get user' });
    }
});

module.exports = router;
