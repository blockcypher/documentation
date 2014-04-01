var ws = new WebSocket("ws://socket.blockcypher.com/v1/btc/main");
ws.onmessage = function (event) {
  var tx = JSON.parse(event.data);
  var shortHash = tx.hash.substring(0, 6) + "...";
  var total = tx.total / 100000000;
  var addrs = tx.addresses.join(", ");
  $('#browser-websockets').before("<div>Unconfirmed transaction " + shortHash + "of" + total +
                                   "involving addresses" + addrs + "</div>");
}
ws.onopen = function(event) {
  console.log("connected");
  ws.send(JSON.stringify({filter: "event=new-pool-tx"}));
}
