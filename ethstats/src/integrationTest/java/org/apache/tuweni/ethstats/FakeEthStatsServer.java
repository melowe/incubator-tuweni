/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuweni.ethstats;

import org.apache.tuweni.concurrent.AsyncCompletion;
import org.apache.tuweni.concurrent.CompletableAsyncCompletion;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;

class FakeEthStatsServer {

  private final HttpServer server;
  private final String networkInterface;
  private int port;
  private ServerWebSocket websocket;
  private List<String> results = new ArrayList<>();

  FakeEthStatsServer(Vertx vertx, String networkInterface, int port) {
    this.networkInterface = networkInterface;

    server = vertx.createHttpServer();
    server.websocketHandler(this::connect);
    CompletableAsyncCompletion compl = AsyncCompletion.incomplete();
    server.listen(port, networkInterface, result -> {
      if (port == 0) {
        FakeEthStatsServer.this.port = server.actualPort();
      } else {
        FakeEthStatsServer.this.port = port;
      }
      compl.complete();
    });
    try {
      compl.join();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  void connect(ServerWebSocket serverWebSocket) {
    this.websocket = serverWebSocket;
    websocket.accept();
    websocket.writeTextMessage("{\"emit\":[\"ready\"]}");
    websocket.handler(buffer -> {
      results.add(buffer.toString());
    });
  }

  public ServerWebSocket getWebsocket() {
    return websocket;
  }

  public String getNetworkInterface() {
    return networkInterface;
  }

  public int getPort() {
    return port;
  }

  public List<String> getResults() {
    return results;
  }

  public void waitForMessages(int numberOfMessages) throws InterruptedException {
    for (int i = 0; i < 100; i++) {
      if (getResults().size() >= numberOfMessages) {
        return;
      }
      Thread.sleep(100);
    }
  }

  public boolean messagesContain(String test) {
    for (String message : getResults()) {
      if (message.contains(test)) {
        return true;
      }
    }
    return false;
  }
}
