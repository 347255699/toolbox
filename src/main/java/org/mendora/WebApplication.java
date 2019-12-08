package org.mendora;

import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import static org.mendora.VertxApplicationInit.vertx;

public class WebApplication{
    public static void main(String[] args) {
        final Vertx vertx = vertx(false, 2);

        vertx.deployVerticle(new AbstractVerticle() {
            @Override
            public void start() throws Exception {
                final String root = PathUtil.root(WebApplication.class);
                System.setProperty("uploadDir", root + "/file-uploads");
                WebModule.builder()
                        .webRoot(root + "/html")
                        .port(8989)
                        .build()
                        .run(this.vertx, "org.mendora.route");
            }
        });
    }
}
