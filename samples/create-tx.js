var rootUrl = "https://api.blockcypher.com/v1/btc/test3";
var source = {
  private : "05a415ea4097a63d9cc285b1834272726a24c42090b83e2944f6490093e6ab11",
  public  : "02c8d29d8c8afb4725d25a1ec496aeddc36fa9eb6f0e014bfc21742fc3d78cb438",
  address : "mwmabpJVisvti3WEP5vhFRtn3yqHRD9KNP"
}
var dest = null;

function logAddr(addr) {
  dest = addr;
  log("Generated new address " + dest.address)
}

function newTransaction() {
  var newtx = {
    "inputs": [{"addresses": [source.address]}],
    "outputs": [{"addresses": [dest.address], "value": 25000}]
  }
  return $.post(rootUrl+"/txs/new", JSON.stringify(newtx));
}

function signAndSend(newtx) {
  if (newtx.errors && newtx.errors.length) {
    log("Errors occured!!/n" + newtx.errors.join("/n"));
    return;
  }

  var key     = Bitcoin.ECKey.fromHex(source.private);
  var pubkeys = [];
  var signatures = newtx.tosign.map(function(tosign) {
    pubkeys.push(source.public);
    return Bitcoin.convert.bytesToHex(key.sign(Bitcoin.convert.hexToBytes(tosign)).concat(1));
  });

  newtx.signatures  = signatures;
  newtx.pubkeys     = pubkeys;
  return $.post(rootUrl+"/txs/send", JSON.stringify(newtx));
}

function print(finaltx) {
  if (finaltx.errors && finaltx.errors.length) {
    log("Errors occured!!/n" + newtx.errors.join("/n"));
    return;
  }
  log("Transaction hash: " + finaltx.tx.hash)
  log("Transaction to " + dest.address + " of " + finaltx.tx.outputs[0].value/100000000 + " BTC sent.");
}

function log(msg) {
  $("div.log").append("<div>" + msg + "</div>")
}

$.post(rootUrl+"/addrs")
  .then(logAddr)
  .then(newTransaction)
  .then(signAndSend)
  .then(print);
