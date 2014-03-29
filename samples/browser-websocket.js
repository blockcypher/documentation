var ws = new WebSocket("ws://api.bc.local:3322/v1/btc/main/socket");
ws.onmessage = function (event) {
  var child = document.createElement("div");
  var d = JSON.parse(event.data);
  child.innerHTML = "New block " + d.height + " / " + d.hash + " with " + d.n_tx + "transactions.";
  document.body.insertBefore(child, document.body.firstChild);
}
ws.send(JSON.stringify({filter: "event=new-block"}));
