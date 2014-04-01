var ws = new WebSocket("ws://socket.blockcypher.com/v1/btc/main");
var count = 0;
ws.onmessage = function (event) {
  var tx = JSON.parse(event.data);
  var shortHash = tx.hash.substring(0, 6) + "...";
  var total = tx.total / 100000000;
  var addrs = tx.addresses.join(", ");
  console.log(tx.addresses)
  $('#browser-websocket').before("<div>Unconfirmed transaction " + shortHash + " of " + total +
                                   " involving addresses " + addrs + "</div>");
  count++;
  if (count > 10) ws.close();
}
ws.onopen = function(event) {
  ws.send(JSON.stringify({filter: "event=new-pool-tx"}));
}
