// Displays the block, for now we just do an alert to show the block's height.
function showBlock(block) {
  alert(block.height);
}

// Gets the JSON data returned by the previous JQuery.get, parse it and returns a
// new promise to get the next block.
function printAndGetNext(block) {
  showBlock(block);
  return $.get(block.prev_block_url);
}

// First gets the blockchain data using JQuery.get, parses it and returns a promise to get
// the latest block. We queue several then() calls right after to get each block one after
// another and display them.
$.get("https://api.blockcypher.com/v1/btc/main").then(function(chain) {
  return $.get(chain.latest_url);
}).then(printAndGetNext).then(printAndGetNext).then(printAndGetNext);

