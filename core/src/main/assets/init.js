
require('process').on('uncaughtException', (err) => {
    console.error(err.stack);
});
