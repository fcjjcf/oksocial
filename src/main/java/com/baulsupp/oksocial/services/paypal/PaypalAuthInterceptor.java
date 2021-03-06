package com.baulsupp.oksocial.services.paypal;

import com.baulsupp.oksocial.authenticator.AuthInterceptor;
import com.baulsupp.oksocial.authenticator.ValidatedCredentials;
import com.baulsupp.oksocial.authenticator.oauth2.Oauth2ServiceDefinition;
import com.baulsupp.oksocial.authenticator.oauth2.Oauth2Token;
import com.baulsupp.oksocial.secrets.Secrets;
import com.google.common.collect.Sets;
import com.baulsupp.oksocial.output.OutputHandler;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * https://developer.paypal.com/docs/authentication
 */
public class PaypalAuthInterceptor implements AuthInterceptor<Oauth2Token> {
  @Override public Oauth2ServiceDefinition serviceDefinition() {
    return new Oauth2ServiceDefinition(host(), "Paypal API", shortName(),
        "https://developer.paypal.com/docs/api/",
        "https://developer.paypal.com/developer/applications/");
  }

  protected String shortName() {
    return "paypal";
  }

  protected String host() {
    return "api.paypal.com";
  }

  @Override public Response intercept(Interceptor.Chain chain, Oauth2Token credentials)
      throws IOException {
    Request request = chain.request();

    String token = credentials.accessToken;

    Request.Builder builder = request.newBuilder().addHeader("Authorization", "Bearer " + token);

    request = builder.build();

    return chain.proceed(request);
  }

  @Override public Future<Optional<ValidatedCredentials>> validate(OkHttpClient client,
      Request.Builder requestBuilder, Oauth2Token credentials) throws IOException {
    // TODO
    return CompletableFuture.completedFuture(Optional.empty());
  }

  @Override public Oauth2Token authorize(OkHttpClient client, OutputHandler outputHandler,
      List<String> authArguments) throws IOException {
    System.err.println("Authorising Paypal API");

    String clientId =
        Secrets.prompt("Paypal Client Id", "paypal.clientId", "", false);
    String clientSecret =
        Secrets.prompt("Paypal Client Secret", "paypal.clientSecret", "", true);

    return PaypalAuthFlow.login(client, host(), outputHandler, clientId, clientSecret);
  }

  @Override public Set<String> hosts() {
    return Sets.newHashSet(host());
  }
}
