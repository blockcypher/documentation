var rootUrl = "https://api.blockcypher.com/v1/btc/test3";
var source = {
  private : "ef05a415ea4097a63d9cc285b1834272726a24c42090b83e2944f6490093e6ab1101a538e67d",
  public  : "02c8d29d8c8afb4725d25a1ec496aeddc36fa9eb6f0e014bfc21742fc3d78cb438",
  address : "mwmabpJVisvti3WEP5vhFRtn3yqHRD9KNP"
}
var dest = null;

function parseAddr(jsonAddr) {
  dest = JSON.parse(jsonAddr);
}

function newTransaction() {
  var newtx = {
    "inputs": [{"addresses": [source.address]}],
    "outputs": [{"addresses": [dest.address], "value": 25000}]
  }
  return $.post(rootUrl+"/txs/new");
}

function signAndSend(newtxjson) {
  var newtx   = JSON.parse(newtxjson);
  if (newtx.errors && newtx.errors.length) {
    console.log("Errors occured!!/n", newtx.errors.join("/n"));
    return;
  }

  var key     = Bitcoin.ECKey.fromHex(source.private);
  var pubkeys = [];
  var signatures = newtx.tosign.map(function(tosign) {
    pubkeys.push(source.public);
    return key.sign(tosign).concat(1);
  });

  newtx.signatures  = signatures;
  newtx.pubkeys     = pubkeys;
  return $.post(rootUrl+"/txs/send", JSON.stringify(newtx));
}

function print(finaltxjson) {
  var finaltx = JSON.parse(newtxjson);
  if (newtx.errors && newtx.errors.length) {
    console.log("Errors occured!!/n", newtx.errors.join("/n"));
    return;
  }
  console.log("Transaction to", dest.address, "sending", finaltx.total/100000000 , "BTC sent.");
}

$.post(rootUrl+"/addrs")
  .then(parseAddr)
  .then(newTransaction)
  .then(signAndSend)
  .then(print);
