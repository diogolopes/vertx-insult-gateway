package io.vertx.starter;

import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.reactivex.Maybe;
import io.vertx.core.json.JsonObject;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.core.DeploymentOptions;

public class MainVerticle extends AbstractVerticle {

	// @Override
	// public void start() {
	// vertx.createHttpServer()
	// .requestHandler(req -> req.response().end("Hello, Vert.x Insult Gateway"))
	// .listen(8080);
	// }

	@Override
	public void start(Future<Void> startFuture) {

		initConfigRetriever() // (1)
				.doOnError(startFuture::fail) // (2)
				.subscribe(ar -> { // (3)
					vertx.deployVerticle(InsultGatewayVerticle.class.getName(), new DeploymentOptions().setConfig(ar));

					startFuture.complete(); // (4)
				});
	}

	private Maybe<JsonObject> initConfigRetriever() {

		// Load the default configuration from the classpath
		ConfigStoreOptions localConfig = new ConfigStoreOptions()
			      .setType("file")
			      .setFormat("json")
			      .setOptional(true)
		      .setConfig(new JsonObject().put("path", "conf/insult-config.json"));
		
		// Add the default and container config options into the ConfigRetriever
		ConfigRetrieverOptions retrieverOptions = new ConfigRetrieverOptions().addStore(localConfig);

		// Create the ConfigRetriever and return the Maybe when complete
		return ConfigRetriever.create(vertx, retrieverOptions).rxGetConfig().toMaybe();
	}

	private void indexHandler(RoutingContext routingContext) { // (3)
		HttpServerResponse response = routingContext.response(); // (4)
		response.putHeader("Content-Type", "text/html").end("Hello, Vert.x Insult Gateway!"); // (5) (6)
	}

}
