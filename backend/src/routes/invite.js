const express = require('express');
const router = express.Router();

// Get invite code
router.get('/code', async (req, res) => {
    try {
        const { Invite } = require('../config/database');
        
        const invites = await Invite.findAll({
            where: { inviterId: req.userId, isUsed: true }
        });

        const totalRewards = invites.reduce((sum, i) => sum + Number(i.rewardAmount), 0);

        res.json({
            code: req.user.inviteCode,
            url: `${process.env.APP_URL || 'https://aibrowser.app'}/register?code=${req.user.inviteCode}`,
            totalUses: invites.length,
            totalRewards
        });
    } catch (error) {
        console.error('Get invite code error:', error);
        res.status(500).json({ error: 'Failed to get invite code' });
    }
});

// Use invite code
router.post('/use', async (req, res) => {
    try {
        const { Invite, User } = require('../config/database');
        const { code } = req.body;

        if (req.user.usedInviteCode) {
            return res.status(400).json({ error: 'Invite code already used' });
        }

        if (code === req.user.inviteCode) {
            return res.status(400).json({ error: 'Cannot use own invite code' });
        }

        const inviter = await User.findOne({ where: { inviteCode: code } });
        if (!inviter) {
            return res.status(404).json({ error: 'Invalid invite code' });
        }

        // Reward inviter
        await inviter.increment('points', { by: 100 });
        await Invite.create({
            inviterId: inviter.id,
            inviteeId: req.userId,
            code,
            rewardAmount: 100,
            isUsed: true
        });

        // Mark invite code as used
        req.user.usedInviteCode = code;
        await req.user.save();

        res.json({ message: 'Invite code used successfully' });
    } catch (error) {
        console.error('Use invite code error:', error);
        res.status(500).json({ error: 'Failed to use invite code' });
    }
});

// Get invite rewards
router.get('/rewards', async (req, res) => {
    try {
        const { Invite, User } = require('../config/database');

        const invites = await Invite.findAll({
            where: { inviterId: req.userId, isUsed: true },
            include: [{ model: User, as: 'invitee', attributes: ['username'] }]
        });

        res.json(invites.map(i => ({
            id: i.id,
            inviterUsername: i.Invitee?.username || 'Unknown',
            rewardAmount: Number(i.rewardAmount),
            rewardedAt: i.createdAt.getTime()
        })));
    } catch (error) {
        console.error('Get rewards error:', error);
        res.status(500).json({ error: 'Failed to get rewards' });
    }
});

module.exports = router;
