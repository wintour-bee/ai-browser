const { Sequelize } = require('sequelize');

const sequelize = new Sequelize(
    process.env.DB_NAME || 'ai_browser',
    process.env.DB_USER || 'postgres',
    process.env.DB_PASSWORD || 'postgres',
    {
        host: process.env.DB_HOST || 'localhost',
        port: process.env.DB_PORT || 5432,
        dialect: 'postgres',
        logging: process.env.NODE_ENV === 'development' ? console.log : false,
        pool: {
            max: 10,
            min: 0,
            acquire: 30000,
            idle: 10000
        },
        define: {
            timestamps: true,
            underscored: true
        }
    }
);

// Import models
const User = require('../models/User')(sequelize);
const RefreshToken = require('../models/RefreshToken')(sequelize);
const VipStatus = require('../models/VipStatus')(sequelize);
const Bookmark = require('../models/Bookmark')(sequelize);
const History = require('../models/History')(sequelize);
const Tab = require('../models/Tab')(sequelize);
const VPNServer = require('../models/VPNServer')(sequelize);
const VPNSubscription = require('../models/VPNSubscription')(sequelize);
const Invite = require('../models/Invite')(sequelize);
const Checkin = require('../models/Checkin')(sequelize);
const AISession = require('../models/AISession')(sequelize);
const AIMessage = require('../models/AIMessage')(sequelize);

// Define associations
User.hasOne(VipStatus, { foreignKey: 'userId', as: 'vipStatus' });
VipStatus.belongsTo(User, { foreignKey: 'userId' });

User.hasMany(RefreshToken, { foreignKey: 'userId', as: 'refreshTokens' });
RefreshToken.belongsTo(User, { foreignKey: 'userId' });

User.hasMany(Bookmark, { foreignKey: 'userId', as: 'bookmarks' });
Bookmark.belongsTo(User, { foreignKey: 'userId' });

User.hasMany(History, { foreignKey: 'userId', as: 'history' });
History.belongsTo(User, { foreignKey: 'userId' });

User.hasMany(Tab, { foreignKey: 'userId', as: 'tabs' });
Tab.belongsTo(User, { foreignKey: 'userId' });

User.hasMany(VPNSubscription, { foreignKey: 'userId', as: 'vpnSubscriptions' });
VPNSubscription.belongsTo(User, { foreignKey: 'userId' });

User.hasMany(Invite, { foreignKey: 'inviterId', as: 'invites' });
Invite.belongsTo(User, { foreignKey: 'inviterId', as: 'inviter' });
Invite.belongsTo(User, { foreignKey: 'inviteeId', as: 'invitee' });

User.hasMany(Checkin, { foreignKey: 'userId', as: 'checkins' });
Checkin.belongsTo(User, { foreignKey: 'userId' });

User.hasMany(AISession, { foreignKey: 'userId', as: 'aiSessions' });
AISession.belongsTo(User, { foreignKey: 'userId' });

AISession.hasMany(AIMessage, { foreignKey: 'sessionId', as: 'messages' });
AIMessage.belongsTo(AISession, { foreignKey: 'sessionId' });

module.exports = {
    sequelize,
    User,
    RefreshToken,
    VipStatus,
    Bookmark,
    History,
    Tab,
    VPNServer,
    VPNSubscription,
    Invite,
    Checkin,
    AISession,
    AIMessage
};
