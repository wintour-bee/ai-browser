const errorHandler = (err, req, res, next) => {
    console.error('Error:', err);

    if (err.name === 'SequelizeValidationError') {
        return res.status(400).json({
            error: 'Validation error',
            details: err.errors.map(e => ({
                field: e.path,
                message: e.message
            }))
        });
    }

    if (err.name === 'SequelizeUniqueConstraintError') {
        return res.status(409).json({
            error: 'Resource already exists',
            details: err.errors.map(e => ({
                field: e.path,
                message: e.message
            }))
        });
    }

    if (err.status) {
        return res.status(err.status).json({
            error: err.message
        });
    }

    res.status(500).json({
        error: process.env.NODE_ENV === 'production' 
            ? 'Internal server error' 
            : err.message
    });
};

module.exports = { errorHandler };
