    var app = require('express')();
	var http = require('http').Server(app);
	var io = require('socket.io')(http);
	var mongoose = require('mongoose');
	var port = process.env.PORT || 3000;

	mongoose.connect('mongodb://localhost/activity', function(err){
	    if (err) {
	    	console.log(err);
	    } else {
	    	console.log('Connected to mongodb');
	    }
	});

	var actSchema = mongoose.Schema({
	    type: String,
	    class: Number,
	    name: String,
	    location: String,
	    latitude: Number,
	    longitude: Number,
	    tags: String,
	    starttime: {type: Date, default: Date.now},
	    endtime: {type: Date, default: Date.now},
	    description: String
	});

	var actModel = mongoose.model('ActData', actSchema);

	app.get('/', function(req, res){
	  res.sendFile(__dirname + '/index.html');
	});

	io.on('connection', function(socket){
	  console.log('new user comes in!');
	  socket.on('upload', function(data, callback){
	  	console.log(data.type);
	  	var newAct = new actModel({type:data.type, class:data.class});
	  	newAct.save(function(err){
	  		 if(err) throw err;
	  		 io.emit('show message', ' Upload success!');
	  	});
	  });

	  socket.on('download', function(data, callback){
	    actModel.find({longtitude: {$gt: data.longitude, $lt: data.longitude+10}, 
	     	            latitude: {$gt: data.latitude, $lt: data.latitude+10}}, 
	     	            function(err, docs){
	     	              if (err) throw err;
	     	              console.log('sending documents');
	     	              socket.emit('download_act', docs);
	                    });
	  });

	});

	http.listen(port, function(){
	  console.log('listening on *:' + port);
	});
