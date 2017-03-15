var fs = require('fs');
var io = require('socket.io').listen(1234);
var spawn = require('child_process').spawn;
var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;
var jsonfile = require('jsonfile');
var base64Img;

console.log('Your server has started successfully!');

io.on('connection', function (socket) {

  socket.on('queryImageDataString', function(info) {
    // Save img to local storage
    var img = 'data:image/jpeg;base64,' + info.buffer;
    var data = img.replace(/^data:image\/\w+;base64,/, "");
    var buf = new Buffer(data, 'base64');
    var imgNameStamp = "" + Date.now();
    var imgName = "image_" + imgNameStamp + ".jpg";
    var imgDirectory = "queryImages/" + imgName;

    fs.writeFile(imgDirectory, buf);
    console.log('Image Saved as: ' + imgName);

    // Run SmartCity_OpenCV to do recognition
    var recognition = spawn('./EyeDetector', ['./queryImages/'+imgName]);

    recognition.stdout.on('data', (data) => {
      var imgInfo = `${data}`;
      var newImgInfo = imgInfo.split(/\r?\n/);
      console.log('First element:'+newImgInfo[0]);

      var attachment = newImgInfo[2];
      console.log('Trying to transfer img');
      fs.readFile(attachment, function read(err, data) {
        if(err) {
          throw err;
        }
        base64Img = new Buffer(data, 'binary').toString('base64');
      });
      console.log('Transfering your img:'+attachment);

      var resultJson = {
        'is_eye': newImgInfo[0],
        'score': newImgInfo[1],
        'image': base64Img
        };
      console.log(base64Img);
      socket.emit('message', {'result': resultJson});
    });

    recognition.stderr.on('data', (data) => {
      console.log(`stderr: ${data}`);
    });
    recognition.on('close', (code) => {
      console.log(`process exited with code ${code}`);
    });
  });
});
