var exec = require("cordova/exec");
module.exports = {
    getCachedTile: function(success, failure, options) {
    
        exec(success || function() {},
             failure || function() {},
             'MapsforgeTileCache',
             'getTile',
             options);
    }

};
