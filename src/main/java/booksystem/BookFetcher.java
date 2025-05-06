/*******************************************************************
 * BookFetcher *
 * *
 * PROGRAMMER: [Emily] *
 * COURSE: [CS360 - Analysis/Algorithms *
 * DATE: [2025-05-06] *
 * REQUIREMENT: Final *
 * *
 * DESCRIPTION: *
 * This class fetches book data from the Open Library API based on the title and author. *
 * It processes book details, such as genre, tone, pace, protagonist type, and more, and outputs the results in CSV format. *
 * The class also handles database insertion for the fetched book details. *
 * *
 * COPYRIGHT: This code is copyright (C) 2025 Emily *
 * *
 * CREDITS: *
 * Sources used: Open Library API documentation, ChatGPT assistance *
 * *
 *******************************************************************/

package booksystem;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.util.ajax.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.crypto.Data;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

    /**********************************************************
     * METHOD: mapToAllowedValue *
     * DESCRIPTION: This method maps values from the API response to allowed values in predefined sets (genres, tones, etc.). *
     * PARAMETERS: [apiValues] - List of API values to be mapped *
     *            [allowedValues] - Set of allowed values for mapping *
     * RETURN VALUE: A string of matched allowed values, or "NA" if no match is found. *
     **********************************************************/
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

    /**********************************************************
     * METHOD: fetchBookData *
     * DESCRIPTION: This method fetches detailed book data from the Open Library API based on title and author. *
     * PARAMETERS: [title] - The title of the book *
     *            [author] - The author of the book *
     * RETURN VALUE: A JSONObject containing the book data, or null if no data is found. *
     **********************************************************/
    private static JSONObject fetchBookData(String title, String author) throws IOException{
        String url = "https://openlibrary.org/search.json?title=" + title.replace(" ", "+") +
                "&author=" + author.replace(" ", "+");
        JSONObject json = fetchJson(url);
        if(json == null || !json.has("docs")) return null;
        JSONArray docs = json.getJSONArray("docs");
        return (docs.length() > 0) ? docs.getJSONObject(0) : null;
    }

    /**********************************************************
     * METHOD: fetchJson *
     * DESCRIPTION: This method fetches JSON data from a given URL. *
     * PARAMETERS: [urlStr] - The URL to fetch data from *
     * RETURN VALUE: A JSONObject containing the fetched data. *
     **********************************************************/
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

    /**********************************************************
     * METHOD: escapeCsv *
     * DESCRIPTION: This method escapes commas and quotation marks in text for CSV format. *
     * PARAMETERS: [text] - The text to be escaped *
     * RETURN VALUE: A string suitable for CSV formatting. *
     **********************************************************/
    private static String escapeCsv(String text) {
        if (text == null) return "";
        if (text.contains(",") || text.contains("\"")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }

    /**********************************************************
     * METHOD: mapLengthByPages *
     * DESCRIPTION: This method maps the page count of a book to a predefined length category. *
     * PARAMETERS: [pages] - The number of pages in the book *
     * RETURN VALUE: A string representing the length category. *
     **********************************************************/

    private static String mapLengthByPages(int pages) {
        if (pages < 150) {
            return "Very Short (< 150 pages)";
        } else if (pages < 300) {
            return "Short (150-300 pages)";
        } else if (pages < 500) {
            return "Medium (300-500 pages)";
        } else if (pages < 700) {
            return "Long (500-700 pages)";
        } else {
            return "Epic (700+ pages)";
        }
    }


    /**********************************************************
     * METHOD: fetchFirstEdition *
     * DESCRIPTION: This method fetches the first edition of a book, if available, from the Open Library API. *
     * PARAMETERS: [workDetails] - JSONObject containing the book's work details *
     * RETURN VALUE: A JSONObject representing the first edition, or null if no edition is found. *
     **********************************************************/
    private static JSONObject fetchFirstEdition(JSONObject workDetails) throws IOException {
        if (workDetails.has("covers")) {
            JSONArray editions = workDetails.optJSONArray("covers"); // Not ideal, but OpenLibrary's `/works/` doesn't return edition keys directly.
            if (editions != null && !editions.isEmpty()) {
                String workKey = workDetails.optString("key", null);
                if (workKey != null) {
                    JSONObject workPage = fetchJson("https://openlibrary.org" + workKey + "/editions.json");
                    if (workPage != null && workPage.has("entries")) {
                        JSONArray entries = workPage.getJSONArray("entries");
                        if (!entries.isEmpty()) {
                            return entries.getJSONObject(0); // Grab first edition
                        }
                    }
                }
            }
        }
        return null;
    }



    /**********************************************************
     * METHOD: processBook *
     * DESCRIPTION: This method processes a book by fetching its detailed data, mapping its subjects to allowed values, and writing the data to a CSV file. *
     * PARAMETERS: [book] - The JSONObject containing the book data *
     *            [writer] - The FileWriter object to write the processed data to CSV *
     * RETURN VALUE: None *
     **********************************************************/
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
            String numberOfPages= "NA";
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

            String length = "NA";

            // First, try to fetch from editions
            JSONObject edition = fetchFirstEdition(workDetails);
            if (edition != null && edition.has("number_of_pages")) {
                try {
                    int pages = edition.getInt("number_of_pages");
                    length = mapLengthByPages(pages);
                } catch (Exception e) {
                    System.out.println("Failed to parse page count from edition for: " + title);
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

    /**********************************************************
     * METHOD: insertBookIntoDatabase *
     * DESCRIPTION: This method inserts a book's details into the database. The book details are extracted from the *
     * JSON object passed to the method. It handles database connection and insertion. *
     * PARAMETERS: [book] - The JSONObject containing the book's details to be inserted into the database. *
     * RETURN VALUE: None *
     **********************************************************/
    public static void insertBookIntoDatabase(JSONObject book) {
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

            String length = "NA";
            JSONObject edition = fetchFirstEdition(workDetails);
            if (edition != null && edition.has("number_of_pages")) {
                try {
                    int pages = edition.getInt("number_of_pages");
                    length = mapLengthByPages(pages);
                } catch (Exception e) {
                    System.out.println("Failed to parse page count from edition for: " + title);
                }
            }

            // Map subject tags to your allowed categories
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
            String style = mapToAllowedValue(allSubjects, ALLOWED_STYLES);
            String theme = mapToAllowedValue(allSubjects, ALLOWED_THEMES);

            // Build Book object
            Book newBook = new Book();
            newBook.setTitle(title);
            newBook.setAuthor(author);
            newBook.setGenre(genre);
            newBook.setTone(tone);
            newBook.setPace(pace);
            newBook.setProtagonist(protagonist);
            newBook.setEnding(ending);
            newBook.setActionDevelopment(actionDev);
            newBook.setRomanceLevel(romance);
            newBook.setTwists(twist);
            newBook.setSupernatural(supernatural);
            newBook.setSetting(setting);
            newBook.setLength(length);
            newBook.setWritingStyle(style);
            newBook.setThemes(theme);

            // Insert into DB using DAO
            BookDAO bookDAO = new BookDAO();
            bookDAO.addBook(newBook);

        } catch (Exception e) {
            System.err.println("Error inserting book into database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**********************************************************
     * METHOD: parseBookFromJson *
     * DESCRIPTION: This method parses a book's details from a JSON object and creates a Book object with the data. *
     * PARAMETERS: [bookJson] - The JSONObject containing the book data to be parsed. *
     * RETURN VALUE: A Book object containing the parsed book data. *
     **********************************************************/
    public static Book parseBookFromJson(JSONObject bookJson) {
        Book book = new Book();

        String title = bookJson.optString("title", "Unknown Title");

        String author = "Unknown";
        if (bookJson.has("author_name")) {
            Object authorsObj = bookJson.get("author_name");
            if (authorsObj instanceof JSONArray) {
                JSONArray authorsArray = (JSONArray) authorsObj;
                if (!authorsArray.isEmpty()) {
                    author = authorsArray.optString(0, "Unknown");
                }
            } else if (authorsObj instanceof String) {
                author = (String) authorsObj;
            }
        }

        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre("Unknown"); // default or fetched genre
        book.setTone("Unknown");
        book.setPace("Unknown");
        book.setProtagonist("Unknown");
        book.setEnding("Unknown");
        book.setActionDevelopment("Unknown");
        book.setRomanceLevel("Unknown");
        book.setTwists("Unknown");
        book.setSupernatural("Unknown");
        book.setSetting("Unknown");
        book.setLength("Unknown");
        book.setWritingStyle("Unknown");
        book.setThemes("Unknown");

        return book;
    }

    /**********************************************************
     * METHOD: searchBookByTitleAndAuthor *
     * DESCRIPTION: This method searches for a book by its title and author using the Open Library API. *
     * PARAMETERS: [title] - The title of the book to search for. *
     *            [author] - The author of the book to search for. *
     * RETURN VALUE: A JSONObject containing the search results for the book, or null if no data is found. *
     **********************************************************/
    public static JSONObject searchBookByTitleAndAuthor(String title, String author) {
        try {
            // Construct the URL-encoded query
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String encodedAuthor = URLEncoder.encode(author, StandardCharsets.UTF_8);

            String apiUrl = "https://openlibrary.org/search.json?title=" + encodedTitle + "&author=" + encodedAuthor;

            // Fetch JSON from the API
            JSONObject result = fetchJson(apiUrl);

            if (result != null && result.has("docs")) {
                JSONArray docs = result.getJSONArray("docs");
                if (!docs.isEmpty()) {
                    return docs.getJSONObject(0); // Return the first matching result
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to search book by title and author: " + e.getMessage());
        }
        return null;
    }

    /**********************************************************
     * METHOD: main *
     * DESCRIPTION: The main method that serves as the entry point for the program. It processes user input to fetch *
     * book data from the Open Library API and inserts it into the database, or performs other operations as needed. *
     * PARAMETERS: [args] - Command-line arguments. *
     * RETURN VALUE: None *
     **********************************************************/
    public static void main(String[] args){
        System.out.println("BookFetcher running");

        String file = "src/main/resources/books.csv";

        String searchQuery = "speech";
        String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
        String apiUrl = "https://openlibrary.org/search.json?q=" + encodedQuery + "&limit=100";

        File csvFile = new File(file);
        boolean isEmpty = !csvFile.exists() || csvFile.length() == 0;

        Set<String> existingTitles = new HashSet<>();

        //Loads existing titles to avoid duplicates
        if(csvFile.exists()){
            try(BufferedReader reader = new BufferedReader(new FileReader(csvFile))){
                //skip the header
                String line = reader.readLine();
                while((line = reader.readLine()) != null){
                    String[] parts = line.split(",", -1); //handle empty columns
                    if(parts.length > 0){
                        existingTitles.add(parts[0].trim().toLowerCase()); //store lowercase title
                    }
                }
            }catch(IOException e){
                System.err.println("Error reading existing books titles: " + e.getMessage());
            }
        }

        //Writes new titles
        try(FileWriter writer = new FileWriter(csvFile, true)){
            if(isEmpty){
                writer.write("Title,Author,Genre,Tone,Pace,Protagonist,Ending,Action/Dev,Romance,Twist,Supernatural,Setting,Length,Style,Theme\n");
            }

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
                String title = book.optString("title", "").trim().toLowerCase();

                if(!existingTitles.contains(title) && !title.isEmpty()){
                    processBook(book, writer);  //only writes unique books
                    existingTitles.add(title);  //adds books to avoid repeats
                }
            }

            System.out.println("Fetched books and wrote to books.csv!");
        }catch(Exception e){
            System.err.println("Error fetching or writing book data: " +e.getMessage());
        }
    }
}