var io = require('socket.io').listen('1234');
var mongoose = require('mongoose');

console.log('Server Start.');

// mongoose.connect('mongodb://localhost/activity', function(err){
//     if (err) {
//     	console.log(err);
//     } else {
//     	console.log('Connected to mongodb');
//     }
// });

// var actSchema = mongoose.Schema({
//     type: String,
//     class: Number,
//     name: String,
//     location: String,
//     latitude: Number,
//     longtitude: Number,
//     tags: String,
//     starttime: {type: Date, default: Date.now},
//     endtime: {type: Date, default: Date.now},
//     description: String
// });

// var actModel = mongoose.model('actData', actSchema);

io.on('connection', function(socket){
	console.log('wait for call');
	socket.on('upload', function(data, callback){
		console.log('Uploading');
		console.log(data.type);
	});

	socket.on('download', function(data, callback){
		console.log('Downloading');
		console.log(data.type);
	});
});