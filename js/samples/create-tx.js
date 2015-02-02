var bitcoin = require("bitcoinjs-lib");
var bigi    = require("bigi");
var buffer  = require('buffer');

var rootUrl = "https://api.blockcypher.com/v1/btc/main";
// please do not drain our test account, if you need testnet BTC use a faucet
// https://tpfaucet.appspot.com/
var source = {
  private : "a2f21b63e2ded430707532030e0fa38ab7becbb0a213c8286110b73abe910bc6",
  public  : "034aa78e5e57b45163abe7e3815c3881d7e218026bc0e32f9063d7f3b71b134f3f",
  address : "15qx9ug952GWGTNn7Uiv6vode4RcGrRemh"
}
var key   = new bitcoin.ECKey(bigi.fromHex(source.private), true);
var dest  = null;

// 0. We get a newly generated address
function logAddr(addr) {
  dest = addr;
  log("Generated new address " + dest.address)
}

// 1. Post our simple transaction information to get back the fully built transaction,
//    includes fees when required.
function newTransaction() {
  var newtx = {
    "inputs": [{"addresses": [source.address]}],
    "outputs": [{"addresses": ["18vXq4DbQBTWz1WPaNWScPSuY3XDMypSeX"], "value": 10000000}]
  }
  return $.post(rootUrl+"/txs/new", JSON.stringify(newtx));
}

// 2. Sign the hexadecimal strings returned with the fully built transaction and include
//    the source public address.
function signAndSend(newtx) {
  if (checkError(newtx)) return;

  newtx.pubkeys     = [];
  newtx.signatures  = newtx.tosign.map(function(tosign) {
    newtx.pubkeys.push(source.public);
    return key.sign(new buffer.Buffer(tosign, "hex")).toDER().toString("hex");
  });
  return $.post(rootUrl+"/txs/send", JSON.stringify(newtx));
}

// 3. Open a websocket to wait for confirmation the transaction has been accepted in a block.
function waitForConfirmation(finaltx) {
  if (checkError(finaltx)) return;
  log("Transaction " + finaltx.tx.hash + " to " + dest.address + " of " +
        finaltx.tx.outputs[0].value/100000000 + " BTC sent.");

  var ws = new WebSocket("ws://socket.blockcypher.com/v1/btc/test3");

  // We keep pinging on a timer to keep the websocket alive
  var ping = pinger(ws);

  ws.onmessage = function (event) {
    if (JSON.parse(event.data).confirmations > 0) {
      log("Transaction confirmed.");
      ping.stop();
      ws.close();
    }
  }
  ws.onopen = function(event) {
    ws.send(JSON.stringify({filter: "event=new-block-tx&hash="+finaltx.tx.hash}));
  }
  log("Waiting for confirmation... (may take > 10 min)")
}

function checkError(msg) {
  if (msg.errors && msg.errors.length) {
    log("Errors occured!!/n" + msg.errors.join("/n"));
    return true;
  }
}

function pinger(ws) {
  var timer = setInterval(function() {
    if (ws.readyState == 1) {
      ws.send(JSON.stringify({event: "ping"}));
    }
  }, 5000);
  return {stop: function() { clearInterval(timer); }};
}

function log(msg) {
  $("div.log").append("<div>" + msg + "</div>")
}

// Chaining
$.post(rootUrl+"/addrs")
  .then(logAddr)
  .then(newTransaction)
  .then(signAndSend)
  .then(waitForConfirmation);
