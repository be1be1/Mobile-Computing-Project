    var io = require('socket.io').listen(1234);
    var jsonfile = require('jsonfile');
	var mongoose = require('mongoose');

	mongoose.connect('mongodb://localhost/activity', function(err){
	    if (err) {
	    	console.log(err);
	    } else {
	    	console.log('Connected to mongodb');
	    }
	});

	var actSchema = mongoose.Schema({
		type: String,
	    tags: String,
	    sub_type: Number,
	    m_lat: Number,
	    location: String,
	    m_lon: Number,
	    description: String,
	    name: String,
	    stime: Number,
	    etime: Number,
	});

	var actModel = mongoose.model('ActData', actSchema);

	io.sockets.on('connection', function(socket){
	  console.log('Connected! New user comes in.');

	  socket.on('upload', function(data, callback){
	  	console.log(data);
	  	var newAct = new actModel({type:data.type, tags:data.tags, sub_type:data.sub_type, m_lat:data.m_lat, location:data.location, m_lon:data.m_lon,
	  	                           description: data.description, name: data.name, stime: data.stime, etime: data.etime});
	  	newAct.save(function(err){
	  		 var result_value = {"result": true};
	  		 if(err) {
	  		 	result_value = {"result": false};
	  		 	throw err;
	  		 }

	  		 socket.emit('upload_result', result_value);
	  	});
	  });

	  socket.on('download', function(data, callback){
	    actModel.find({m_lon: {$gt: data.m_lon-1e-3, $lt: data.m_lon+1e-3}, 
	     	           m_lat: {$gt: data.m_lat-1e-3, $lt: data.m_lat+1e-3},
	     	           type: data.type,
	     	           stime: {$lt: data.ntime},
	     	           etime: {$gt: data.ntime}
	     	           }, 

	     	            function(err, docs){
	     	              if (err) {
	     	              	var result_value = {"num_result": 0};
	     	              	socket.emit('download_data', result_value);
	     	              	throw err;
	     	              } else {
	     	                console.log('sending documents');
	     	                var kitty = {"num_result":1,
	  						"result_data":docs};
	     	                socket.emit('download_data', kitty);
	     	              }
	                    });
	  });

	});
