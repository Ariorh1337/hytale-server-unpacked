package com.hypixel.hytale.server.core.auth.oauth;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.EmptyExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.auth.AuthConfig;
import com.hypixel.hytale.server.core.auth.HttpResponseException;
import com.hypixel.hytale.server.core.util.ServiceHttpClientFactory;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OAuthClient {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private static final SecureRandom RANDOM = new SecureRandom();
   private final HttpClient httpClient = ServiceHttpClientFactory.create(AuthConfig.HTTP_TIMEOUT);

   public Runnable startFlow(@Nonnull OAuthBrowserFlow flow) {
      AtomicBoolean cancelled = new AtomicBoolean(false);
      CompletableFuture.runAsync(
         () -> {
            HttpServer server = null;

            try {
               String csrfState = this.generateRandomString(32);
               String codeVerifier = this.generateRandomString(64);
               String codeChallenge = this.generateCodeChallenge(codeVerifier);
               server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
               int port = server.getAddress().getPort();
               String encodedState = this.encodeStateWithPort(csrfState, port);
               String redirectUri = "https://accounts.hytale.com/consent/client";
               CompletableFuture<String> authCodeFuture = new CompletableFuture<>();
               HttpServer finalServer = server;
               String expectedState = csrfState;
               server.createContext(
                  "/",
                  exchange -> {
                     try {
                        String query = exchange.getRequestURI().getQuery();
                        String code = this.extractParam(query, "code");
                        String returnedEncodedState = this.extractParam(query, "state");
                        String response;
                        int statusCode;
                        if (returnedEncodedState == null || !returnedEncodedState.equals(expectedState)) {
                           response = buildHtmlPage(
                              false,
                              "Authentication Failed",
                              "Authentication Failed",
                              "Something went wrong during authentication. Please close this window and try again.",
                              "Invalid state parameter"
                           );
                           statusCode = 400;
                           authCodeFuture.completeExceptionally(new Exception("Invalid state"));
                        } else if (code != null && !code.isEmpty()) {
                           response = buildHtmlPage(
                              true,
                              "Authentication Successful",
                              "Authentication Successful",
                              "You have been logged in successfully. You can now close this window and return to the server.",
                              null
                           );
                           statusCode = 200;
                           authCodeFuture.complete(code);
                        } else {
                           String error = this.extractParam(query, "error");
                           String errorMsg = error != null ? error : "No code received";
                           response = buildHtmlPage(
                              false,
                              "Authentication Failed",
                              "Authentication Failed",
                              "Something went wrong during authentication. Please close this window and try again.",
                              errorMsg
                           );
                           statusCode = 400;
                           authCodeFuture.completeExceptionally(new Exception(errorMsg));
                        }

                        exchange.sendResponseHeaders(statusCode, response.length());

                        try (OutputStream os = exchange.getResponseBody()) {
                           os.write(response.getBytes(StandardCharsets.UTF_8));
                        }
                     } catch (Exception e) {
                        LOGGER.at(Level.WARNING).withCause(ex).log("Error handling OAuth callback");
                     } finally {
                        HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> finalServer.stop(0), 1L, TimeUnit.SECONDS);
                     }
                  }
               );
               server.setExecutor(null);
               server.start();
               String authUrl = this.buildAuthUrl(encodedState, codeChallenge, redirectUri);
               flow.onFlowInfo(authUrl, formatVerificationCode(codeChallenge));
               String authCode = authCodeFuture.get(5L, TimeUnit.MINUTES);
               if (cancelled.get()) {
                  flow.onFailure("Authentication cancelled");
                  return;
               }

               OAuthClient.TokenResponse oauthTokens = this.exchangeCodeForTokens(authCode, codeVerifier, redirectUri);
               if (oauthTokens != null) {
                  flow.onSuccess(oauthTokens);
                  return;
               }

               flow.onFailure("Failed to exchange authorization code for tokens");
            } catch (Exception e) {
               LOGGER.at(Level.WARNING).withCause(e).log("OAuth browser flow failed");
               if (!cancelled.get()) {
                  flow.onFailure(e.getMessage());
               }

               return;
            } finally {
               if (server != null) {
                  server.stop(0);
               }
            }
         }
      );
      return () -> cancelled.set(true);
   }

   public Runnable startFlow(OAuthDeviceFlow flow) {
      AtomicBoolean cancelled = new AtomicBoolean(false);
      CompletableFuture.runAsync(() -> {
         try {
            OAuthClient.DeviceAuthResponse deviceAuth = this.requestDeviceAuthorization();
            if (deviceAuth == null) {
               flow.onFailure("Failed to start device authorization");
               return;
            }

            flow.onFlowInfo(deviceAuth.userCode(), deviceAuth.verificationUri(), deviceAuth.verificationUriComplete(), deviceAuth.expiresIn());
            int pollInterval = Math.max(deviceAuth.interval, 15);
            long deadline = System.currentTimeMillis() + deviceAuth.expiresIn * 1000L;

            while (System.currentTimeMillis() < deadline && !cancelled.get()) {
               Thread.sleep(pollInterval * 1000L);
               OAuthClient.TokenResponse tokens = this.pollDeviceToken(deviceAuth.deviceCode);
               if (tokens != null) {
                  if (tokens.error == null) {
                     flow.onSuccess(tokens);
                     return;
                  }

                  if (!"authorization_pending".equals(tokens.error)) {
                     if (!"slow_down".equals(tokens.error)) {
                        flow.onFailure("Device authorization failed: " + tokens.error);
                        return;
                     }

                     pollInterval += 5;
                  }
               }
            }

            if (cancelled.get()) {
               flow.onFailure("Authentication cancelled");
            } else {
               flow.onFailure("Device authorization expired");
            }
         } catch (Exception e) {
            LOGGER.at(Level.WARNING).withCause(e).log("OAuth device flow failed");
            if (!cancelled.get()) {
               flow.onFailure(e.getMessage());
            }
         }
      });
      return () -> cancelled.set(true);
   }

   @Nullable
   public OAuthClient.TokenResponse refreshTokens(@Nonnull String refreshToken) throws IOException, InterruptedException {
      try {
         String body = "grant_type=refresh_token&client_id="
            + URLEncoder.encode("hytale-server", StandardCharsets.UTF_8)
            + "&refresh_token="
            + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
         HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://oauth.accounts.hytale.com/oauth2/token"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", AuthConfig.USER_AGENT)
            .POST(BodyPublishers.ofString(body))
            .build();
         HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
         int statusCode = response.statusCode();
         if (statusCode != 200) {
            LOGGER.at(Level.WARNING).log("Token refresh failed: HTTP %d - %s", (int)statusCode, response.body());
            if (!AuthConfig.isRejectedStatusCode(statusCode)) {
               throw new HttpResponseException(statusCode, response.body());
            } else {
               return null;
            }
         } else {
            return this.parseTokenResponse(response.body());
         }
      } catch (IOException e) {
         LOGGER.at(Level.WARNING).withCause(e).log("Token refresh failed (IO error)");
         throw e;
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw e;
      } catch (Exception e) {
         LOGGER.at(Level.WARNING).withCause(e).log("Token refresh failed");
         return null;
      }
   }

   private String buildAuthUrl(String state, String codeChallenge, String redirectUri) {
      return "https://oauth.accounts.hytale.com/oauth2/auth?response_type=code&client_id="
         + URLEncoder.encode("hytale-server", StandardCharsets.UTF_8)
         + "&redirect_uri="
         + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
         + "&scope="
         + URLEncoder.encode(String.join(" ", AuthConfig.SCOPES), StandardCharsets.UTF_8)
         + "&state="
         + URLEncoder.encode(state, StandardCharsets.UTF_8)
         + "&code_challenge="
         + URLEncoder.encode(codeChallenge, StandardCharsets.UTF_8)
         + "&code_challenge_method=S256";
   }

   private static String formatVerificationCode(String codeChallenge) {
      String clean = codeChallenge.replaceAll("[^A-Za-z0-9]", "");
      return clean.length() < 8 ? clean : clean.substring(0, 4) + "-" + clean.substring(4, 8);
   }

   @Nullable
   private OAuthClient.TokenResponse exchangeCodeForTokens(String code, String codeVerifier, String redirectUri) {
      try {
         String body = "grant_type=authorization_code&client_id="
            + URLEncoder.encode("hytale-server", StandardCharsets.UTF_8)
            + "&code="
            + URLEncoder.encode(code, StandardCharsets.UTF_8)
            + "&redirect_uri="
            + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
            + "&code_verifier="
            + URLEncoder.encode(codeVerifier, StandardCharsets.UTF_8);
         HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://oauth.accounts.hytale.com/oauth2/token"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", AuthConfig.USER_AGENT)
            .POST(BodyPublishers.ofString(body))
            .build();
         HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
         if (response.statusCode() != 200) {
            LOGGER.at(Level.WARNING).log("Token exchange failed: HTTP %d - %s", (int)response.statusCode(), response.body());
            return null;
         } else {
            return this.parseTokenResponse(response.body());
         }
      } catch (Exception e) {
         LOGGER.at(Level.WARNING).withCause(e).log("Token exchange failed");
         return null;
      }
   }

   @Nullable
   private OAuthClient.DeviceAuthResponse requestDeviceAuthorization() {
      try {
         String body = "client_id="
            + URLEncoder.encode("hytale-server", StandardCharsets.UTF_8)
            + "&scope="
            + URLEncoder.encode(String.join(" ", AuthConfig.SCOPES), StandardCharsets.UTF_8);
         HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://oauth.accounts.hytale.com/oauth2/device/auth"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", AuthConfig.USER_AGENT)
            .POST(BodyPublishers.ofString(body))
            .build();
         HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
         if (response.statusCode() != 200) {
            LOGGER.at(Level.WARNING).log("Device authorization request failed: HTTP %d - %s", (int)response.statusCode(), response.body());
            return null;
         } else {
            return this.parseDeviceAuthResponse(response.body());
         }
      } catch (Exception e) {
         LOGGER.at(Level.WARNING).withCause(e).log("Device authorization request failed");
         return null;
      }
   }

   @Nullable
   private OAuthClient.TokenResponse pollDeviceToken(String deviceCode) {
      try {
         String body = "grant_type=urn:ietf:params:oauth:grant-type:device_code&client_id="
            + URLEncoder.encode("hytale-server", StandardCharsets.UTF_8)
            + "&device_code="
            + URLEncoder.encode(deviceCode, StandardCharsets.UTF_8);
         HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://oauth.accounts.hytale.com/oauth2/token"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", AuthConfig.USER_AGENT)
            .POST(BodyPublishers.ofString(body))
            .build();
         HttpResponse<String> response = this.httpClient.send(request, BodyHandlers.ofString());
         if (response.statusCode() == 400) {
            return this.parseTokenResponse(response.body());
         } else if (response.statusCode() != 200) {
            LOGGER.at(Level.WARNING).log("Device token poll failed: HTTP %d - %s", (int)response.statusCode(), response.body());
            return null;
         } else {
            return this.parseTokenResponse(response.body());
         }
      } catch (Exception e) {
         LOGGER.at(Level.WARNING).withCause(e).log("Device token poll failed");
         return null;
      }
   }

   private String generateRandomString(int length) {
      byte[] bytes = new byte[length];
      RANDOM.nextBytes(bytes);
      return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, length);
   }

   private String generateCodeChallenge(String verifier) {
      try {
         MessageDigest digest = MessageDigest.getInstance("SHA-256");
         byte[] hash = digest.digest(verifier.getBytes(StandardCharsets.US_ASCII));
         return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
      } catch (Exception e) {
         throw new RuntimeException("Failed to generate code challenge", e);
      }
   }

   private String extractParam(String query, String name) {
      if (query == null) {
         return null;
      }

      Pattern pattern = Pattern.compile(name + "=([^&]*)");
      Matcher matcher = pattern.matcher(query);
      return matcher.find() ? URLDecoder.decode(matcher.group(1), StandardCharsets.UTF_8) : null;
   }

   private String encodeStateWithPort(String state, int port) {
      String json = String.format("{\"state\":\"%s\",\"port\":\"%d\"}", state, port);
      return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
   }

   private OAuthClient.TokenResponse parseTokenResponse(String json) throws IOException {
      OAuthClient.TokenResponseData data = OAuthClient.TokenResponseData.CODEC.decodeJson(new RawJsonReader(json.toCharArray()), EmptyExtraInfo.EMPTY);
      return new OAuthClient.TokenResponse(data.accessToken, data.refreshToken, data.idToken, data.error, data.expiresIn);
   }

   private OAuthClient.DeviceAuthResponse parseDeviceAuthResponse(String json) throws IOException {
      OAuthClient.DeviceAuthResponseData data = OAuthClient.DeviceAuthResponseData.CODEC
         .decodeJson(new RawJsonReader(json.toCharArray()), EmptyExtraInfo.EMPTY);
      return new OAuthClient.DeviceAuthResponse(
         data.deviceCode, data.userCode, data.verificationUri, data.verificationUriComplete, data.expiresIn, data.interval
      );
   }

   private static String buildHtmlPage(boolean success, String title, String heading, String message, @Nullable String errorDetail) {
      String detail = errorDetail != null && !errorDetail.isEmpty() ? "<div class=\"error\">" + errorDetail + "</div>" : "";
      String iconClass = success ? "icon-success" : "icon-error";
      String iconSvg = success
         ? "<polyline points=\"20 6 9 17 4 12\"></polyline>"
         : "<line x1=\"18\" y1=\"6\" x2=\"6\" y2=\"18\"></line><line x1=\"6\" y1=\"6\" x2=\"18\" y2=\"18\"></line>";
      return "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n    <meta charset=\"UTF-8\">\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n    <title>%s - Hytale</title>\n    <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n    <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n    <link href=\"https://fonts.googleapis.com/css2?family=Lexend:wght@700&family=Nunito+Sans:wght@400;700&display=swap\" rel=\"stylesheet\">\n    <style>\n        * { margin: 0; padding: 0; box-sizing: border-box; }\n        html { color-scheme: dark; background: linear-gradient(180deg, #15243A, #0F1418); min-height: 100vh; }\n        body { font-family: \"Nunito Sans\", sans-serif; color: #b7cedd; min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 20px; }\n        .card { background: rgba(0,0,0,0.4); border: 2px solid rgba(71,81,107,0.6); border-radius: 12px; padding: 48px 40px; max-width: 420px; text-align: center; }\n        .icon { width: 64px; height: 64px; margin: 0 auto 24px; border-radius: 50%%; display: flex; align-items: center; justify-content: center; }\n        .icon svg { width: 32px; height: 32px; }\n        .icon-success { background: linear-gradient(135deg, #2d5a3d, #1e3a2a); border: 2px solid #4a9d6b; }\n        .icon-success svg { color: #6fcf97; }\n        .icon-error { background: linear-gradient(135deg, #5a2d3d, #3a1e2a); border: 2px solid #c3194c; }\n        .icon-error svg { color: #ff6b8a; }\n        h1 { font-family: \"Lexend\", sans-serif; font-size: 1.5rem; text-transform: uppercase; background: linear-gradient(#f5fbff, #bfe6ff); -webkit-background-clip: text; background-clip: text; color: transparent; margin-bottom: 12px; }\n        p { line-height: 1.6; }\n        .error { background: rgba(195,25,76,0.15); border: 1px solid rgba(195,25,76,0.4); border-radius: 6px; padding: 12px; margin-top: 16px; color: #ff8fa8; font-size: 0.875rem; word-break: break-word; }\n    </style>\n</head>\n<body><div class=\"card\"><div class=\"icon %s\"><svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2.5\" stroke-linecap=\"round\" stroke-linejoin=\"round\">%s</svg></div><h1>%s</h1><p>%s</p>%s</div></body>\n</html>\n"
         .formatted(title, iconClass, iconSvg, heading, message, detail);
   }

   private static <T> KeyedCodec<T> externalKey(String key, Codec<T> codec) {
      return new KeyedCodec<>(key, codec, false, true);
   }

   public record DeviceAuthResponse(String deviceCode, String userCode, String verificationUri, String verificationUriComplete, int expiresIn, int interval) {
   }

   private static final class DeviceAuthResponseData {
      String deviceCode;
      String userCode;
      String verificationUri;
      String verificationUriComplete;
      int expiresIn = 600;
      int interval = 5;
      static final BuilderCodec<OAuthClient.DeviceAuthResponseData> CODEC = BuilderCodec.builder(
            OAuthClient.DeviceAuthResponseData.class, OAuthClient.DeviceAuthResponseData::new
         )
         .append(OAuthClient.externalKey("device_code", Codec.STRING), (r, v) -> r.deviceCode = v, r -> r.deviceCode)
         .add()
         .append(OAuthClient.externalKey("user_code", Codec.STRING), (r, v) -> r.userCode = v, r -> r.userCode)
         .add()
         .append(OAuthClient.externalKey("verification_uri", Codec.STRING), (r, v) -> r.verificationUri = v, r -> r.verificationUri)
         .add()
         .append(OAuthClient.externalKey("verification_uri_complete", Codec.STRING), (r, v) -> r.verificationUriComplete = v, r -> r.verificationUriComplete)
         .add()
         .append(OAuthClient.externalKey("expires_in", Codec.INTEGER), (r, v) -> r.expiresIn = v, r -> r.expiresIn)
         .add()
         .append(OAuthClient.externalKey("interval", Codec.INTEGER), (r, v) -> r.interval = v, r -> r.interval)
         .add()
         .build();
   }

   public record TokenResponse(@Nullable String accessToken, @Nullable String refreshToken, @Nullable String idToken, @Nullable String error, int expiresIn) {
      public boolean isSuccess() {
         return this.error == null && this.accessToken != null;
      }
   }

   private static final class TokenResponseData {
      String accessToken;
      String refreshToken;
      String idToken;
      String error;
      int expiresIn;
      static final BuilderCodec<OAuthClient.TokenResponseData> CODEC = BuilderCodec.builder(
            OAuthClient.TokenResponseData.class, OAuthClient.TokenResponseData::new
         )
         .append(OAuthClient.externalKey("access_token", Codec.STRING), (r, v) -> r.accessToken = v, r -> r.accessToken)
         .add()
         .append(OAuthClient.externalKey("refresh_token", Codec.STRING), (r, v) -> r.refreshToken = v, r -> r.refreshToken)
         .add()
         .append(OAuthClient.externalKey("id_token", Codec.STRING), (r, v) -> r.idToken = v, r -> r.idToken)
         .add()
         .append(OAuthClient.externalKey("error", Codec.STRING), (r, v) -> r.error = v, r -> r.error)
         .add()
         .append(OAuthClient.externalKey("expires_in", Codec.INTEGER), (r, v) -> r.expiresIn = v, r -> r.expiresIn)
         .add()
         .build();
   }
}
