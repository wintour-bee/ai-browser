const { DataTypes } = require('sequelize');

module.exports = (sequelize) => {
    return sequelize.define('RefreshToken', {
        id: {
            type: DataTypes.UUID,
            defaultValue: DataTypes.UUIDV4,
            primaryKey: true
        },
        token: {
            type: DataTypes.STRING(500),
            allowNull: false,
            unique: true
        },
        userId: {
            type: DataTypes.UUID,
            allowNull: false,
            references: {
                model: 'users',
                key: 'id'
            }
        },
        expiresAt: {
            type: DataTypes.DATE,
            allowNull: false
        },
        deviceInfo: {
            type: DataTypes.JSONB,
            allowNull: true
        },
        isRevoked: {
            type: DataTypes.BOOLEAN,
            defaultValue: false
        }
    }, {
        tableName: 'refresh_tokens',
        timestamps: true
    });
};

module.exports = (sequelize) => {
    const VipStatus = sequelize.define('VipStatus', {
        id: {
            type: DataTypes.UUID,
            defaultValue: DataTypes.UUIDV4,
            primaryKey: true
        },
        userId: {
            type: DataTypes.UUID,
            allowNull: false,
            references: {
                model: 'users',
                key: 'id'
            }
        },
        level: {
            type: DataTypes.INTEGER,
            defaultValue: 0
        },
        isActive: {
            type: DataTypes.BOOLEAN,
            defaultValue: false
        },
        expireTime: {
            type: DataTypes.DATE,
            allowNull: true
        },
        features: {
            type: DataTypes.JSONB,
            defaultValue: []
        },
        purchaseToken: {
            type: DataTypes.STRING(255),
            allowNull: true
        },
        productId: {
            type: DataTypes.STRING(100),
            allowNull: true
        }
    }, {
        tableName: 'vip_statuses',
        timestamps: true
    });

    return VipStatus;
};

module.exports = (sequelize) => {
    return sequelize.define('Bookmark', {
        id: {
            type: DataTypes.UUID,
            defaultValue: DataTypes.UUIDV4,
            primaryKey: true
        },
        userId: {
            type: DataTypes.UUID,
            allowNull: false,
            references: {
                model: 'users',
                key: 'id'
            }
        },
        title: {
            type: DataTypes.STRING(500),
            allowNull: false
        },
        url: {
            type: DataTypes.STRING(2000),
            allowNull: false
        },
        iconUrl: {
            type: DataTypes.STRING(1000),
            allowNull: true
        },
        folderId: {
            type: DataTypes.UUID,
            allowNull: true
        }
    }, {
        tableName: 'bookmarks',
        timestamps: true
    });
};

module.exports = (sequelize) => {
    return sequelize.define('History', {
        id: {
            type: DataTypes.UUID,
            defaultValue: DataTypes.UUIDV4,
            primaryKey: true
        },
        userId: {
            type: DataTypes.UUID,
            allowNull: false,
            references: {
                model: 'users',
                key: 'id'
            }
        },
        title: {
            type: DataTypes.STRING(500),
            allowNull: false
        },
        url: {
            type: DataTypes.STRING(2000),
            allowNull: false
        },
        iconUrl: {
            type: DataTypes.STRING(1000),
            allowNull: true
        },
        visitCount: {
            type: DataTypes.INTEGER,
            defaultValue: 1
        },
        lastVisitedAt: {
            type: DataTypes.DATE,
            defaultValue: DataTypes.NOW
        }
    }, {
        tableName: 'history',
        timestamps: true,
        updatedAt: 'lastVisitedAt'
    });
};

module.exports = (sequelize) => {
    return sequelize.define('Tab', {
        id: {
            type: DataTypes.UUID,
            defaultValue: DataTypes.UUIDV4,
            primaryKey: true
        },
        userId: {
            type: DataTypes.UUID,
            allowNull: false,
            references: {
                model: 'users',
                key: 'id'
            }
        },
        url: {
            type: DataTypes.STRING(2000),
            defaultValue: 'about:blank'
        },
        title: {
            type: DataTypes.STRING(500),
            defaultValue: 'New Tab'
        },
        iconUrl: {
            type: DataTypes.STRING(1000),
            allowNull: true
        },
        isIncognito: {
            type: DataTypes.BOOLEAN,
            defaultValue: false
        },
        position: {
            type: DataTypes.INTEGER,
            defaultValue: 0
        },
        scrollPosition: {
            type: DataTypes.INTEGER,
            defaultValue: 0
        }
    }, {
        tableName: 'tabs',
        timestamps: true
    });
};

