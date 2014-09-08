public class MultiSigExample {

    private static final String MY_PRIVATE_KEY = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private static final String MY_ADDRESS = "mvYwMT3aZ5jNcRNNjv7ckxjbqMDtvQbAHz";

    private final Logger logger = Logger.getLogger(MultiSigExample.class);

    private BlockCypherContext context;

    public static void main(String[] args) {
        new MultiSigExample().run();
    }

    public void run() {
        try {
            context = new BlockCypherContext("v1", "btc", "test3");
            Address addressA = context.getAddressService().createAddress();
            logger.info(MessageFormat.format("Adress {0} created", addressA.getAddress()));
            Address addressB = context.getAddressService().createAddress();
            logger.info(MessageFormat.format("Adress {0} created", addressB.getAddress()));
            Address addressC = context.getAddressService().createAddress();
            logger.info(MessageFormat.format("Adress {0} created", addressC.getAddress()));
            logger.info("Generated 3 new addresses for 2-of-3 multisig.");

            // Noneed for this
            //fillAddress(addressA.getAddress(), addressB.getAddress(), addressC.getAddress());

            IntermediaryTransaction unsignedTransaction = context.getTransactionService()
                    .newFundingTransaction(new ArrayList<String>(Arrays.asList("mvYwMT3aZ5jNcRNNjv7ckxjbqMDtvQbAHz")),
                            new ArrayList<String>(Arrays.asList(addressA.getPublic(), addressB.getPublic(), addressC.getPublic())),
                            25000, false
                    );
            logger.info("Unsigned 1: " + unsignedTransaction);

            // Sign
            SignUtils.signWithBase58KeyWithPubKey(unsignedTransaction, MY_PRIVATE_KEY);

            // Send
            Transaction transaction = context.getTransactionService().sendTransaction(unsignedTransaction);
            logger.info(MessageFormat.format("Sent transaction {0} to address {1} of {2} BTC sent", transaction.getHash(),
                    transaction.getOutputs().get(0).getAddresses().get(0),
                    transaction.getOutputs().get(0).getValue().divide(new BigDecimal(100000000))));

            waitTransactionConfirmedWithWebsocket(transaction);
            //waitTransactionConfirmedByPullingEvery30Seconds(transaction);

            // new 2-of-3 Transaction Signed aggainst Key C
            IntermediaryTransaction unsignedTransaction2Of3C = context.getTransactionService()
                    .newFundingTransaction(new ArrayList<String>(Arrays.asList(addressA.getPublic(), addressB.getPublic(), addressC.getPublic())),
                            Arrays.asList("mvYwMT3aZ5jNcRNNjv7ckxjbqMDtvQbAHz"),
                            15000, true
                    );
            logger.info("Unsigned 2Of3 signed and sent by C: " + unsignedTransaction2Of3C);
            // BlockCypher returns the hex string, not the WIF Base 58 key
            // Signed by C
            SignUtils.signWithHexKeyNoPubKey(unsignedTransaction2Of3C, addressC.getPrivate());
            // Send
            Transaction transaction2Of3C = context.getTransactionService().sendTransaction(unsignedTransaction2Of3C);
            logger.info(MessageFormat.format("Sent transaction {0} to address {1} of {2} BTC sent", transaction2Of3C.getHash(),
                    transaction2Of3C.getOutputs().get(0).getAddresses().get(0),
                    transaction2Of3C.getOutputs().get(0).getValue().divide(new BigDecimal(100000000))));

            // new 2-of-3 Transaction Signed aggainst Key A
            IntermediaryTransaction unsignedTransaction2Of3A = context.getTransactionService()
                    .newFundingTransaction(new ArrayList<String>(Arrays.asList(addressA.getPublic(), addressB.getPublic(), addressC.getPublic())),
                            Arrays.asList("mvYwMT3aZ5jNcRNNjv7ckxjbqMDtvQbAHz"),
                            15000, true
                    );
            logger.info("Unsigned 2Of3 signed and sent by A: " + unsignedTransaction2Of3A);

            // Signed by A
            SignUtils.signWithHexKeyNoPubKey(unsignedTransaction2Of3A, addressA.getPrivate());
            Transaction transaction2Of3A = context.getTransactionService().sendTransaction(unsignedTransaction2Of3A);
            logger.info(MessageFormat.format("Sent transaction {0} to address {1} of {2} BTC sent", transaction2Of3A.getHash(),
                    transaction2Of3A.getOutputs().get(0).getAddresses().get(0),
                    transaction2Of3A.getOutputs().get(0).getValue().divide(new BigDecimal(100000000))));

            waitTransactionConfirmedWithWebsocket(transaction2Of3A);
        } catch (Exception ex) {
            logger.error("Exception while creating Transaction");
        }
    }

    private void waitTransactionConfirmedWithWebsocket(Transaction transaction) throws DeploymentException, IOException {
        WSClient wsClient = new WSClient();
        wsClient.setTxHash(transaction.getHash());

        WebSocketContainer container = null;
        container = ContainerProvider.getWebSocketContainer();
        Session session = container.connectToServer(wsClient, URI.create(MessageFormat.format(
                "wss://socket.blockcypher.com/{0}/{1}/{2}",
                context.getEndpointConfig().getVersion(),
                context.getEndpointConfig().getCurrency(),
                context.getEndpointConfig().getNetwork()
        )));
        wsClient.wait4TerminateSignal();
    }


    @ClientEndpoint
    public class WSClient {
        private CountDownLatch transactionConfirmed = new CountDownLatch(1);
        private String txHash;

        public void setTxHash(String txHash) {
            this.txHash = txHash;
        }

        private void wait4TerminateSignal() {
            try {
                transactionConfirmed.await();
            } catch (InterruptedException e) {
                logger.error("Error while await", e);
            }
        }

        @OnMessage
        public void onMessage(String message, Session session) {
            if ("{\"event\": \"pong\"}".equals(message)) {
                logger.info("Pong!");
            } else {
                logger.info("Received msg: " + message);
                Transaction transaction = GsonFactory.getGson().fromJson(message, Transaction.class);
                if (transaction.getHash().equals(txHash) && transaction.getConfirmations() > 0) {
                    logger.info(MessageFormat.format("Transaction {0} has {1} confirmation(s)",
                            transaction.getHash(), transaction.getConfirmations()));
                    transactionConfirmed.countDown();
                }
            }
        }

        @OnOpen
        public void onOpen(Session session) {
            logger.info("WebSocket opened: " + session.getId());
            final RemoteEndpoint.Basic remote = session.getBasicRemote();
            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("filter", "event=new-block-tx&hash="+ txHash);
                logger.info("Sent " + jsonObject);
                remote.sendText(jsonObject.toString());
                pingRegularly(remote);
            } catch (IOException e) {
                logger.error("onOpen", e);
            }
        }

        private void pingRegularly(RemoteEndpoint.Basic remote) {
            // Send regularly ping otherwise the websocket closes after 60 seconds
            ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
            service.scheduleAtFixedRate(new Ping(remote), 0, 20, TimeUnit.SECONDS);
        }

        @OnClose
        public void onClose(Session session) {
            logger.info("WebSocket closed: " + session.getId());
        }

        @OnError
        public void onError(Session session, Throwable t) {
            logger.error("WebSocket error: " + session.getId(), t);
        }

        private class Ping implements Runnable {
            RemoteEndpoint.Basic remote;

            public Ping(RemoteEndpoint.Basic remote) {
                this.remote = remote;
            }

            @Override
            public void run() {
                logger.info("Ping!");
                try {
                    remote.sendText("{\"event\": \"ping\"}");
                } catch (IOException e) {
                    logger.error("Error while Pinging", e);
                }
            }
        }
    }

}