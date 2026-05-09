const express = require('express');
const router = express.Router();
const { User, Bookmark, History, Tab } = require('../config/database');
const { Op } = require('sequelize');

// Sync bookmarks
router.get('/bookmarks', async (req, res) => {
    try {
        const since = req.query.since ? new Date(parseInt(req.query.since)) : new Date(0);
        
        const bookmarks = await Bookmark.findAll({
            where: { userId: req.userId, updatedAt: { [Op.gt]: since } }
        });

        res.json(bookmarks.map(b => ({
            id: b.id,
            title: b.title,
            url: b.url,
            iconUrl: b.iconUrl,
            folderId: b.folderId,
            createdAt: b.createdAt.getTime(),
            updatedAt: b.updatedAt.getTime()
        })));
    } catch (error) {
        console.error('Sync bookmarks error:', error);
        res.status(500).json({ error: 'Failed to sync bookmarks' });
    }
});

router.post('/bookmarks', async (req, res) => {
    try {
        const bookmarks = req.body;
        
        for (const bookmark of bookmarks) {
            await Bookmark.upsert({
                id: bookmark.id,
                userId: req.userId,
                title: bookmark.title,
                url: bookmark.url,
                iconUrl: bookmark.iconUrl,
                folderId: bookmark.folderId
            });
        }

        res.json({ success: true });
    } catch (error) {
        console.error('Sync bookmarks error:', error);
        res.status(500).json({ error: 'Failed to sync bookmarks' });
    }
});

// Sync history
router.get('/history', async (req, res) => {
    try {
        const since = req.query.since ? new Date(parseInt(req.query.since)) : new Date(0);
        
        const history = await History.findAll({
            where: { userId: req.userId, updatedAt: { [Op.gt]: since } }
        });

        res.json(history.map(h => ({
            id: h.id,
            title: h.title,
            url: h.url,
            iconUrl: h.iconUrl,
            visitedAt: h.lastVisitedAt.getTime(),
            visitCount: h.visitCount
        })));
    } catch (error) {
        console.error('Sync history error:', error);
        res.status(500).json({ error: 'Failed to sync history' });
    }
});

router.post('/history', async (req, res) => {
    try {
        const historyItems = req.body;
        
        for (const item of historyItems) {
            await History.upsert({
                id: item.id,
                userId: req.userId,
                title: item.title,
                url: item.url,
                iconUrl: item.iconUrl,
                visitCount: item.visitCount
            });
        }

        res.json({ success: true });
    } catch (error) {
        console.error('Sync history error:', error);
        res.status(500).json({ error: 'Failed to sync history' });
    }
});

// Sync tabs
router.get('/tabs', async (req, res) => {
    try {
        const tabs = await Tab.findAll({
            where: { userId: req.userId }
        });

        res.json(tabs.map(t => ({
            id: t.id,
            url: t.url,
            title: t.title,
            iconUrl: t.iconUrl,
            isIncognito: t.isIncognito,
            position: t.position,
            scrollPosition: t.scrollPosition,
            createdAt: t.createdAt.getTime()
        })));
    } catch (error) {
        console.error('Sync tabs error:', error);
        res.status(500).json({ error: 'Failed to sync tabs' });
    }
});

router.post('/tabs', async (req, res) => {
    try {
        const tabs = req.body;
        
        for (const tab of tabs) {
            await Tab.upsert({
                id: tab.id,
                userId: req.userId,
                url: tab.url,
                title: tab.title,
                iconUrl: tab.iconUrl,
                isIncognito: tab.isIncognito,
                position: tab.position,
                scrollPosition: tab.scrollPosition
            });
        }

        res.json({ success: true });
    } catch (error) {
        console.error('Sync tabs error:', error);
        res.status(500).json({ error: 'Failed to sync tabs' });
    }
});

module.exports = router;
