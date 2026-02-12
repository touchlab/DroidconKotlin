config.resolve = config.resolve || {};
config.resolve.fallback = Object.assign(config.resolve.fallback || {}, {
    fs: false,
    path: false,
    crypto: false,
    os: false,
});

// Serve sql.js WASM from node_modules (avoids needing copy-webpack-plugin)
const path = require('path');
if (config.devServer && config.devServer.static) {
    config.devServer.static.push({
        directory: path.resolve(__dirname, '../../node_modules/sql.js/dist'),
        watch: false
    });
}