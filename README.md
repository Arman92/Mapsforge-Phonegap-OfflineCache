Mapsforge-Phonegap-OfflineCache
===============================
A Phonegap plugin to render and cache mapsforge offline maps (.map files) and wrapping by leaflet map viewer.

Install Instructions:
=====================
1- Install the plugin by running this command on your phonegap app folder :
  'cordova plugin add https://github.com/Arman92/Mapsforge-Phonegap-OfflineCache.git'

2- In order to get mapsforge renderer classes to work, we need to initilize AndroidGraphicFactory class,
so add thess lines to your phonegap onCreate method at your Activity class:
   AndroidGraphicFactory.createInstance(this.getApplication());
  
3- Call to StaticTileCache.initialize method in order to initilize neccessery objects: 
    StaticTileCache.initialize(Environment.getExternalStorageDirectory() + "/iran.map", this);
where "/iran.map" referes to your mapsforge offline map. (in which you can download from here : http://download.mapsforge.org/maps/)
				
4- Add these lines to your deviceReady function in your phonegap www/js/index.js file : 

    L.OfflineTileLayer = L.TileLayer.extend({
        getTileUrl: function (tilePoint, tile) {
            var z = tilePoint.z,
			    x = tilePoint.x,
			    y = tilePoint.y;
            console.log("given coordinates:" + x + "," + y + "," + z);
            if (window.plugins.mapsforgeTileCache) {
                window.plugins.mapsforgeTileCache.getCachedTile(function (result) {
                    if (result.tile) {
                        tile.src = result.tile;
                        console.log(x + "," + y + "," + z + ":success, tile retireved");
                    } else {
                        console.log(x + "," + y + "," + z + ":success, but no tile");
                        tile.src = "img/MapTileUnavailable.png";
                    }
                }, function (error) {
                    console.log(x + "," + y + "," + z + ":error: " + error);
                    console.log("error");
                    tile.src = "img/MapTileUnavailable.png";

                }, [
				    x,
				    y,
				    z
                ]);

            } else {
                console.log("plugin not found");
                tile.src = "img/MapTileUnavailable.png";
            }
        },
        _loadTile: function (tile, tilePoint) {
            tile._layer = this;
            tile.onload = this._tileOnLoad;
            tile.onerror = this._tileOnError;

            this._adjustTilePoint(tilePoint);
            this.getTileUrl(tilePoint, tile);

            this.fire('tileloadstart', {
                tile: tile,
                url: tile.src
            });
        },
    });

    L.offlineTileLayer = function (url, options) {
        return new L.OfflineTileLayer(url, options);
    };

    var map = L.map('map').setView([32.63, 51.63], 10);

    L.offlineTileLayer('http://COULD-BE-ANY-THING.EVEN-NULL', {
        attribution: 'Dolphin Map Viewer',
        maxZoom: 18
    }).addTo(map);
    
