config.resolve = config.resolve || {};
config.resolve.fallback = Object.assign(config.resolve.fallback || {}, {
    fs: false,
    path: false,
    crypto: false,
    os: false,
});

const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');

const sqlJsDist = path.resolve(__dirname, '../../node_modules/sql.js/dist');
config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            { from: path.join(sqlJsDist, 'sql-wasm.wasm'), to: '.' },
        ],
    }),
);
