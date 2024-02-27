import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class ThankyouJesus {

    static String CELTICS = "https://www.espn.com/nba/team/_/name/bos/boston-celtics";

    public static void main(String[] args) throws IOException{
        System.out.print(run(CELTICS));
    }

    public static String run(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
