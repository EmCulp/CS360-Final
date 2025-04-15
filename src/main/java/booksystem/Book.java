package booksystem;

public class Book {
    private String genre;
    private String tone;
    private String pace;
    private String protagonist;
    private String ending;
    private String actionDevelopment;
    private String romanceLevel;
    private String twists;
    private String supernatural;
    private String setting;
    private String length;
    private String writingStyle;
    private String themes;

    public Book(String genre, String tone, String pace, String protagonist, String ending, String actionDevelopment,
                String romanceLevel, String twists, String supernatural, String setting, String length,
                String writingStyle, String themes) {
        this.genre = genre;
        this.tone = tone;
        this.pace = pace;
        this.protagonist = protagonist;
        this.ending = ending;
        this.actionDevelopment = actionDevelopment;
        this.romanceLevel = romanceLevel;
        this.twists = twists;
        this.supernatural = supernatural;
        this.setting = setting;
        this.length = length;
        this.writingStyle = writingStyle;
        this.themes = themes;
    }

    // Getters
    public String getGenre() {
        return genre;
    }

    public String getTone() {
        return tone;
    }

    public String getPace() {
        return pace;
    }

    public String getProtagonist() {
        return protagonist;
    }

    public String getEnding() {
        return ending;
    }

    public String getActionDevelopment() {
        return actionDevelopment;
    }

    public String getRomanceLevel() {
        return romanceLevel;
    }

    public String getTwists() {
        return twists;
    }

    public String getSupernatural() {
        return supernatural;
    }

    public String getSetting() {
        return setting;
    }

    public String getLength() {
        return length;
    }

    public String getWritingStyle() {
        return writingStyle;
    }

    public String getThemes() {
        return themes;
    }
}