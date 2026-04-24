const LOG_LEVEL = process.env.REACT_APP_LOG_LEVEL || 'debug';

const levels = {
    debug: 0,
    info: 1,
    warn: 2,
    error: 3
};

const shouldLog = (level) => levels[level] >= levels[LOG_LEVEL];

const getTimestamp = () => new Date().toISOString();

export const logger = {
    debug: (message, ...args) => {
        if (shouldLog('debug')) {
            console.debug(`[${getTimestamp()}] [DEBUG] ${message}`, ...args);
        }
    },
    info: (message, ...args) => {
        if (shouldLog('info')) {
            console.info(`[${getTimestamp()}] [INFO] ${message}`, ...args);
        }
    },
    warn: (message, ...args) => {
        if (shouldLog('warn')) {
            console.warn(`[${getTimestamp()}] [WARN] ${message}`, ...args);
        }
    },
    error: (message, ...args) => {
        if (shouldLog('error')) {
            console.error(`[${getTimestamp()}] [ERROR] ${message}`, ...args);
        }
    }
};

