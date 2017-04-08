var express = require('express');
var app = express();
var server = require('http').createServer(app).listen(1234);
var io = require('socket.io').listen(server);
var mongoose = require('mongoose');

console.log('Server Start.');

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
    longtitude: Number,
    tags: String,
    starttime: {type: Number, default: 0},
    endtime: {type: Number, default: 0},
    description: String
});

var actModel = mongoose.model('actData', actSchema);

io.on('connection', function(socket){
	console.log('wait for call');
	socket.on('upload', function(data, callback){
		console.log(data.type);
	  	var newAct = new actModel({type:data.type, class:data.class});
	  	newAct.save(function(err){
	  		 if(err) throw err;
	  		 io.emit('show message', ' Upload success!');
	  	});
	});

	socket.on('download', function(data, callback){
		console.log('Downloading');
		console.log(data.type);
	});
});