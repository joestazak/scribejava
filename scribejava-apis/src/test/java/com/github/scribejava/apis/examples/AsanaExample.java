package com.github.scribejava.apis.examples;

import com.github.scribejava.apis.Asana20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class AsanaExample {

    private static final String PROTECTED_RESOURCE_URL = "https://app.asana.com/api/1.0/users/me";

    private AsanaExample() {

    }

    public static void main(String... args) throws IOException, InterruptedException, ExecutionException {
        final String apiKey = "959256988407894";
        final String apiSecret = "5e937466a22bbfb69847a6c5b1617d60";
        final String secretState = "secret" + new Random().nextInt(999_999);
        final OAuth20Service service = new ServiceBuilder(apiKey)
                .apiSecret(apiSecret)
                .callback("http://localhost:4080/profile/asana/")
                .build(Asana20.instance());
        final Scanner in = new Scanner(System.in);

        // Obtain Auth URL
        System.out.println("Fetching the Authorication URL...");
        System.out.println("Got the Authorization URL!");
        final String authorizationUrl = service.getAuthorizationUrl();
        System.out.println("Now go and authorize ScribeJava here:");
        System.out.println(authorizationUrl);
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        final String code = in.nextLine();
        System.out.println();

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        OAuth2AccessToken accessToken = service.getAccessToken(code);
        System.out.println("Got the Access Token!");
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
        System.out.println();

        System.out.println("And paste the state from server here. We have set 'secretState'='" + secretState + "'.");
        System.out.print(">>");
        final String value = in.nextLine();
        if (secretState.equals(value)) {
            System.out.println("State value does match!");
        } else {
            System.out.println("Ooops, state value does not match!");
            System.out.println("Expected = " + secretState);
            System.out.println("Got      = " + value);
            System.out.println();
        }

        System.out.println("Refreshing the Access Token...");
        accessToken = service.refreshAccessToken(accessToken.getRefreshToken());
        System.out.println("Refreshed the Access Token!");
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
        System.out.println();

        System.out.println("Now we're going to access a protected resource...");
        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        final Response response = service.execute(request);
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getCode());
        System.out.println(response.getBody());

        System.out.println();
        System.out.println("Thats it man! Go and build something awesome with ScribeJava! :)");
    }

}
