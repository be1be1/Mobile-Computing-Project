var io = require('socket.io').listen(1234);
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
	//     longitude: Number,
	//     tags: String,
	//     starttime: {type: Date, default: Date.now},
	//     endtime: {type: Date, default: Date.now},
	//     description: String
	// });

	// var actModel = mongoose.model('ActData', actSchema);

	io.sockets.on('connection', function(socket) {
	  console.log('Connected! New user comes in!');


	  socket.on('upload', function(data) {
	  	var kitty = { "result": true};

	  	console.log(data);
	  	console.log(kitty);

	    socket.emit('upload_result', kitty);
	  });


	  socket.on('download', function(data) {

	  	var kitty = {"num_result":5,
	  	"result_data":[
	  	{"tags":"some,thi,ng","sub_type":2,"m_lat":data.tar_lat+11e-5,"location":"something","m_lon":data.tar_lon-8e-4,"description":"something","name":"something"},
	  	{"tags":"some,thi,ng","sub_type":3,"m_lat":data.tar_lat+21e-4,"location":"something","m_lon":data.tar_lon+14e-4,"description":"something","name":"something"},
	  	{"tags":"some,thi,ng","sub_type":4,"m_lat":data.tar_lat-12e-4,"location":"something","m_lon":data.tar_lon-15e-4,"description":"something","name":"something"},
	  	{"tags":"some,thi,ng","sub_type":5,"m_lat":data.tar_lat-17e-4,"location":"something","m_lon":data.tar_lon+12e-4,"description":"something","name":"something"},
	  	{"tags":"some,thi,ng","sub_type":2,"m_lat":data.tar_lat+16e-4,"location":"something","m_lon":data.tar_lon-21e-4,"description":"something","name":"something"}]};

	  	console.log(data);
	  	console.log(kitty);

	    socket.emit('download_data', kitty);
	  });

	});