module.exports = (sequelize) => {
    return sequelize.define('VPNServer', {
        id: {
            type: DataTypes.UUID,
            defaultValue: DataTypes.UUIDV4,
            primaryKey: true
        },
        name: {
            type: DataTypes.STRING(100),
            allowNull: false
        },
        country: {
            type: DataTypes.STRING(50),
            allowNull: false
        },
        city: {
            type: DataTypes.STRING(100),
            allowNull: false
        },
        host: {
            type: DataTypes.STRING(255),
            allowNull: false
        },
        port: {
            type: DataTypes.INTEGER,
            allowNull: false
        },
        protocol: {
            type: DataTypes.STRING(20),
            defaultValue: 'vless'
        },
        config: {
            type: DataTypes.TEXT,
            allowNull: false
        },
        pingLatency: {
            type: DataTypes.INTEGER,
            allowNull: true
        },
        isPremium: {
            type: DataTypes.BOOLEAN,
            defaultValue: false
        },
        load: {
            type: DataTypes.FLOAT,
            defaultValue: 0
        },
        isActive: {
            type: DataTypes.BOOLEAN,
            defaultValue: true
        }
    }, {
        tableName: 'vpn_servers',
        timestamps: true
    });
};

module.exports = (sequelize) => {
    return sequelize.define('VPNSubscription', {
        id: {
            type: DataTypes.UUID,
            defaultValue: DataTypes.UUIDV4,
            primaryKey: true
        },
        userId: {
            type: DataTypes.UUID,
            allowNull: false,
            references: {
                model: 'users',
                key: 'id'
            }
        },
        url: {
            type: DataTypes.STRING(1000),
            allowNull: false
        },
        name: {
            type: DataTypes.STRING(100),
            allowNull: false
        },
        serverCount: {
            type: DataTypes.INTEGER,
            defaultValue: 0
        },
        expiresAt: {
            type: DataTypes.DATE,
            allowNull: true
        },
        isActive: {
            type: DataTypes.BOOLEAN,
            defaultValue: true
        }
    }, {
        tableName: 'vpn_subscriptions',
        timestamps: true
    });
};

module.exports = (sequelize) => {
    return sequelize.define('Invite', {
        id: {
            type: DataTypes.UUID,
            defaultValue: DataTypes.UUIDV4,
            primaryKey: true
        },
        inviterId: {
            type: DataTypes.UUID,
            allowNull: false,
            references: {
                model: 'users',
                key: 'id'
            }
        },
        inviteeId: {
            type: DataTypes.UUID,
            allowNull: true,
            references: {
                model: 'users',
                key: 'id'
            }
        },
        code: {
            type: DataTypes.STRING(10),
            allowNull: false
        },
        rewardAmount: {
            type: DataTypes.BIGINT,
            defaultValue: 100
        },
        isUsed: {
            type: DataTypes.BOOLEAN,
            defaultValue: false
        }
    }, {
        tableName: 'invites',
        timestamps: true
    });
};

module.exports = (sequelize) => {
    return sequelize.define('Checkin', {
        id: {
            type: DataTypes.UUID,
            defaultValue: DataTypes.UUIDV4,
            primaryKey: true
        },
        userId: {
            type: DataTypes.UUID,
            allowNull: false,
            references: {
                model: 'users',
                key: 'id'
            }
        },
        checkinDate: {
            type: DataTypes.DATEONLY,
            allowNull: false
        },
        points: {
            type: DataTypes.BIGINT,
            defaultValue: 10
        },
        streak: {
            type: DataTypes.INTEGER,
            defaultValue: 1
        }
    }, {
        tableName: 'checkins',
        timestamps: true,
        indexes: [
            { unique: true, fields: ['userId', 'checkinDate'] }
        ]
    });
};

module.exports = (sequelize) => {
    return sequelize.define('AISession', {
        id: {
            type: DataTypes.UUID,
            defaultValue: DataTypes.UUIDV4,
            primaryKey: true
        },
        userId: {
            type: DataTypes.UUID,
            allowNull: false,
            references: {
                model: 'users',
                key: 'id'
            }
        },
        provider: {
            type: DataTypes.STRING(50),
            defaultValue: 'chatgpt'
        },
        title: {
            type: DataTypes.STRING(200),
            defaultValue: 'New Chat'
        }
    }, {
        tableName: 'ai_sessions',
        timestamps: true
    });
};

module.exports = (sequelize) => {
    return sequelize.define('AIMessage', {
        id: {
            type: DataTypes.UUID,
            defaultValue: DataTypes.UUIDV4,
            primaryKey: true
        },
        sessionId: {
            type: DataTypes.UUID,
            allowNull: false,
            references: {
                model: 'ai_sessions',
                key: 'id'
            }
        },
        role: {
            type: DataTypes.STRING(20),
            allowNull: false
        },
        content: {
            type: DataTypes.TEXT,
            allowNull: false
        },
        imageUrls: {
            type: DataTypes.JSONB,
            defaultValue: []
        }
    }, {
        tableName: 'ai_messages',
        timestamps: true
    });
};
