const express = require('express');
const router = express.Router();

// VIP routes
router.get('/status', async (req, res) => {
    try {
        const { VipStatus } = require('../config/database');
        let vipStatus = await VipStatus.findOne({ where: { userId: req.userId } });

        if (!vipStatus) {
            vipStatus = await VipStatus.create({ userId: req.userId });
        }

        const isActive = vipStatus.isActive && vipStatus.expireTime > new Date();

        res.json({
            isVip: isActive,
            level: vipStatus.level,
            expireTime: vipStatus.expireTime?.getTime(),
            features: vipStatus.features
        });
    } catch (error) {
        console.error('Get VIP status error:', error);
        res.status(500).json({ error: 'Failed to get VIP status' });
    }
});

router.post('/activate', async (req, res) => {
    try {
        const { VipStatus } = require('../config/database');
        const { purchaseToken, productId } = req.body;

        // Verify with Google Play (in production)
        // For now, activate VIP
        let vipStatus = await VipStatus.findOne({ where: { userId: req.userId } });

        if (!vipStatus) {
            vipStatus = await VipStatus.create({
                userId: req.userId,
                level: 1,
                isActive: true,
                expireTime: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000), // 30 days
                features: ['all']
            });
        } else {
            vipStatus.level = 1;
            vipStatus.isActive = true;
            vipStatus.expireTime = new Date(Date.now() + 30 * 24 * 60 * 60 * 1000);
            await vipStatus.save();
        }

        res.json({
            isVip: true,
            level: vipStatus.level,
            expireTime: vipStatus.expireTime.getTime(),
            features: vipStatus.features
        });
    } catch (error) {
        console.error('Activate VIP error:', error);
        res.status(500).json({ error: 'Failed to activate VIP' });
    }
});

module.exports = router;
