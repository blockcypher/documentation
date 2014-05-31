var http = require("http");

// Setup the http server we'll get the callbacks on
var server = http.createServer(function(req, res) {
  console.log("Received request.");
  var data = "";
  req.on('data', function(chunk) {
    data += chunk.toString();
  });
  req.on('error', function(e) {
    console.log('error', e);
  })

  req.on('end', function() {
    console.log("Transaction:\n", JSON.parse(data));
    res.writeHead(200);
    res.end();
  });
});

server.listen(51413);
console.log("Server is listening");

// Prepare the request to create the webhook
// CHANGE TO YOUR IP ADDRESS FOR TESTING and make sure it's reachable from the outside
var data = JSON.stringify({url: "http://173.228.17.216:51413", filter: "event=new-pool-tx"});

var options = {
  host: 'api.blockcypher.com',
  port: '80',
  path: '/v1/btc/main/hooks',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Content-Length': data.length
  }
};

// Set up the request
var req = http.request(options, function(res) {
  if (res.statusCode != 201) {
    log.Println("Error when creating webhook:", res.statusCode)
  }
  res.setEncoding('utf8');
  res.on('data', function (chunk) {
      console.log('Response: ' + chunk);
  });
});

// Post the data
req.write(data);
req.end();

console.log("WebHook created, expecting callbacks...");
