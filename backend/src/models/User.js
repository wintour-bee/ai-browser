const { DataTypes } = require('sequelize');
const bcrypt = require('bcryptjs');

module.exports = (sequelize) => {
    const User = sequelize.define('User', {
        id: {
            type: DataTypes.UUID,
            defaultValue: DataTypes.UUIDV4,
            primaryKey: true
        },
        email: {
            type: DataTypes.STRING(255),
            allowNull: false,
            unique: true,
            validate: {
                isEmail: true
            }
        },
        username: {
            type: DataTypes.STRING(50),
            allowNull: false,
            unique: true
        },
        passwordHash: {
            type: DataTypes.STRING(255),
            allowNull: true
        },
        avatarUrl: {
            type: DataTypes.STRING(500),
            allowNull: true
        },
        googleId: {
            type: DataTypes.STRING(255),
            unique: true,
            allowNull: true
        },
        points: {
            type: DataTypes.BIGINT,
            defaultValue: 0
        },
        inviteCode: {
            type: DataTypes.STRING(10),
            unique: true
        },
        usedInviteCode: {
            type: DataTypes.STRING(10),
            allowNull: true
        },
        role: {
            type: DataTypes.ENUM('user', 'vip', 'admin'),
            defaultValue: 'user'
        },
        lastLoginAt: {
            type: DataTypes.DATE,
            allowNull: true
        },
        lastSyncAt: {
            type: DataTypes.DATE,
            allowNull: true
        },
        settings: {
            type: DataTypes.JSONB,
            defaultValue: {}
        }
    }, {
        tableName: 'users',
        timestamps: true,
        hooks: {
            beforeCreate: async (user) => {
                if (user.passwordHash) {
                    user.passwordHash = await bcrypt.hash(user.passwordHash, 12);
                }
                if (!user.inviteCode) {
                    user.inviteCode = generateInviteCode();
                }
            },
            beforeUpdate: async (user) => {
                if (user.changed('passwordHash')) {
                    user.passwordHash = await bcrypt.hash(user.passwordHash, 12);
                }
            }
        }
    });

    User.prototype.comparePassword = async function(password) {
        return bcrypt.compare(password, this.passwordHash);
    };

    User.prototype.toJSON = function() {
        const values = { ...this.get() };
        delete values.passwordHash;
        return values;
    };

    function generateInviteCode() {
        const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
        let code = '';
        for (let i = 0; i < 8; i++) {
            code += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        return code;
    }

    return User;
};
