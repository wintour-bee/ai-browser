const express = require('express');
const router = express.Router();

// Get settings
router.get('/', async (req, res) => {
    try {
        res.json(req.user.settings || {});
    } catch (error) {
        console.error('Get settings error:', error);
        res.status(500).json({ error: 'Failed to get settings' });
    }
});

// Update settings
router.put('/', async (req, res) => {
    try {
        const settings = req.body;
        
        req.user.settings = { ...req.user.settings, ...settings };
        await req.user.save();

        res.json(req.user.settings);
    } catch (error) {
        console.error('Update settings error:', error);
        res.status(500).json({ error: 'Failed to update settings' });
    }
});

module.exports = router;
