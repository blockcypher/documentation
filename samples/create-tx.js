var bytesToHex = Bitcoin.convert.bytesToHex;
var hexToBytes = Bitcoin.convert.hexToBytes;

var rootUrl = "https://api.blockcypher.com/v1/btc/test3";
// please do not drain our test account, if you need testnet BTC use a faucet
// https://tpfaucet.appspot.com/
var source = {
  private : "05a415ea4097a63d9cc285b1834272726a24c42090b83e2944f6490093e6ab11",
  public  : "02c8d29d8c8afb4725d25a1ec496aeddc36fa9eb6f0e014bfc21742fc3d78cb438",
  address : "mwmabpJVisvti3WEP5vhFRtn3yqHRD9KNP"
}
var key   = Bitcoin.ECKey.fromHex(source.private);
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
    "outputs": [{"addresses": [dest.address], "value": 25000}]
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
  log("Transaction " + finaltx.tx.hash + " to " + dest.address + " of " +
        finaltx.tx.outputs[0].value/100000000 + " BTC sent.");

  var ws = new WebSocket("ws://socket.blockcypher.com/v1/btc/test3");
  ws.onmessage = function (event) { log("Transaction confirmed."); ws.close(); }
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

function log(msg) {
  $("div.log").append("<div>" + msg + "</div>")
}

// Chaining
$.post(rootUrl+"/addrs")
  .then(logAddr)
  .then(newTransaction)
  .then(signAndSend)
  .then(waitForConfirmation);
