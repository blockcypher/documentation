public class WebhookExample {

    private final Logger logger = Logger.getLogger(WebhookExample.class);

    public static void main(String[] args) throws BlockCypherException, InterruptedException {
        new WebhookExample().run();
    }

    public void run() throws BlockCypherException, InterruptedException {
        BlockCypherContext context = new BlockCypherContext("v1", "btc", "main");
        EmbeddedServer embeddedServer = new EmbeddedServer();

        Map<Class, ReaderTestInterceptor> classInterceptor = new HashMap<Class, ReaderTestInterceptor>();
        classInterceptor.put(Transaction.class, new ReaderTestInterceptor());
        classInterceptor.put(BlockChain.class, new ReaderTestInterceptor());
        classInterceptor.put(Address.class, new ReaderTestInterceptor());
        embeddedServer.start(classInterceptor);

        while (embeddedServer.getHost() == null) {
            Thread.sleep(1000);
        }

        logger.info("Webserver init with Host: " + embeddedServer.getHost());
        // ie: "3cc375e5.ngrok.com";
        String myIp = embeddedServer.getHost();
        // We will run on this port our mini server
        int myPort = embeddedServer.getPort();

        logger.info(format("Server started on: http://{0}:{1}", myIp, String.valueOf(myPort)));
        Webhook webhook = context.getWebhookService().createWebHook(format("http://{0}:{1}" +
                                AnnotationUtils.getPath(Transaction.class),
                        myIp, String.valueOf(myPort)),
                "event=" + WebhookConstants.FILTER_NEW_POOL_TX,
                null);
        logger.info("WebHook: " + GsonFactory.getGson().toJson(webhook));
        while (classInterceptor.get(Transaction.class).getReceivedEvents().isEmpty()) {
            Thread.sleep(1000);
        }
        assert false : StringUtils.isBlank(((Transaction) classInterceptor.get(Transaction.class).getReceivedEvents().get(0)).getHash());

        if (webhook != null) {
            context.getWebhookService().delete(webhook.getId());
            logger.info("Deleted Webhook: " + webhook.getId());
        }
        if (embeddedServer != null) {
            embeddedServer.destroy();
        }
    }

    public class EmbeddedServer {

        private static final int SERVER_PORT = 80;
        private final Logger logger = Logger.getLogger(EmbeddedServer.class);
        private static final String NGROK_EXEC = "ngrok";
        private Map classInterceptor;
        private String host;
        private Process processNgrok;
        private HttpServer httpServer = null;

        private HttpServer createHttpServer() throws IOException {
            ResourceConfig config = new ResourceConfig();
            config.packages(true, "com.blockcypher.model");
            config.register(new DynamicBinding());
            config.register(new WebhookJsonReader());
            return JdkHttpServerFactory.createHttpServer(getURI(), config);
        }

        private URI getURI() {
            return UriBuilder.fromUri("http://" + "0.0.0.0" + "/").port(SERVER_PORT).build();
        }

        public void start(Map<Class, ReaderTestInterceptor> classInterceptor) {
            this.classInterceptor = classInterceptor;
            logger.info("Starting Embedded Jersey HTTPServer...\n");
            try {
                httpServer = createHttpServer();
            } catch (IOException e) {
                logger.error("Error while creating http server", e);
            }
            logger.info(String.format("\nJersey Application Server started with WADL available at " + "%sapplication.wadl\n", getURI()));
            logger.info("Started Embedded Jersey HTTPServer Successfully !!!");

            logger.info("Starting ngrok");
            ProcessBuilder processNgrokBuilder = new ProcessBuilder(NGROK_EXEC, "-log=stdout", String.valueOf(SERVER_PORT));
            try {
                processNgrok = processNgrokBuilder.start();
                inheritIO(processNgrok.getInputStream());
                logger.info("ngrok started");
            } catch (IOException e) {
                logger.error("Error while starting ngrok", e);
            }
        }

        public void destroy() {
            if (processNgrok != null) {
                processNgrok.destroy();
            }
            if (httpServer != null) {
                httpServer.stop(1);
            }
        }

        public String getHost() {
            return host;
        }

        private void inheritIO(final InputStream src) {
            new Thread(new Runnable() {
                public void run() {
                    Scanner sc = new Scanner(src);
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        if (line.contains("[client] Tunnel established")) {
                            logger.info(line);
                            host = line.substring(line.lastIndexOf("/") + 1);
                            logger.info("ngrok started on host: " + host);
                        } else if (line.contains("[INFO]")) {
                            logger.info(line);
                        } else if (line.contains("[DEBG]")) {
                            //logger.debug(line);
                        } else if (line.contains("[EROR]")) {
                            logger.error(line);
                        }
                    }
                    logger.info("Stop ngrok process redirection");
                }
            }).start();
        }

        @Provider
        public class DynamicBinding implements DynamicFeature {

            @Override
            public void configure(ResourceInfo resourceInfo, FeatureContext context) {
                if (classInterceptor.containsKey(resourceInfo.getResourceClass())
                        && resourceInfo.getResourceMethod().getName().contains("post")) {
                    context.register(classInterceptor.get(resourceInfo.getResourceClass()));
                }
            }

        }

        public int getPort() {
            return SERVER_PORT;
        }

    }

    public class ReaderTestInterceptor implements ReaderInterceptor {

        private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ReaderTestInterceptor.class);

        private List receivedEvents = new ArrayList();

        @Override
        public Object aroundReadFrom(ReaderInterceptorContext readerInterceptorContext) throws IOException, WebApplicationException {
            logger.info("Coolio we received something!");
            Object returnedObject = readerInterceptorContext.proceed();
            logger.info("Received:" + returnedObject);
            receivedEvents.add(returnedObject);
            return returnedObject;
        }

        public List getReceivedEvents() {
            return receivedEvents;
        }

    }

}