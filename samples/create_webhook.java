BlockCypherContext context = new BlockCypherContext("v1", "btc", "main");
Webhook createdWebhook = blockCypherContext.getWebhookService().createWebHook(
    format("http://{0}:{1}" + "/txs/",
    "123.456.78.90", "80"),
    "event=" + WebhookConstants.FILTER_NEW_POOL_TX,
    null);
System.out.println("WebHook: " + GsonFactory.getGson().toJson(createdWebhook));