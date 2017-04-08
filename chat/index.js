
    var app = require('express')();
	var http = require('http').Server(app);
	var io = require('socket.io')(http);
	var mongoose = require('mongoose');
	var port = process.env.PORT || 1234;

	mongoose.connect('mongodb://localhost/activity', function(err){
	    if (err) {
	    	console.log(err);
	    } else {
	    	console.log('Connected to mongodb');
	    }
	});

	var actSchema = mongoose.Schema({
	    tags: String,
	    sub_type: Number,
	    m_lat: Number,
	    location: String,
	    m_lon: Number,
	    description: String,
	    name: String
	});

	var actModel = mongoose.model('ActData', actSchema);

	app.get('/', function(req, res){
	  res.sendFile(__dirname + '/index.html');
	});

	io.sockets.on('connection', function(socket){
	  console.log('Connected! New user comes in.');

	  socket.on('upload', function(data, callback){
	  	console.log(data.type);
	  	var newAct = new actModel({type:data.type, class:data.class});
	  	newAct.save(function(err){
	  		 var result_value = {"result": true};
	  		 if(err) {
	  		 	result_value = {"result": false};
	  		 	throw err;
	  		 }
	  		 io.emit('upload_result', result_value);
	  	});
	  });

	  socket.on('download', function(data, callback){
	    actModel.find({longtitude: {$gt: data.longitude-1e-3, $lt: data.longitude+1e-3}, 
	     	            latitude: {$gt: data.latitude-1e-3, $lt: data.latitude+1e-3}}, 
	     	            function(err, docs){
	     	              if (err) {
	     	              	var result_value = {"num_result": 0};
	     	              	socket.emit('download_data', result_value);
	     	              	throw err;
	     	              } else {
	     	                console.log('sending documents');
	     	                socket.emit('download_data', docs);
	     	              }
	                    });
	  });

	});

	http.listen(port, function(){
	  console.log('listening on *:' + port);
	});
