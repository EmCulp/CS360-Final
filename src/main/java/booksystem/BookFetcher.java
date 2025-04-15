package booksystem;

import org.eclipse.jetty.util.ajax.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class BookFetcher {

    private static final Set<String> ALLOWED_GENRES = new HashSet<>(Arrays.asList(
            "Biography / Autobiography", "Comic", "Drama", "Essay", "Fable", "Fairy Tale",
            "Fantasy", "Fiction", "Narrative Nonfiction", "Poetry", "Science Fiction",
            "Speech", "Fiction in Verse", "Folklore", "Historical Fiction", "Horror",
            "Legend", "Magna", "Mystery", "mythology", "nonfiction", "realistic fiction",
            "short story", "Tall Tale"
    ));

    private static final Set<String> ALLOWED_TONES = new HashSet<>(Arrays.asList(
            "Hopeful", "Tense", "Romantic", "Dark", "Whimsical", "Mysterious", "Cynical",
            "Suspenseful", "Grim", "Reflective", "Eerie", "Dramatic", "Playful", "Sincere",
            "Uplifting", "Somber", "Gripping", "Lyrical", "Adventurous"
    ));

    private static final Set<String> ALLOWED_PACES = new HashSet<>(Arrays.asList("Fast", "Slow"));
    private static final Set<String> ALLOWED_PROTAGONISTS = new HashSet<>(Arrays.asList("Hero", "Antihero", "Protagonist", "Supporting Protagonist"));
    private static final Set<String> ALLOWED_ENDINGS = new HashSet<>(Arrays.asList("Happy", "Sad"));
    private static final Set<String> ALLOWED_ACTION_DEV = new HashSet<>(Arrays.asList("Action", "Character development"));
    private static final Set<String> ALLOWED_ROMANCE = new HashSet<>(Arrays.asList("None", "A little", "A good bit", "A lot"));
    private static final Set<String> ALLOWED_TWISTS = new HashSet<>(Arrays.asList("Twists and surprises", "Predictable"));
    private static final Set<String> ALLOWED_SUPERNATURAL = new HashSet<>(Arrays.asList("Love them", "Occasionally", "Neutral", "Realistic", "Avoid"));
    private static final Set<String> ALLOWED_SETTINGS = new HashSet<>(Arrays.asList(
            "Historical", "Big cities / Urban Life", "Futuristic / Sci-Fi Worlds", "Dystopian / Post apocalyptic",
            "Fantasy Realms", "Nature-based / Wilderness Adventure", "Modern-Day / Contemporary Settings",
            "Otherworldly / Parallel Universes", "Small towns or close-knit communities", "depends on the plot"
    ));
    private static final Set<String> ALLOWED_LENGTHS = new HashSet<>(Arrays.asList(
            "Very Short (< 150 pages)", "Short (150-300 pages)", "Medium (300-500 pages)",
            "Long (500-700 pages)", "Epic (700+ pages)", "No preference"
    ));
    private static final Set<String> ALLOWED_STYLES = new HashSet<>(Arrays.asList(
            "Descriptive & Lush", "Reflective & Thoughtful", "Concise & Straightforward",
            "Experimental or Non-Linear", "Flowery & Poetic", "Satirical or Witty", "Conversational",
            "Dark & Atmospheric", "Fast-pace & action-oriented", "No preferences"
    ));
    private static final Set<String> ALLOWED_THEMES = new HashSet<>(Arrays.asList(
            "Adventure", "Coming of Age", "Social Issues", "Love / Romance", "Historical", "Family Dynamics",
            "Friendship", "Science Fiction", "Survival", "Mystery / Thriller", "Horror", "Humor",
            "Fantasy", "Political / Philosophical", "Grief / Loss", "Redemption", "Faith / Spiritually",
            "Justice / Law", "Sports", "Teamwork & Competition", "Victory & Achievement", "Training & Dedication",
            "Inspiration from Athletes"
    ));

    private static String mapToAllowedValue(List<String> apiValues, Set<String> allowedValues) {
        Set<String> matched = new LinkedHashSet<>();

        for (String apiVal : apiValues) {
            for (String allowed : allowedValues) {
                if (apiVal.toLowerCase().contains(allowed.toLowerCase()) ||
                        allowed.toLowerCase().contains(apiVal.toLowerCase())) {
                    matched.add(allowed);
                }
            }
        }
        return matched.isEmpty() ? "NA" : String.join(" / ", matched);
    }

    private static JSONObject fetchBookData(String title, String author) throws IOException{
        String url = "https://openlibrary.org/search.json?title=" + title.replace(" ", "+") +
                "&author=" + author.replace(" ", "+");
        JSONObject json = fetchJson(url);
        if(json == null || !json.has("docs")) return null;
        JSONArray docs = json.getJSONArray("docs");
        return (docs.length() > 0) ? docs.getJSONObject(0) : null;
    }

    private static JSONObject fetchJson(String urlStr) throws IOException{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        Scanner sc = new Scanner(conn.getInputStream());
        StringBuilder sb = new StringBuilder();
        while(sc.hasNext()) sb.append(sc.nextLine());
        sc.close();
        return new JSONObject(sb.toString());
    }

    private static String escapeCsv(String text) {
        if (text == null) return "";
        if (text.contains(",") || text.contains("\"")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }

    public static void processBook(JSONObject book, FileWriter writer) throws IOException {
        try {
            String title = book.optString("title", "Unknown Title");

            String author = "Unknown";
            if (book.has("author_name")) {
                Object authorsObj = book.get("author_name");
                if (authorsObj instanceof JSONArray) {
                    JSONArray authorsArray = (JSONArray) authorsObj;
                    if (!authorsArray.isEmpty()) {
                        author = authorsArray.optString(0, "Unknown");
                    }
                } else if (authorsObj instanceof String) {
                    author = (String) authorsObj;
                }
            }

            JSONObject detailedBook = fetchBookData(title, author);
            if (detailedBook == null || !detailedBook.has("key")) {
                System.out.println("No detailed data for: " + title);
                return;
            }

            String workKey = detailedBook.getString("key"); // like "/works/OL123W"
            JSONObject workDetails = fetchJson("https://openlibrary.org" + workKey + ".json");

            List<String> allSubjects = new ArrayList<>();
            if (workDetails != null) {
                for (String field : List.of("subjects", "subject_people", "subject_places", "subject_times")) {
                    JSONArray array = workDetails.optJSONArray(field);
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            allSubjects.add(array.getString(i));
                        }
                    }
                }
            }

            String genre = mapToAllowedValue(allSubjects, ALLOWED_GENRES);
            String tone = mapToAllowedValue(allSubjects, ALLOWED_TONES);
            String pace = mapToAllowedValue(allSubjects, ALLOWED_PACES);
            String protagonist = mapToAllowedValue(allSubjects, ALLOWED_PROTAGONISTS);
            String ending = mapToAllowedValue(allSubjects, ALLOWED_ENDINGS);
            String actionDev = mapToAllowedValue(allSubjects, ALLOWED_ACTION_DEV);
            String romance = mapToAllowedValue(allSubjects, ALLOWED_ROMANCE);
            String twist = mapToAllowedValue(allSubjects, ALLOWED_TWISTS);
            String supernatural = mapToAllowedValue(allSubjects, ALLOWED_SUPERNATURAL);
            String setting = mapToAllowedValue(allSubjects, ALLOWED_SETTINGS);
            String length = mapToAllowedValue(allSubjects, ALLOWED_LENGTHS);
            String style = mapToAllowedValue(allSubjects, ALLOWED_STYLES);
            String theme = mapToAllowedValue(allSubjects, ALLOWED_THEMES);

            // Write to CSV
            writer.append(String.join(",", escapeCsv(title), escapeCsv(author), genre, tone, pace, protagonist, ending,
                    actionDev, romance, twist, supernatural, setting, length, style, theme));
            writer.append("\n");
        }catch(Exception e){
            System.err.println("Error processing a book: "+ e.getMessage());
        }
    }

    public static void main(String[] args){
        String searchQuery = "fiction";
        String apiUrl = "https://openlibrary.org/search.json?q=" + searchQuery + "&limit=100";

        try(FileWriter writer = new FileWriter("books.csv")){
            writer.write("Title,Author,Genre,Tone,Pace,Protagonist,Ending,Action/Dev,Romance,Twist,Supernatural,Setting,Length,Style,Theme\n");

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while((inputLine = in.readLine())!= null){
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();

            JSONObject response = new JSONObject(content.toString());
            JSONArray docs = response.getJSONArray("docs");

            for(int i =0; i< docs.length(); i++){
                JSONObject book = docs.getJSONObject(i);
                processBook(book, writer);
            }
            System.out.println("Fetched books and wrote to books.csv!");
        }catch(Exception e){
            System.err.println("Error fetching or writing book data: " +e.getMessage());
        }
    }
}
