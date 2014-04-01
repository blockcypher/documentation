// Displays the block by rendering a div summarizing some of its most important details
function showBlock(b) {
  var total = b.total / 100000000
  $('#10-latest-blocks').before("<div>Block at "+b.height+" transferred "+ total+" in "+b.n_tx+" transactions.</div>");
}

// Gets the JSON data returned by the previous JQuery.get, parse it and returns a
// new promise to get the next block.
function printAndGetNext(data) {
  var block = JSON.parse(data);
  showBlock(block);
  return $.get(block.prev_block_url);
}

// Gets the blockchain data and parse it, returning a promise to get the latest block
var initiate = $.get("http://api.blockcypher.com/v1/btc/main").then(function(data) {
  var chain = JSON.parse(data);
  return $.get(chain.latestUrl);
});

// We continue by looping on the promise returned above and chaining 10 calls to print
// the block and get the next one.
var next = initiate;
for (var n = 0; n < 10; n++) { next = next.then(printAndGetNext); }
