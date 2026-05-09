const express = require('express');
const router = express.Router();

// User profile routes
router.get('/profile', async (req, res) => {
    try {
        res.json(req.user.toJSON());
    } catch (error) {
        console.error('Get profile error:', error);
        res.status(500).json({ error: 'Failed to get profile' });
    }
});

router.put('/profile', async (req, res) => {
    try {
        const { username, avatarUrl } = req.body;

        if (username) req.user.username = username;
        if (avatarUrl !== undefined) req.user.avatarUrl = avatarUrl;

        await req.user.save();
        res.json(req.user.toJSON());
    } catch (error) {
        console.error('Update profile error:', error);
        res.status(500).json({ error: 'Failed to update profile' });
    }
});

router.put('/password', async (req, res) => {
    try {
        const { oldPassword, newPassword } = req.body;

        if (!req.user.passwordHash) {
            return res.status(400).json({ error: 'No password set' });
        }

        const isValid = await req.user.comparePassword(oldPassword);
        if (!isValid) {
            return res.status(401).json({ error: 'Invalid password' });
        }

        req.user.passwordHash = newPassword;
        await req.user.save();

        res.json({ message: 'Password updated' });
    } catch (error) {
        console.error('Change password error:', error);
        res.status(500).json({ error: 'Failed to change password' });
    }
});

module.exports = router;
