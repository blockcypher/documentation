public class TransactionCreationWebsocketExample {

    private static final String MY_PRIVATE_KEY = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private static final String MY_ADDRESS = "mvYwMT3aZ5jNcRNNjv7ckxjbqMDtvQbAHz";

    private final Logger logger = Logger.getLogger(TransactionCreationWebsocketExample.class);

    private BlockCypherContext context;

    public static void main(String[] args) {
        new TransactionCreationWebsocketExample().run();
    }

    public void run() {
        try {
            context = new BlockCypherContext("v1", "btc", "test3");

			// Create a random address 
            Address addressA = context.getAddressService().createAddress();
            logger.info(MessageFormat.format("Generated new address {0}", addressA.getAddress()));

            IntermediaryTransaction unsignedTransaction = context.getTransactionService()
                    .newTransaction(new ArrayList<String>(Arrays.asList(MY_ADDRESS)),
                            new ArrayList<String>(Arrays.asList(addressA.getAddress())),
                            25000
                    );

            logger.info("Unsigned: " + unsignedTransaction);
			
			// Sign the transaction
            SignUtils.signWithBase58KeyWithPubKey(unsignedTransaction, MY_PRIVATE_KEY);

			// Send the transaction
            Transaction transaction = context.getTransactionService().sendTransaction(unsignedTransaction);
            logger.info(MessageFormat.format("Sent transaction {0} to address {1} of {2} BTC sent", transaction.getHash(),
                    transaction.getOutputs().get(0).getAddresses().get(0),
                    transaction.getOutputs().get(0).getValue().divide(new BigDecimal(100000000))));

			// Wait for the confirmation using Websocket
            waitTransactionConfirmedWithWebsocket(transaction);

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
