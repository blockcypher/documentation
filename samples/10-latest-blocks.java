BlockCypherContext context = new BlockCypherContext("v1", "btc", "main");
Info info = context.getInfoService().getInfo();
String hash = info.getHash();
for (int i = 0; i < 10; i++) {
    BlockChain blockChain = context.getBlockChainService().getBlockChain(hash);
    System.out.println(MessageFormat.format("Block at {0} transferred {1} in {2} transactions.",
            String.valueOf(blockChain.getHeight()),
            String.valueOf(Double.parseDouble(blockChain.getTotal().toString()) / 100000000),
            String.valueOf(blockChain.getnTx())));
    hash = blockChain.getPrevBlock();
}