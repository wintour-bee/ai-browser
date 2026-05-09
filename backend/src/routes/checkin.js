const express = require('express');
const router = express.Router();

// Daily check-in
router.post('/daily', async (req, res) => {
    try {
        const { Checkin, User } = require('../config/database');
        
        const today = new Date().toISOString().split('T')[0];
        
        // Check if already checked in today
        const existing = await Checkin.findOne({
            where: { userId: req.userId, checkinDate: today }
        });

        if (existing) {
            return res.status(400).json({ error: 'Already checked in today' });
        }

        // Get last check-in
        const lastCheckin = await Checkin.findOne({
            where: { userId: req.userId },
            order: [['checkinDate', 'DESC']]
        });

        // Calculate streak
        let streak = 1;
        if (lastCheckin) {
            const lastDate = new Date(lastCheckin.checkinDate);
            const yesterday = new Date();
            yesterday.setDate(yesterday.getDate() - 1);
            
            if (lastDate.toISOString().split('T')[0] === yesterday.toISOString().split('T')[0]) {
                streak = lastCheckin.streak + 1;
            }
        }

        // Calculate points (more for longer streaks)
        const basePoints = 10;
        const bonusPoints = Math.min(streak * 2, 50);
        const totalPoints = basePoints + bonusPoints;

        // Create check-in record
        await Checkin.create({
            userId: req.userId,
            checkinDate: today,
            points: totalPoints,
            streak
        });

        // Add points to user
        await req.user.increment('points', { by: totalPoints });

        res.json({
            success: true,
            points: totalPoints,
            streak
        });
    } catch (error) {
        console.error('Check-in error:', error);
        res.status(500).json({ error: 'Check-in failed' });
    }
});

// Get check-in status
router.get('/status', async (req, res) => {
    try {
        const { Checkin } = require('../config/database');
        
        const today = new Date().toISOString().split('T')[0];
        
        // Check today's status
        const todayCheckin = await Checkin.findOne({
            where: { userId: req.userId, checkinDate: today }
        });

        // Get current streak
        const lastCheckin = await Checkin.findOne({
            where: { userId: req.userId },
            order: [['checkinDate', 'DESC']]
        });

        res.json({
            checkedInToday: !!todayCheckin,
            currentStreak: lastCheckin?.streak || 0,
            totalPoints: Number(req.user.points),
            nextReward: 10 + Math.min((lastCheckin?.streak || 0) * 2, 50)
        });
    } catch (error) {
        console.error('Get check-in status error:', error);
        res.status(500).json({ error: 'Failed to get status' });
    }
});

module.exports = router;
