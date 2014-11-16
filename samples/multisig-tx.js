var bytesToHex = Bitcoin.convert.bytesToHex;
var hexToBytes = Bitcoin.convert.hexToBytes;

var rootUrl = "https://api.blockcypher.com/v1/btc/test3";
// please do not drain our test account, if you need testnet BTC use a faucet
// https://tpfaucet.appspot.com/
var source = {
  private : "1af97b1f428ac89b7d35323ea7a68aba8cad178a04eddbbf591f65671bae48a2",
  public  : "03bb318b00de944086fad67ab78a832eb1bf26916053ecd3b14a3f48f9fbe0821f",
  address : "mtWg6ccLiZWw2Et7E5UqmHsYgrAi5wqiov"
}
var key   = Bitcoin.ECKey.fromHex(source.private);

var addrs = [];

// 0. We get 3 newly generated address
function logAddrs(dest1, dest2, dest3) {
  addrs[0] = dest1[0];
  addrs[1] = dest2[0];
  addrs[2] = dest3[0];
  log("Generated 3 new addresses for 2-of-3 multisig.");
}

// 1. Post our funding transaction, sending money from a standard address to the multisig
// address for our 3 keys.
function newFundingTransaction() {
  var newtx = {
    "inputs": [{"addresses": [source.address]}],
    "outputs": [{
      "addresses"   : [addrs[0].public, addrs[1].public, addrs[2].public],
      "script_type" : "multisig-2-of-3",
      "value"       : 25000,
    }]
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
    return bytesToHex(key.sign(hexToBytes(tosign)));
  });
  return $.post(rootUrl+"/txs/send", JSON.stringify(newtx));
}

// 3. Open a websocket to wait for confirmation the transaction has been accepted in a block.
function waitForConfirmation(finaltx) {
  if (checkError(finaltx)) return;
  console.log(finaltx);
  log("Transaction " + finaltx.tx.hash + " to address " + finaltx.tx.outputs[0].addresses[0] + " of " +
        finaltx.tx.outputs[0].value/100000000 + " BTC sent.");

  var confirmed = $.Deferred();
  var ws = new WebSocket("wss://socket.blockcypher.com/v1/btc/test3");
  // We keep pinging on a timer to keep the websocket alive
  var ping = pinger(ws);

  ws.onmessage = function (event) {
    if (JSON.parse(event.data).confirmations > 0) {
      log("Transaction confirmed.");
      confirmed.resolve();
      ping.stop();
      ws.close();
    }
  }
  ws.onopen = function(event) {
    ws.send(JSON.stringify({filter: "event=new-block-tx&hash="+finaltx.tx.hash}));
  }
  log("Waiting for confirmation... (may take > 10 min)");
  return confirmed;
}

function newTwoOfThreeTransaction() {
  var newtx = {
    "inputs": [{
      "addresses"   : [addrs[0].public, addrs[1].public, addrs[2].public],
      "script_type" : "multisig-2-of-3"
    }],
    "outputs": [{
      "addresses" : [source.address],
      "value"     : 15000
    }]
  }
  return $.post(rootUrl+"/txs/new", JSON.stringify(newtx));
}

function signForAddressAndSend(addressNum) {
  return function(newtx) {
    if (checkError(newtx)) return;
    console.log(addrs[addressNum-1]);

    var key = Bitcoin.ECKey.fromHex(addrs[addressNum-1].private);
    newtx.signatures  = newtx.tosign.map(function(tosign) {
      return bytesToHex(key.sign(hexToBytes(tosign)));
    });
    return $.post(rootUrl+"/txs/send", JSON.stringify(newtx));
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

function checkError(msg) {
  if (msg.errors && msg.errors.length) {
    log("Errors occured!!/n" + msg.errors.join("/n"));
    return true;
  }
}

function log(msg) {
  $("div.log").append("<div>" + msg + "</div>")
}

// Chaining
$.when($.post(rootUrl+"/addrs?a"), $.post(rootUrl+"/addrs?b"), $.post(rootUrl+"/addrs?c"))
  .then(logAddrs)
  // funding transaction to multisig address
  .then(newFundingTransaction)
  .then(signAndSend)
  .then(waitForConfirmation)
  // transfer signing with key #3
  .then(newTwoOfThreeTransaction)
  .then(signForAddressAndSend(3))
  // transfer signing with key #1
  .then(newTwoOfThreeTransaction)
  .then(signForAddressAndSend(1))
  // final confirmation
  .then(waitForConfirmation);
