BlockCypherContext context = new BlockCypherContext("v1", "btc", "main");
Info info = context.getInfoService().getInfo();
System.out.println(MessageFormat.format("Block Hash {0} Height {1}",
        info.getHash(), String.valueOf(info.getHeight())));
String previousHash = info.getPreviousHash();
for (int i = 0; i < 2; i++) {
    BlockChain blockChain = context.getBlockChainService().getBlockChain(previousHash);
    System.out.println(MessageFormat.format("Block Hash {0} Height {1}",
            blockChain.getHash(), String.valueOf(blockChain.getHeight())));
    previousHash = blockChain.getPrevBlock();
}